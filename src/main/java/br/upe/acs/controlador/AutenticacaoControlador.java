package br.upe.acs.controlador;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.respostas.AutenticacaoResposta;
import br.upe.acs.dominio.dto.AlterarSenhaDTO;
import br.upe.acs.dominio.dto.LoginDTO;
import br.upe.acs.dominio.dto.RecuperacaoDeContaDTO;
import br.upe.acs.dominio.dto.RegistroDTO;
import br.upe.acs.servico.AutenticacaoServico;
import br.upe.acs.utils.AcsExcecao;
import br.upe.acs.utils.MensagemUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AutenticacaoControlador {

    private final AutenticacaoServico servico;
    
    private final JwtService jwtService;

    @Operation(summary = "Cadastro de usuário")
    @PostMapping("acesso/cadastro")
    public ResponseEntity<AutenticacaoResposta> cadastrarUsuario(@Valid @RequestBody RegistroDTO registro, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
        	throw new AcsExcecao(String.join("; ", bindingResult.getAllErrors().stream()
        			.map(DefaultMessageSourceResolvable::getDefaultMessage).toList()));
        }       
        return ResponseEntity.ok(servico.cadastrarUsuario(registro));
    }

    @Operation(summary = "Login de usuário")
    @PostMapping("acesso/login")
    public ResponseEntity<AutenticacaoResposta> loginUsuario(@RequestBody LoginDTO login) {
    	return ResponseEntity.ok(servico.loginUsuario(login));
    }
    
    @Operation(summary = "Verificar usuário por token")
    @PostMapping("/verificacao")
    public ResponseEntity<MensagemUtil> verificarUsuario(HttpServletRequest request,
                                              @RequestParam(value = "codigoDeVerificacao") String codigo) {
            String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
            return ResponseEntity.ok(new MensagemUtil(servico.verificarUsuario(email, codigo)));

    }
    
    @Operation(summary = "Solicitar novo código de verificação")
    @GetMapping("/verificacao/novo")
    public ResponseEntity<MensagemUtil> alterarCodigoVerificacao(HttpServletRequest request) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(new MensagemUtil(servico.alterarCodigoVerificacao(email)));

    }
    
    @Operation(summary = "Alterar senha do usuário")
    @PatchMapping("/senha")
    public ResponseEntity<?> alterarSenha(
            HttpServletRequest request,
            @RequestBody AlterarSenhaDTO alterarSenhaDTO
    ) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        servico.alterarSenha(email, alterarSenhaDTO.getSenha(), alterarSenhaDTO.getNovaSenha());
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Esquecer a senha")
    @PostMapping("acesso/senha/esquecer")
    public ResponseEntity<?> esquecerSenha(@RequestParam String email) {
        servico.esquecerSenha(email);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Alterar senha")
    @PostMapping("alterar-senha")
    public ResponseEntity<?> alterarSenha(HttpServletRequest request,
    									  @RequestBody RecuperacaoDeContaDTO recuperacaoDeContaDTO) {
        String token = request.getHeader("Authorization").substring(7);
        servico.recuperarSenha(token, recuperacaoDeContaDTO.novaSenha());
        return ResponseEntity.noContent().build();
    }
}
