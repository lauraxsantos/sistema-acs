package br.upe.acs.servico.interfaces;

import java.util.List;
import java.util.Map;

import br.upe.acs.dominio.Requisicao;

public interface IRequisicaoServico {
	 public Long adicionarRequisicao(String email);
	 
	 public String submeterRequisicao(Long requisicaoId);
	 
	 public void excluirRequisicao(Long requisicaoId, String email);
	 
	 public List<Requisicao> listarRequisicoes(); 
	 
	 public List<Requisicao> listarRequisicoesPorAluno(Long alunoId);

	 public Map<String, Object> listarRequisicoesPaginadas(int pagina, int quantidade);
	 
	 public Requisicao buscarRequisicaoPorId(Long id);
	 
	 public String arquivarRequisicao(Long id, String email);
	 
	 public String desarquivarRequisicao(Long id, String email);
	 
	 public List<Requisicao> listarRequisicoesArquivadas(String email);
}
