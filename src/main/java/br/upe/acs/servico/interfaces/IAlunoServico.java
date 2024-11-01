package br.upe.acs.servico.interfaces;

import java.util.Map;

import br.upe.acs.dominio.vo.AtividadeComplementarVO;
import br.upe.acs.dominio.vo.MinhasHorasNaAtividadeVO;

public interface IAlunoServico {

	Map<String, Object> listarRequisicoesPaginadas(String email, int pagina, int quantidade);

	AtividadeComplementarVO atividadesComplementaresAluno(String email);

	MinhasHorasNaAtividadeVO minhasHorasNaAtividade(String email, Long atividadeId);

}
