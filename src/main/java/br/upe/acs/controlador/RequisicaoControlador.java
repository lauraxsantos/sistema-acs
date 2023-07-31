package br.upe.acs.controlador;

import java.util.Map;

import br.upe.acs.config.JwtService;
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

    private final RequisicaoServico requisicaoServico;

    @Operation(summary = "Listar as requisições com paginação")
    @GetMapping("/paginacao")
    public ResponseEntity<Map<String, Object>> listarRequisicoesPaginas(@RequestParam(defaultValue = "0") int pagina,
                                                                        @RequestParam(defaultValue = "10") int quantidade) {
        return ResponseEntity.ok(servico.listarRequisicoesPaginadas(pagina, quantidade));
    }

    @Operation(summary = "Buscar requisição por id")
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarRequisicaoPorId(@PathVariable("id") Long id) {
        try {
            RequisicaoResposta requisicaoResposta = new RequisicaoResposta(servico.buscarRequisicaoPorId(id));
            return ResponseEntity.ok(requisicaoResposta);
        } catch (AcsExcecao e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Adicionar requisição")
    @PostMapping
    public ResponseEntity<?> adicionarRequisicao(HttpServletRequest request) {
        ResponseEntity<?> resposta;
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        try {
            resposta = ResponseEntity.status(201).body(requisicaoServico.adicionarRequisicao(email));
        } catch (Exception e) {
            resposta = ResponseEntity.badRequest().body(e.getMessage());
        }

        return resposta;
    }
   
    @Operation(summary = "Baixar pdf de uma requisição")
    @PostMapping("{id}/pdf")
    public ResponseEntity<?> gerarRequisicaoPDF(@PathVariable("id") Long requisicaoId) {
        ResponseEntity<?> resposta;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("requisição" + requisicaoId + ".pdf").build());
            resposta = ResponseEntity.ok().headers(headers).body(servico.gerarRequisicaoPDF(requisicaoId));
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(e.getMessage());
        }

        return resposta;
    }

    @Operation(summary = "Submissão de requisição")
    @PutMapping("/submissão/{id}")
    public ResponseEntity<?> submeterRequisicao(@PathVariable("id") Long requisicaoId) {
        ResponseEntity<?> resposta;
        try {
            resposta = ResponseEntity.ok(servico.submeterRequisicao(requisicaoId));
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(e.getMessage());
        }

        return resposta;
    }

    @Operation(summary = "excluir requisição")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirCertificado(HttpServletRequest request, @PathVariable("id") Long requisicaoId) {
        ResponseEntity<?> resposta;
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        try {
            servico.excluirRequisicao(requisicaoId, email);
            resposta = ResponseEntity.noContent().build();
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(e.getMessage());
        }

        return resposta;
    }
}
