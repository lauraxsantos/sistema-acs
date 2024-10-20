package br.upe.acs.servico;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import br.upe.acs.dominio.Atividade;
import br.upe.acs.dominio.dto.CertificadoDTO;
import br.upe.acs.dominio.enums.CertificadoStatusEnum;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.Requisicao;
import br.upe.acs.repositorio.CertificadoRepositorio;
import br.upe.acs.utils.AcsExcecao;
import br.upe.acs.utils.ConverterArquivoExcecao;
import br.upe.acs.utils.FormatoInvalidoExcecao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificadoServico {

	private final CertificadoRepositorio repositorio;

	private final RequisicaoServico requisicaoServico;

	private final AtividadeServico atividadeServico;

	public Certificado buscarCertificadoPorId(Long id) {
		return repositorio.findById(id).orElseThrow(() -> new AcsExcecao("Certificado não encontrado"));
	}

	public byte[] buscarPdfDoCertificadoPorId(Long certificadoId) {
		Certificado certificado = buscarCertificadoPorId(certificadoId);
		return certificado.getCertificado();
	}

	public Long adicionarCertificado(MultipartFile file, Long requisicaoId, String email) {
		Requisicao requisicao = requisicaoServico.buscarRequisicaoPorId(requisicaoId);

		if (!Objects.equals(file.getContentType(), "application/pdf")) {
			throw new FormatoInvalidoExcecao("É aceito somente pdf!");
		}

		if (!requisicao.getUsuario().getEmail().equals(email)) {
			throw new FormatoInvalidoExcecao("Esse id não pertence a nenhuma requisição do aluno!");
		}

		if (requisicao.getStatusRequisicao() != RequisicaoStatusEnum.RASCUNHO) {
			throw new FormatoInvalidoExcecao("Essa requisição já foi submetida e não pode anexar novos certificados!");
		}

		if (requisicao.getCertificados().size() >= 10) {
			throw new FormatoInvalidoExcecao("Essa requisição já possui muitos certificados!");
		}

		   byte[] fileBytes;
		   
	        try {
	            fileBytes = file.getBytes();
	        } catch (IOException e) {
	            throw new ConverterArquivoExcecao(e.getMessage());
	        }

		if (certificadoUnico(requisicao, fileBytes)) {
			throw new FormatoInvalidoExcecao("Essa certificado já foi cadastrado!");
		}

		Certificado certificado = new Certificado();
		certificado.setCertificado(fileBytes);
		certificado.setRequisicao(requisicao);
		certificado.setStatusCertificado(CertificadoStatusEnum.RASCUNHO);
		Certificado certificadoSalvo = repositorio.save(certificado);
		return certificadoSalvo.getId();
	}

	public void alterarCertificado(Long certificadoId, CertificadoDTO certificadoDTO, String email) {
		Certificado certificado = buscarCertificadoPorId(certificadoId);
		if (!certificado.getRequisicao().getUsuario().getEmail().equals(email)) {
			throw new AcsExcecao("Certificado não encontrado");
		}
		Atividade atividade = null;
		if (certificadoDTO.getAtividadeId() != 0) {
			atividade = atividadeServico.buscarAtividadePorId(certificadoDTO.getAtividadeId());
		}

		certificado.setTitulo(certificadoDTO.getTitulo());
		certificado.setAtividade(atividade);

		if (certificadoDTO.getDataIncial() != null) {
			certificado.setDataInicial(converterParaData(certificadoDTO.getDataIncial()));
		}

		if (certificadoDTO.getDataFinal() != null) {
			certificado.setDataFinal(converterParaData(certificadoDTO.getDataFinal()));
		}

		certificado.setCargaHoraria((certificadoDTO.getQuantidadeDeHoras()));
		repositorio.save(certificado);
	}

	public void excluirCertificado(Long certificadoId, String email) throws AcsExcecao {
		Certificado certificado = buscarCertificadoPorId(certificadoId);
		if (!certificado.getRequisicao().getUsuario().getEmail().equals(email)) {
			throw new AcsExcecao("Certificado não encontrado");
		}

		if (!certificado.getStatusCertificado().equals(CertificadoStatusEnum.RASCUNHO)) {
			throw new AcsExcecao("Um certificado já submetido não pode ser apagado!");
		}
		repositorio.deleteById(certificadoId);
	}

	private static Date converterParaData(String dataString) {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");			
		try {
			return formato.parse(dataString);
		} catch (ParseException e) {
            throw new AcsExcecao(e.getMessage());
        }
	}

	private boolean certificadoUnico(Requisicao requisicao, byte[] bytes) {
		return requisicao.getCertificados().stream()
				.anyMatch(certificado -> Arrays.equals(certificado.getCertificado(), bytes));
	}
}
