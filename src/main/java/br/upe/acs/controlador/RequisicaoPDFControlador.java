package br.upe.acs.controlador;

import br.upe.acs.servico.RequisicaoPDFCasoDeUso;
import br.upe.acs.utils.AcsExcecao;
import br.upe.acs.utils.MensagemUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/requisicao")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RequisicaoPDFControlador {

    private final RequisicaoPDFCasoDeUso casoDeUso;

    @Operation(summary = "Baixar pdf de uma requisição")
    @PostMapping("{id}/pdf")
    public ResponseEntity<?> gerarRequisicaoPDF(@PathVariable("id") Long requisicaoId) {
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
        		.filename("requisição" + requisicaoId + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(casoDeUso.gerarRequisicaoPDF(requisicaoId));
    }

}
