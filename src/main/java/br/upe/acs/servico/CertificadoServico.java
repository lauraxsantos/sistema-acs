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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificadoServico {

	private final CertificadoRepositorio repositorio;

	private final RequisicaoServico requisicaoServico;

	private final AtividadeServico atividadeServico;

	private final CertificadoDTO certificadoDTO;

	public Certificado buscarCertificadoPorId(Long id) throws AcsExcecao {
		Optional<Certificado> certificado = repositorio.findById(id);
		if (certificado.isEmpty()) {
			throw new AcsExcecao("Não existe um certificado associado a este id!");
		}

		return certificado.get();
	}

	public byte[] buscarPdfDoCertificadoPorId(Long certificadoId) throws AcsExcecao {
		Certificado certificado = buscarCertificadoPorId(certificadoId);
		return certificado.getCertificado();
	}

	public Long adicionarCertificado(MultipartFile file, Long requisicaoId, String email) throws AcsExcecao, IOException, ParseException {
		Requisicao requisicao = requisicaoServico.buscarRequisicaoPorId(requisicaoId);
		Date dataInicial = converterParaData(certificadoDTO.getDataInicial());
	    Date dataFinal = converterParaData(certificadoDTO.getDataFinal());

		if (!Objects.equals(file.getContentType(), "application/pdf")) {
			throw new AcsExcecao("É aceito somente pdf!");
		}

		if (!requisicao.getUsuario().getEmail().equals(email)) {
			throw new AcsExcecao("Esse id não pertence a nenhuma requisição do aluno!");
		}

		if (requisicao.getStatusRequisicao() != RequisicaoStatusEnum.RASCUNHO) {
			throw new AcsExcecao("Essa requisição já foi submetida e não pode anexar novos certificados!");
		}

		if (requisicao.getCertificados().size() >= 10) {
			throw new AcsExcecao("Essa requisição já possui muitos certificados!");
		}
		
		 if (dataFinal.before(dataInicial)) {
		        throw new AcsExcecao("A data final não pode ser anterior à data inicial!");
		    }

		byte[] fileBytes = file.getBytes();

		if (certificadoUnico(requisicao, fileBytes)) {
			throw new AcsExcecao("Essa certificado já foi cadastrado!");
		}

		Certificado certificado = new Certificado();
		certificado.setDataInicial(dataInicial);
	    certificado.setDataFinal(dataFinal);
		certificado.setCertificado(fileBytes);
		certificado.setRequisicao(requisicao);
		certificado.setStatusCertificado(CertificadoStatusEnum.RASCUNHO);
		Certificado certificadoSalvo = repositorio.save(certificado);
		return certificadoSalvo.getId();
	}

	public void alterarCertificado(Long certificadoId, CertificadoDTO certificadoDTO, String email) throws AcsExcecao, ParseException {
		Date dataInicial = converterParaData(certificadoDTO.getDataInicial());
	    Date dataFinal = converterParaData(certificadoDTO.getDataFinal());
		Certificado certificado = buscarCertificadoPorId(certificadoId);
		if (!certificado.getRequisicao().getUsuario().getEmail().equals(email)) {
			throw new AcsExcecao("Esse id não pertence a nenhuma certificado do aluno!");	
		}
		
		 if (dataFinal.before(dataInicial)) {
		        throw new AcsExcecao("A data final não pode ser anterior à data inicial!");
		    }
		
		Atividade atividade = null;
		if (certificadoDTO.getAtividadeId() != 0) {
			atividade = atividadeServico.buscarAtividadePorId(certificadoDTO.getAtividadeId());
		}

		certificado.setTitulo(certificadoDTO.getTitulo());
		certificado.setAtividade(atividade);

		if (certificadoDTO.getDataInicial() != null) {
			certificado.setDataInicial(converterParaData(certificadoDTO.getDataInicial()));
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
			throw new AcsExcecao("Usuário sem premissão para excluir esse certificado!");
		}

		if (!certificado.getStatusCertificado().equals(CertificadoStatusEnum.RASCUNHO)) {
			throw new AcsExcecao("Um certificado já submetido não pode ser apagado!");
		}
		repositorio.deleteById(certificadoId);
	}

	private static Date converterParaData(String dataString) throws ParseException {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		return formato.parse(dataString);
	}

	private boolean certificadoUnico(Requisicao requisicao, byte[] bytes) {
		return requisicao.getCertificados().stream()
				.anyMatch(certificado -> Arrays.equals(certificado.getCertificado(), bytes));
	}
}
