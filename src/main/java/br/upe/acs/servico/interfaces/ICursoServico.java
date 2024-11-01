package br.upe.acs.servico.interfaces;

import java.util.List;

import br.upe.acs.dominio.Curso;

public interface ICursoServico {
	
	public List<Curso> listarCursos();
	
	public Curso buscarCursoPorId(Long id);

}
