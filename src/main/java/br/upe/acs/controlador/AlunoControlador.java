package br.upe.acs.controlador;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.respostas.UsuarioResposta;
import br.upe.acs.dominio.vo.AtividadeComplementarVO;
import br.upe.acs.dominio.vo.MinhasHorasNaAtividadeVO;
import br.upe.acs.utils.AcsExcecao;
import br.upe.acs.utils.MensagemUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.upe.acs.servico.AlunoServico;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/aluno")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlunoControlador {

    private final AlunoServico servico;

    private final JwtService jwtService;

    @Operation(summary = "Listar todas as requisições do aluno")
    @GetMapping("/requisicao/paginacao")
    public  ResponseEntity<?> listarRequisicoesPaginadas(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade
    ) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(servico.listarRequisicoesPaginadas(email, pagina, quantidade));
    }

    @Operation(summary = "Carga horaria dos alunos")
    @GetMapping("/horas")
    public ResponseEntity<AtividadeComplementarVO> atividadesComplementaresAluno(HttpServletRequest request) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(servico.atividadesComplementaresAluno(email));
    }


    @Operation(summary = "Busca horas de aluno por atividade")
    @GetMapping("/horas/{atividadeId}")
    public ResponseEntity<MinhasHorasNaAtividadeVO> minhasHorasNaAtividade(
            HttpServletRequest request,
            @PathVariable("atividadeId") Long atividadeId
    ) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(servico.minhasHorasNaAtividade(email, atividadeId));
    }

}
