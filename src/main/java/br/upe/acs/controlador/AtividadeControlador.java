package br.upe.acs.controlador;

import java.util.List;
import java.util.stream.Collectors;

import br.upe.acs.utils.MensagemUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.upe.acs.controlador.respostas.AtividadeResposta;
import br.upe.acs.dominio.dto.AtividadeDTO;
import br.upe.acs.servico.AtividadeServico;
import br.upe.acs.utils.AcsExcecao;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/atividade")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AtividadeControlador {

    private final AtividadeServico servico;

    @Operation(summary = "Listar todas as atividades")
    @GetMapping
    public ResponseEntity<List<AtividadeResposta>> listarAtividades() {
        return ResponseEntity.ok(servico.listarAtividades().stream().map(AtividadeResposta::new)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Buscar atividade por id")
    @GetMapping("/{id}")
    public ResponseEntity<AtividadeResposta> buscarAtividadePorId(@PathVariable("id") Long id) throws AcsExcecao {
        return ResponseEntity.ok(new AtividadeResposta(servico.buscarAtividadePorId(id)));
    }

    @Operation(summary = "Buscar atividades por eixo")
    @GetMapping("/eixo")
    public ResponseEntity<List<AtividadeResposta>> buscarAtividadePorEixo(@RequestParam String eixo){
    	return ResponseEntity.ok(servico.buscarAtividadePorEixo(eixo).stream().map(AtividadeResposta::new).toList());
    }

    @Operation(summary = "Criar uma nova atividade")
    @PostMapping
    public ResponseEntity<AtividadeResposta> criarAtividade(@RequestBody AtividadeDTO atividade) {
        return ResponseEntity.ok(new AtividadeResposta(servico.criarAtividade(atividade)));
    }

    @Operation(summary = "Alterar atividade")
    @PutMapping("/{id}")
    public ResponseEntity<?> alterarAtividade(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @RequestBody AtividadeDTO atividadeDTO
    ) {
        return ResponseEntity.ok(new AtividadeResposta(servico.alterarAtividade(id, atividadeDTO)));
    }


    @Operation(summary = "Excluir atividade")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirAtividade(HttpServletRequest request, @PathVariable("id") Long id) {
    	servico.excluirAtividade(id);
        return ResponseEntity.noContent().build();

    }

}
