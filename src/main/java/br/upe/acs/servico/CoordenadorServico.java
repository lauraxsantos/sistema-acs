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
import br.upe.acs.dominio.enums.PerfilEnum;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;
import br.upe.acs.repositorio.RequisicaoRepositorio;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.utils.EmailUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoordenadorServico {
	
	public final UsuarioServico usuarioServico;
	
	public final RequisicaoServico requisicaoServico;
	
	public final RequisicaoRepositorio requisicaoRepositorio;
	
	public final UsuarioRepositorio usuarioRepositorio;
	
	public final EmailUtils emailServico;
	
	public List<Usuario> listarProfessores(String email){
		Usuario coordenador = usuarioServico.buscarUsuarioPorEmail(email);
		return usuarioRepositorio.findAll().stream()
				.filter(usuario -> usuario.getPerfil() == PerfilEnum.COMISSAO)
				.filter(usuario -> usuario.getCurso() == coordenador.getCurso()).toList();
	}

	public Map<String, Object> listarRequisicoesPaginadasCurso(String email, int pagina, int quantidade){
		Usuario coordenador = usuarioServico.buscarUsuarioPorEmail(email);
		List<RequisicaoSimplesResposta> requisicoesCurso = new ArrayList<>(requisicaoServico.listarRequisicoes().stream()
				.filter(requisicao -> !requisicao.isArquivada())
				.filter(requisicao -> requisicao.getCurso() == coordenador.getCurso())
				.sorted(Comparator.comparing(Requisicao::getStatusRequisicao))
				.map(RequisicaoSimplesResposta::new).toList());
		return gerarPaginacaoRequisicoes(requisicoesCurso, pagina, quantidade);
	}
	
    public void mandarRequisicaoComissao(Long requisicaoId, Long comissaoId) {
    	Usuario usuario = usuarioServico.buscarUsuarioPorId(comissaoId);
    	Requisicao requisicao = requisicaoServico.buscarRequisicaoPorId(requisicaoId);
    	
    	requisicao.getCertificados().stream()
                .forEach(certificado -> certificado.setStatusCertificado(CertificadoStatusEnum.ENCAMINHADO_COMISSAO));
    	  	
    	requisicao.setComissao(usuario);
    	requisicaoRepositorio.save(requisicao);    	
  	
    	CompletableFuture.runAsync(() -> emailServico.enviarEmailRecebimentoRequisicao(requisicao));
  	    	
  }  
}
