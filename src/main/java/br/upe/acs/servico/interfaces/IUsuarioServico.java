package br.upe.acs.servico.interfaces;

import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.enums.PerfilEnum;

public interface IUsuarioServico {
	
	public Usuario buscarUsuarioPorId(Long id);
	
	public Usuario buscarUsuarioPorEmail(String email);
	
	public void alterarDados(String email, String nomeCompleto, String telefone, Long cursoId);

	public void alterarPerfil(String email, PerfilEnum perfil);
	
	public void desativarPerfilDoUsuario(String email);

//	void mandarRequisicaoComissao(Long requisicaoId, String email);
}
