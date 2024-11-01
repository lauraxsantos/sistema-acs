package br.upe.acs.servico.interfaces;

import java.util.List;

import br.upe.acs.dominio.Atividade;
import br.upe.acs.dominio.dto.AtividadeDTO;

public interface IAtividadeServico {
	
	List<Atividade> listarAtividades();
	
	Atividade buscarAtividadePorId(Long id);
	
	List<Atividade> buscarAtividadePorEixo(String eixo);
	
	Atividade criarAtividade(AtividadeDTO atividade);

	void excluirAtividade(Long id);

	Atividade alterarAtividade(Long id, AtividadeDTO atividade);

}
