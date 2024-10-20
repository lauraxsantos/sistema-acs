package br.upe.acs.controlador;

import java.util.List;
import java.util.Map;

import br.upe.acs.config.JwtService;
import br.upe.acs.utils.MensagemUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import br.upe.acs.controlador.respostas.RequisicaoResposta;
import br.upe.acs.servico.RequisicaoServico;
import br.upe.acs.utils.AcsExcecao;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/requisicao")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RequisicaoControlador {

    private final RequisicaoServico servico;

    private final JwtService jwtService;

    @Operation(
            summary = "Listar todas as requisições",
            description = "Descrição: Através deste endpoint, pode-se visualizar todas as requisições do sistema."
    )
    @GetMapping
    public ResponseEntity<List<RequisicaoResposta>> listarRequisicoes() {
        return ResponseEntity.ok(servico.listarRequisicoes().stream().filter(requisicao -> !requisicao.isArquivada())
                .map(RequisicaoResposta::new).toList());
    }

    @Operation(
            summary = "Listar as requisições com paginação",
            description = "Descrição: Através deste endpoint, o usuário pode visualizar sua lista de requisições com paginação.\n" +
                    "Pré-condições: O usuário deve estar logado para utilizar o endpoint.\n" +
                    "Pós-condições: Caso selecione alguma requisição, o usuário é redirecionado para a tela da requisição selecionada."
    )
    @GetMapping("/paginacao")
    public ResponseEntity<Map<String, Object>> listarRequisicoesPaginas(@RequestParam(defaultValue = "0") int pagina,
                                                                        @RequestParam(defaultValue = "10") int quantidade) {
        return ResponseEntity.ok(servico.listarRequisicoesPaginadas(pagina, quantidade));
    }

    @Operation(
            summary = "Listar as requisições de um usuário específico",
            description = "Descrição: Através deste endpoint, o usuário pode visualizar sua lista de requisições.\n" +
                    "Pré-condições: O usuário deve estar logado para utilizar o endpoint.\n" +
                    "Pós-condições: Caso selecione alguma requisição, o usuário é redirecionado para a tela da requisição selecionada."
    )
    @GetMapping("/usuario/{id}")
    public ResponseEntity<?> listarRequisicoesPorAluno(@PathVariable("id") Long alunoId) {
        try {
            return ResponseEntity.ok(servico.listarRequisicoesPorAluno(alunoId).stream()
                    .filter(requisicao -> !requisicao.isArquivada()).map(RequisicaoResposta::new).toList());
        } catch (AcsExcecao e) {
            return ResponseEntity.badRequest().body(new MensagemUtil(e.getMessage()));
        }
    }

    @Operation(
            summary = "Buscar requisição por id",
            description = "Descrição: Através dest endpoint, o usuário pode visualizar uma requisição específica.\n" +
                    "Pré-condições: O usuário deve estar logado.\n" +
                    "Pós-condições: O usuário é redirecionado para a tela da requisição selecionada."
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarRequisicaoPorId(@PathVariable("id") Long id) {
        try {
            RequisicaoResposta requisicaoResposta = new RequisicaoResposta(servico.buscarRequisicaoPorId(id));
            return ResponseEntity.ok(requisicaoResposta);
        } catch (AcsExcecao e) {
            return ResponseEntity.badRequest().body(new MensagemUtil(e.getMessage()));
        }
    }

    @Operation(
            summary = "Listar requisições arquivadas",
            description = "Descrição: Através deste endpoint, o usuário pode visualizar sua lista de requisições arquivadas.\n" +
                    "Pré-condições: O usuário deve estar logado para utilizar o endpoint.\n" +
                    "Pós-condições: Caso selecione alguma requisição, o usuário é redirecionado para a tela da requisição selecionada."
    )
    @GetMapping("/arquivar")
    public ResponseEntity<?> listarRequisicoesArquivadas(HttpServletRequest request) {
        ResponseEntity<?> resposta;
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        try {
            resposta = ResponseEntity.ok(servico.listarRequisicoesArquivadas(email)
                    .stream().map(RequisicaoResposta::new).toList());
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(e.getMessage());
        }

        return resposta;
    }

    @Operation(
            summary = "Arquivar requisição",
            description = "Descrição: Através deste endpoint, o usuário pode arquivar uma requisição.\n" +
                    "Pré-condições: O usuário deve estar logado.\n" +
                    "Pós-condições: O usuário recebe uma mensagem de confirmação de requisição arquivada."
    )
    @PostMapping("/arquivar/{id}")
    public ResponseEntity<?> arquivarRequisicao(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<?> resposta;
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        try {
            resposta = ResponseEntity.ok(new MensagemUtil(servico.arquivarRequisicao(id, email)));
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(e.getMessage());
        }
        return resposta;
    }

    @Operation(
            summary = "Desarquivar requisição",
            description = "Descrição: Através deste endpoint, o usuário pode desarquivar uma requisição.\n" +
                    "Pré-condições: O usuário deve estar logado.\n" +
                    "Pós-condições: O usuário recebe uma mensagem de confirmação de requisição desarquivada."
    )
    @PostMapping("/desarquivar/{id}")
    public ResponseEntity<?> desarquivarRequisicao(@PathVariable Long id, HttpServletRequest request) {
        ResponseEntity<?> resposta;
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        try {
            resposta = ResponseEntity.ok(new MensagemUtil(servico.desarquivarRequisicao(id, email)));
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(e.getMessage());
        }

        return resposta;
    }
    
}
