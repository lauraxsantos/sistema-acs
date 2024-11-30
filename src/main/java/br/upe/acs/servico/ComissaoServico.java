package br.upe.acs.servico;

import static br.upe.acs.servico.RequisicaoServico.gerarPaginacaoRequisicoes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import br.upe.acs.controlador.respostas.RequisicaoSimplesResposta;
import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.enums.CertificadoStatusEnum;
import br.upe.acs.dominio.enums.EixoEnum;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;
import br.upe.acs.repositorio.CertificadoRepositorio;
import br.upe.acs.repositorio.RequisicaoRepositorio;
import br.upe.acs.utils.AcsExcecao;
import br.upe.acs.utils.EmailUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComissaoServico {
	
	private final UsuarioServico usuarioServico;
	
	private final RequisicaoServico requisicaoServico;
	
	private final CertificadoServico certificadoServico;
	
	private final EmailUtils emailServico;
	
	private final RequisicaoRepositorio requisicaoRepositorio;
	
	private final CertificadoRepositorio certificadoRepositorio;
	
	public Map<String, Object> listarRequisicoesPaginadas(String email, int pagina, int quantidade){
		Usuario comissao = usuarioServico.buscarUsuarioPorEmail(email);
		List<RequisicaoSimplesResposta> requisicoesComissao = new ArrayList<>(comissao.getRequisicoesComissao().stream()
				.filter(requisicao -> !requisicao.isArquivada())
				.sorted(Comparator.comparing(Requisicao::getStatusRequisicao))
				.map(RequisicaoSimplesResposta::new).toList());
		return gerarPaginacaoRequisicoes(requisicoesComissao, pagina, quantidade);
	}
	   
    public Certificado avaliarCertificado(Long certificadoId, String email, CertificadoStatusEnum status, String observacao, float cargaHoraria) {
		Usuario comissao = usuarioServico.buscarUsuarioPorEmail(email);
		
		Certificado certificado = certificadoServico.buscarCertificadoPorId(certificadoId);
		
		if(certificado.getStatusCertificado() != CertificadoStatusEnum.ENCAMINHADO_COMISSAO) {
			throw new AcsExcecao("Este certificado não pode ser avaliado");
		}

		certificado.setStatusCertificado(status);		
		Usuario aluno = certificado.getRequisicao().getUsuario();
		
		if(status == CertificadoStatusEnum.CONCLUIDO) {			
			EixoEnum eixo = certificado.getAtividade().getEixo();
			
			if (eixo == EixoEnum.ENSINO) {
				aluno.setHorasEnsino(cargaHoraria);
			} else if (eixo == EixoEnum.EXTENSAO) {
				aluno.setHorasExtensao(cargaHoraria);
			} else if (eixo == EixoEnum.GESTAO) {
				aluno.setHorasGestao(cargaHoraria);
			} else {
				aluno.setHorasPesquisa(cargaHoraria);
			}
		} else if (status == CertificadoStatusEnum.PROBLEMA) {
			certificado.setObservacao(observacao);
		}
		
		certificadoRepositorio.save(certificado);
	
		return certificado;
	}
	
    public Requisicao avaliarRequisicao(Long requisicaoId, String email, RequisicaoStatusEnum status, String observacao) {
    	Usuario comissao = usuarioServico.buscarUsuarioPorEmail(email);
    	Requisicao requisicao = requisicaoServico.buscarRequisicaoPorId(requisicaoId);
    	
    	if(requisicao.getStatusRequisicao() != RequisicaoStatusEnum.TRANSITO) {
    		throw new AcsExcecao("Esta requisição já foi avaliada");
    	}
    	
    	List<Certificado> certificadosInvalidos = requisicao.getCertificados().stream()
                .filter(certificado -> certificado.getStatusCertificado() != CertificadoStatusEnum.CONCLUIDO && certificado.getStatusCertificado() != CertificadoStatusEnum.PROBLEMA).toList();
    	
    	if(!certificadosInvalidos.isEmpty()) {
    		throw new AcsExcecao(
                    "Certificados: " + String.join( "; ", certificadosInvalidos.stream()
                            .map(certificado -> certificado.getId().toString()).toList())
                            + " não foram avaliados."
            );
    	}
    	
    	
    	requisicao.setStatusRequisicao(status);
    	requisicao.setObservacao(observacao);
    	requisicaoRepositorio.save(requisicao);
    	
        CompletableFuture.runAsync(() -> emailServico.enviarEmailAlteracaoStatusRequisicao(requisicao));
            	
		return requisicao;    	
    	
    }
    
    public List<Certificado> listarCertificadosPorAluno(Long requisicaoId){
    	Requisicao requisicao = requisicaoServico.buscarRequisicaoPorId(requisicaoId);
    	Usuario aluno = requisicao.getUsuario();
    	
    	List<Certificado> certificados = new ArrayList<Certificado>();
    	
    	for(Requisicao req : aluno.getRequisicoes()) {
    		for (Certificado cert : req.getCertificados()) {
    			certificados.add(cert);    			
    		}
    	}
    	
		return certificados.stream().filter(certificado -> certificado.getStatusCertificado() == CertificadoStatusEnum.CONCLUIDO).toList();
    	
    }

}
