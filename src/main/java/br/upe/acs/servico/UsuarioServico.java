package br.upe.acs.servico;

import br.upe.acs.controlador.respostas.CertificadoResposta;
import br.upe.acs.controlador.respostas.RequisicaoSimplesResposta;
import br.upe.acs.dominio.Curso;
import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.enums.EixoEnum;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.utils.AcsExcecao;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

import static br.upe.acs.servico.RequisicaoServico.gerarPaginacaoRequisicoes;

@Service
@RequiredArgsConstructor
public class UsuarioServico {
	
    private final UsuarioRepositorio repositorio;

	private final CursoServico cursoServico;

    public Usuario buscarUsuarioPorId(Long id) throws AcsExcecao {
    	return repositorio.findById(id).orElseThrow(() -> new AcsExcecao("Usuario não encontrado"));

    }
    
    public Usuario buscarUsuarioPorEmail(String email) throws AcsExcecao{
    	return repositorio.findByEmail(email).orElseThrow(() -> new AcsExcecao("Usuario não encontrado"));
    }


    public Map<String, Object> listarRequisicoesPorAlunoPaginadas(Long alunoId, int pagina, int quantidade) {
		Usuario usuario = buscarUsuarioPorId(alunoId);
		List<RequisicaoSimplesResposta> requisicoesAluno = new ArrayList<>(usuario.getRequisicoes().stream()
				.filter(requisicao -> requisicao.getStatusRequisicao() != RequisicaoStatusEnum.RASCUNHO)
				.sorted(Comparator.comparing(Requisicao::getDataDeSubmissao).reversed())
				.map(RequisicaoSimplesResposta::new).toList());

		return gerarPaginacaoRequisicoes(requisicoesAluno, pagina, quantidade);
	}
    public Map<String, Object> listarRequisicoesPorAlunoPaginadasEixo(Long alunoId, EixoEnum eixo, int pagina, int quantidade) {
		
    	Usuario usuario = buscarUsuarioPorId(alunoId);
		List<Requisicao> requisicoes = usuario.getRequisicoes().stream()
				.filter(requisicao -> requisicao.getStatusRequisicao() != RequisicaoStatusEnum.RASCUNHO).toList();
		
		List<Requisicao> requisicoesFiltro = new ArrayList<>();
		List<CertificadoResposta> certificados = new ArrayList<>();
		
		for (Requisicao req : requisicoes) {	
			certificados = req.getCertificados().stream()
					.filter(certificado -> certificado.getAtividade().getEixo().equals(eixo))
					.map(CertificadoResposta::new).toList();
			if(!certificados.isEmpty()) {
				requisicoesFiltro.add(req);
			}
		}
		
		List<RequisicaoSimplesResposta> requisicoesAluno = new ArrayList<>(requisicoesFiltro.stream()
				.map(RequisicaoSimplesResposta::new).toList());


		return gerarPaginacaoRequisicoes(requisicoesAluno, pagina, quantidade);
	}


	public void alterarDados(String email, String nomeCompleto, String telefone, Long cursoId) {
		Usuario usuario = buscarUsuarioPorEmail(email);
		usuario.setNomeCompleto(nomeCompleto);
		usuario.setTelefone(telefone);
		Curso curso = cursoServico.buscarCursoPorId(cursoId);
        usuario.setCurso(curso);
        repositorio.save(usuario);
	}

	public void desativarPerfilDoUsuario(String email) throws AcsExcecao {
		Usuario usuario = buscarUsuarioPorEmail(email);

		if (usuario.getRequisicoes().isEmpty()) {
			repositorio.deleteById(usuario.getId());
		} else {
			usuario.setEnabled(false);
		}
	}

}
