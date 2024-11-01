package br.upe.acs.controlador;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.respostas.ArquivoResposta;
import br.upe.acs.dominio.dto.CertificadoDTO;
import br.upe.acs.utils.MensagemUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.upe.acs.controlador.respostas.CertificadoResposta;
import br.upe.acs.servico.CertificadoServico;
import br.upe.acs.utils.AcsExcecao;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/certificado")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CertificadoControlador {

    private final CertificadoServico servico;

    private final JwtService jwtService;

    @Operation(summary = "Buscar certificado por id")
    @GetMapping("/{id}")
    public ResponseEntity<CertificadoResposta> buscarCertificadoPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(new CertificadoResposta(servico.buscarCertificadoPorId(id)));
    }

    @Operation(summary = "Buscar o arquivo do certificado por id")
    @GetMapping("/{id}/pdf")
    public ResponseEntity<ArquivoResposta> buscarPdfDoCertificadoPorId(@PathVariable("id") Long certificadoId) {
        return ResponseEntity.ok(new ArquivoResposta(servico.buscarPdfDoCertificadoPorId(certificadoId)));
    }


    @Operation(summary = "Adicionar certificado")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> adicionarCertificado(
            HttpServletRequest request,
            Long requisicaoId,
            @RequestPart(value = "certificado") MultipartFile certificado) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.status(201).body(servico.adicionarCertificado(certificado, requisicaoId, email));
    }

    @Operation(summary = "Alterar certificado")
    @PutMapping("/{id}")
    public ResponseEntity<?> alterarCertificado(
            HttpServletRequest request,
            @PathVariable("id") Long id,
            @RequestBody CertificadoDTO certificadoDTO
            ) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        servico.alterarCertificado(id, certificadoDTO, email);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Excluir certificados")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirCertificado(HttpServletRequest request, @PathVariable("id") Long certificadoId) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        servico.excluirCertificado(certificadoId, email);
        return ResponseEntity.noContent().build();
    }
}
