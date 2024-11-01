package br.upe.acs.servico.interfaces;

import br.upe.acs.controlador.respostas.AutenticacaoResposta;
import br.upe.acs.dominio.dto.LoginDTO;
import br.upe.acs.dominio.dto.RegistroDTO;

public interface IAutenticacaoServico {
	
	public AutenticacaoResposta cadastrarUsuario(RegistroDTO registro);
	
	public AutenticacaoResposta loginUsuario(LoginDTO login);
	
	public String verificarUsuario(String email, String codigoVerificacao);
	
	public String alterarCodigoVerificacao(String email);
	
	public void alterarSenha(String email, String senha, String novaSenha);
	
	public void esquecerSenha(String email);
	
	public void recuperarSenha(String token, String novaSenha); 

}
