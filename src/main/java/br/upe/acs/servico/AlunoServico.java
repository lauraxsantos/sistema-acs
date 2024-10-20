package br.upe.acs.servico;

import br.upe.acs.controlador.respostas.RequisicaoSimplesResposta;
import br.upe.acs.dominio.Atividade;
import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.vo.AtividadeComplementarVO;
import br.upe.acs.dominio.vo.MinhasHorasNaAtividadeVO;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.utils.AcsExcecao;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.*;

import static br.upe.acs.servico.RequisicaoServico.gerarPaginacaoRequisicoes;

@Service
@RequiredArgsConstructor
public class AlunoServico {

	private final UsuarioRepositorio repositorio;

	private final AtividadeServico atividadeServico;

	public Map<String, Object> listarRequisicoesPaginadas(String email, int pagina, int quantidade) {
		Usuario aluno = repositorio.findByEmail(email).orElseThrow(() -> new AcsExcecao("Email não cadastrado"));
		List<RequisicaoSimplesResposta> requisicoesAluno = new ArrayList<>(aluno.getRequisicoes().stream()
				.filter(requisicao -> !requisicao.isArquivada())
				.sorted(Comparator.comparing(Requisicao::getStatusRequisicao))
				.map(RequisicaoSimplesResposta::new).toList());
		return gerarPaginacaoRequisicoes(requisicoesAluno, pagina, quantidade);
	}

	public AtividadeComplementarVO atividadesComplementaresAluno(String email) {

		Usuario aluno = repositorio.findByEmail(email).orElseThrow(() -> new AcsExcecao("Email não cadastrado"));

		return new AtividadeComplementarVO(aluno);
	}

    public MinhasHorasNaAtividadeVO minhasHorasNaAtividade(String email, Long atividadeId) {
		Atividade atividade = atividadeServico.buscarAtividadePorId(atividadeId);
		Usuario aluno = repositorio.findByEmail(email).orElseThrow(() -> new AcsExcecao("Email não cadastrado"));

		return calcularMinhasHoras(aluno, atividade.getChMaxima());


    }

	private MinhasHorasNaAtividadeVO calcularMinhasHoras(Usuario aluno, int chMaximo) {
		float horasRacunhos = 0;
		float horasAndamento = 0;
		float horasAceitas = 0;
		float horasComProblemas = 0;
		for (Requisicao requisicao: aluno.getRequisicoes()) {
			for (Certificado certificado: requisicao.getCertificados()) {
				switch (certificado.getStatusCertificado()) {
					case RASCUNHO -> horasRacunhos += certificado.getCargaHoraria();
					case PROBLEMA -> horasComProblemas += certificado.getCargaHoraria();
					case CONCLUIDO -> horasAceitas += certificado.getCargaHoraria();
					default -> horasAndamento += certificado.getCargaHoraria();
				}
			}
		}

		float horasRestantes = chMaximo - (horasAceitas + horasAndamento + horasRacunhos + horasComProblemas);

		return new MinhasHorasNaAtividadeVO(horasAceitas, horasAndamento, horasRacunhos, horasComProblemas, horasRestantes);
	}
}
