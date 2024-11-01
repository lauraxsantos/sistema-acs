package br.upe.acs.servico.interfaces;

import java.util.Map;

import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.enums.EixoEnum;

public interface IUsuarioServico {
	
	public Usuario buscarUsuarioPorId(Long id);
	
	public Usuario buscarUsuarioPorEmail(String email);
	
	public Map<String, Object> listarRequisicoesPorAlunoPaginadas(Long alunoId, int pagina, int quantidade);

	public Map<String, Object> listarRequisicoesPorAlunoPaginadasEixo(Long alunoId, EixoEnum eixo, int pagina, int quantidade);
	
	public void alterarDados(String email, String nomeCompleto, String telefone, Long cursoId);

	public void desativarPerfilDoUsuario(String email);
}
