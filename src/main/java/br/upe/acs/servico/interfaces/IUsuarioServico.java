package br.upe.acs.servico.interfaces;

import br.upe.acs.dominio.Usuario;

public interface IUsuarioServico {
	
	public Usuario buscarUsuarioPorId(Long id);
	
	public Usuario buscarUsuarioPorEmail(String email);
	
	public void alterarDados(String email, String nomeCompleto, String telefone, Long cursoId);

	public void desativarPerfilDoUsuario(String email);
}
