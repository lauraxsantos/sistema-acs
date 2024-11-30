package br.upe.acs.controlador;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.respostas.CertificadoResposta;
import br.upe.acs.controlador.respostas.RequisicaoResposta;
import br.upe.acs.dominio.enums.CertificadoStatusEnum;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;
import br.upe.acs.servico.ComissaoServico;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/comissao")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComissaoControlador {
	
	private final ComissaoServico servico;

    private final JwtService jwtService;
	
    @Operation(summary = "Listar todas as requisicoes")
    @GetMapping
    public  ResponseEntity<?> listarRequisicoesPaginadas(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade
    ) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(servico.listarRequisicoesPaginadas(email, pagina, quantidade));
    }
    
    @Operation(summary = "Avaliar Certificados")
    @PostMapping("/certificados")
    public ResponseEntity<?> avaliarCertificados(
    		HttpServletRequest request, 
    		@RequestParam Long certificadoId, 
    		@RequestParam CertificadoStatusEnum status, 
    		@RequestParam(defaultValue = "") String observacao,
    		@RequestParam float cargaHoraria) 
    {
    	String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
    	return ResponseEntity.ok(new CertificadoResposta(servico.avaliarCertificado(certificadoId, email, status, observacao, cargaHoraria))); 	
    }
    
    @Operation(summary = "Avaliar Requisição")
    @PostMapping("/requisicao")
    public ResponseEntity<RequisicaoResposta> avaliarRequisicao(
    		HttpServletRequest request, 
    		@RequestParam Long requisicaoId, 
    		@RequestParam RequisicaoStatusEnum status, 
    		@RequestParam(defaultValue = "") String observacao) 
    {
    	String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
    	return ResponseEntity.ok(new RequisicaoResposta(servico.avaliarRequisicao(requisicaoId, email, status, observacao)));    	
    }
    
    @Operation(summary = "Listar Certificados Aprovados do Aluno")    
    @GetMapping("/aluno/certificados")
    public ResponseEntity<List<CertificadoResposta>> listarCertificadosPorAluno(@RequestParam Long requisicaoId){
    	return ResponseEntity.ok(servico.listarCertificadosPorAluno(requisicaoId).stream().map(CertificadoResposta::new).toList());
    }

}
