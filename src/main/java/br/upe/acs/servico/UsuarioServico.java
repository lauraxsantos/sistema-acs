package br.upe.acs.servico;


import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.Curso;
import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.enums.CertificadoStatusEnum;
import br.upe.acs.dominio.enums.EixoEnum;
import br.upe.acs.dominio.enums.PerfilEnum;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;
import br.upe.acs.repositorio.RequisicaoRepositorio;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.servico.interfaces.IUsuarioServico;
import br.upe.acs.utils.AcsExcecao;
import br.upe.acs.utils.EmailUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioServico implements IUsuarioServico {
	
    private final UsuarioRepositorio repositorio;

	private final CursoServico cursoServico;

	@Override
    public Usuario buscarUsuarioPorId(Long id){
    	return repositorio.findById(id).orElseThrow(() -> new AcsExcecao("Usuario não encontrado"));

    }
    
    @Override
    public Usuario buscarUsuarioPorEmail(String email){
    	return repositorio.findByEmail(email).orElseThrow(() -> new AcsExcecao("Usuario não encontrado"));
    }


    @Override
	public void alterarDados(String email, String nomeCompleto, String telefone, Long cursoId) {
		Usuario usuario = buscarUsuarioPorEmail(email);
		usuario.setNomeCompleto(nomeCompleto);
		usuario.setTelefone(telefone);
		Curso curso = cursoServico.buscarCursoPorId(cursoId);
        usuario.setCurso(curso);
        repositorio.save(usuario);
	}
    
	
    @Override
    public void alterarPerfil(String email, PerfilEnum perfil) {
    	Usuario usuario = buscarUsuarioPorEmail(email);
    	usuario.setPerfil(perfil);
    	repositorio.save(usuario);
    	
    }
	    
	@Override
	public void desativarPerfilDoUsuario(String email) {
		Usuario usuario = buscarUsuarioPorEmail(email);

		if (usuario.getRequisicoes().isEmpty()) {
			repositorio.deleteById(usuario.getId());
		} else {
			usuario.setEnabled(false);
		}
	}


}
