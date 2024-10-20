package br.upe.acs.servico;

import br.upe.acs.dominio.Atividade;
import br.upe.acs.dominio.dto.AtividadeDTO;
import br.upe.acs.dominio.enums.EixoEnum;
import br.upe.acs.repositorio.AtividadeRepositorio;
import br.upe.acs.utils.AcsExcecao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AtividadeServico {

	private final AtividadeRepositorio repositorio;

	public List<Atividade> listarAtividades() {
		return repositorio.findAll();
	}

	public Atividade buscarAtividadePorId(Long id){
		return repositorio.findById(id).orElseThrow(() -> new AcsExcecao("Atividade não encontrada"));
	}

	public List<Atividade> buscarAtividadePorEixo(String eixo){
		boolean existe = false;
		EixoEnum eixoFormato = null;
		for(EixoEnum c : EixoEnum.values()) {
			if(c.toString().equalsIgnoreCase(eixo)) {
				existe = true;
				eixoFormato = c;
			};
		}
		if(!existe) {
			throw new AcsExcecao("Não existe um eixo correspondente");
		}

		List<Atividade> atividade = repositorio.findByEixo(eixoFormato);

		if(atividade.isEmpty()) {
			throw new AcsExcecao("Não existe atividade associada a esse eixo");
		}

		return atividade;
	}

	public Atividade criarAtividade(AtividadeDTO atividade) {
		Atividade atividadeNova = new Atividade();
		atividadeNova.setEixo(atividade.getEixo());
		atividadeNova.setDescricao(atividade.getDescricao());
		atividadeNova.setCriteriosParaAvaliacao(atividade.getCriteriosParaAvaliacao());
		atividadeNova.setChMaxima(atividade.getChMaxima());
		atividadeNova.setChPorCertificado(atividade.getChPorCertificado());
        repositorio.save(atividadeNova);
		return atividadeNova;
	}

	public void excluirAtividade(Long id){
		repositorio.findById(id).orElseThrow(() -> new AcsExcecao("Atividade não encontrada"));
        repositorio.deleteById(id);
    }

    public Atividade alterarAtividade(Long id, AtividadeDTO atividade){
        Atividade atividadeAtualizada = repositorio.findById(id).orElseThrow(() -> new AcsExcecao("Atividade não encontrada"));


        atividadeAtualizada.setEixo(atividade.getEixo());
        atividadeAtualizada.setDescricao(atividade.getDescricao());
        atividadeAtualizada.setCriteriosParaAvaliacao(atividade.getCriteriosParaAvaliacao());
		atividadeAtualizada.setChMaxima(atividade.getChMaxima());
        atividadeAtualizada.setChPorCertificado(atividade.getChPorCertificado());
        repositorio.save(atividadeAtualizada);
        return atividadeAtualizada;
    }
}
