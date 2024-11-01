package br.upe.acs.servico;

import java.util.List;

import org.springframework.stereotype.Service;

import br.upe.acs.dominio.Curso;
import br.upe.acs.repositorio.CursoRepositorio;
import br.upe.acs.servico.interfaces.ICursoServico;
import br.upe.acs.utils.AcsExcecao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoServico implements ICursoServico {
	
	private final CursoRepositorio repositorio;
	
	@Override
	public List<Curso> listarCursos() {
		return repositorio.findAll();
	}
	
	@Override
	public Curso buscarCursoPorId(Long id) {
		return repositorio.findById(id).orElseThrow(() -> new AcsExcecao("Curso não encontrado"));
	}
}
