package br.upe.acs.controlador;

import br.upe.acs.controlador.respostas.ViaCepResposta;
import br.upe.acs.servico.EnderecoServico;
import br.upe.acs.utils.MensagemUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/endereco")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EnderecoControlador {

    private final EnderecoServico servico;
    @Operation(summary = "Buscar endereço por CEP")
    @GetMapping("/{cep}")
    public ResponseEntity<?> buscarEnderecoPorCep(@PathVariable String cep) {
    	return ResponseEntity.ok(new ViaCepResposta(servico.buscarEnderecoPorCep(cep)));
    }
}
