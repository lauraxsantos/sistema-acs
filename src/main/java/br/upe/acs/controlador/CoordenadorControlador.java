package br.upe.acs.controlador;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.respostas.UsuarioResposta;
import br.upe.acs.servico.CoordenadorServico;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/coordenador")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CoordenadorControlador {
	
	public final JwtService jwtService;
	
	public final CoordenadorServico servico;
	
	@Operation(summary = "Listar requisicoes sem comissao por curso")
    @GetMapping
    public  ResponseEntity<Map<String,Object>> listarRequisicoesPaginadas(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade
    ) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(servico.listarRequisicoesPaginadasCurso(email, pagina, quantidade));
    }
	
	@Operation(summary = "Listar requisicoes com comissao por curso")
    @GetMapping("/requisicoes/comissao")
    public  ResponseEntity<Map<String,Object>> listarRequisicoesPaginadasComissao(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade
    ) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(servico.listarRequisicoesComissao(email, pagina, quantidade));
    }
	
	@Operation(summary = "Listar comissão por curso")
    @GetMapping("/comissao")
    public  ResponseEntity<List<UsuarioResposta>> listarComissao(
            HttpServletRequest request
    ) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(servico.listarProfessores(email).stream().map(UsuarioResposta::new).toList());
    }
	
	
    @Operation(summary = "Mandar Requisição")
    @PostMapping("/requisicao")
    public ResponseEntity<?> mandarRequisicao(
    		@RequestParam Long comissaoId, 
    		@RequestParam Long requisicaoId) 
    {;
    	servico.mandarRequisicaoComissao(requisicaoId, comissaoId);
    	return ResponseEntity.noContent().build();    	
    }
    
    

}
