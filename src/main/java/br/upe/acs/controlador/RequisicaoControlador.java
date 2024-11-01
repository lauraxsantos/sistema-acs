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

    @Operation(summary = "Listar todas as requisições")
    @GetMapping
    public ResponseEntity<List<RequisicaoResposta>> listarRequisicoes() {
        return ResponseEntity.ok(servico.listarRequisicoes().stream().filter(requisicao -> !requisicao.isArquivada())
                .map(RequisicaoResposta::new).toList());
    }

    @Operation(summary = "Listar as requisições com paginação")
    @GetMapping("/paginacao")
    public ResponseEntity<Map<String, Object>> listarRequisicoesPaginas(@RequestParam(defaultValue = "0") int pagina,
                                                                        @RequestParam(defaultValue = "10") int quantidade) {
        return ResponseEntity.ok(servico.listarRequisicoesPaginadas(pagina, quantidade));
    }

    @Operation(summary = "Listar as requisições de um usuário específico")
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<RequisicaoResposta>> listarRequisicoesPorAluno(@PathVariable("id") Long alunoId) {
    	return ResponseEntity.ok(servico.listarRequisicoesPorAluno(alunoId).stream().filter(requisicao -> !requisicao.isArquivada()).map(RequisicaoResposta::new).toList());
    }

    @Operation(summary = "Buscar requisição por id")
    @GetMapping("/{id}")
    public ResponseEntity<RequisicaoResposta> buscarRequisicaoPorId(@PathVariable("id") Long id) {
    	return ResponseEntity.ok(new RequisicaoResposta(servico.buscarRequisicaoPorId(id)));
    }

    @Operation(summary = "Listar requisições arquivadas")
    @GetMapping("/arquivar")
    public ResponseEntity<List<RequisicaoResposta>> listarRequisicoesArquivadas(HttpServletRequest request) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(servico.listarRequisicoesArquivadas(email)
        		.stream().map(RequisicaoResposta::new).toList());
    }

    @Operation(summary = "Arquivar requisição")
    @PostMapping("/arquivar/{id}")
    public ResponseEntity<MensagemUtil> arquivarRequisicao(@PathVariable Long id, HttpServletRequest request) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(new MensagemUtil(servico.arquivarRequisicao(id, email)));
    }

    @Operation(summary = "Desarquivar requisição")
    @PostMapping("/desarquivar/{id}")
    public ResponseEntity<?> desarquivarRequisicao(@PathVariable Long id, HttpServletRequest request) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(new MensagemUtil(servico.desarquivarRequisicao(id, email)));
    }
    
    @Operation(summary = "Adicionar requisição")
    @PostMapping
    public ResponseEntity<?> adicionarRequisicao(HttpServletRequest request) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.status(201).body(servico.adicionarRequisicao(email));

    }

    @Operation(summary = "Submissão de requisição")
    @PutMapping("/submissão/{id}")
    public ResponseEntity<MensagemUtil> submeterRequisicao(@PathVariable("id") Long requisicaoId) {
        return ResponseEntity.ok(new MensagemUtil(servico.submeterRequisicao(requisicaoId)));
    }

    @Operation(summary = "Excluir requisição")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirRequisicao(HttpServletRequest request, @PathVariable("id") Long requisicaoId) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        servico.excluirRequisicao(requisicaoId, email);
        return ResponseEntity.noContent().build();
    }
}
