package br.upe.acs.controlador;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.respostas.UsuarioResposta;
import br.upe.acs.dominio.dto.AlterarSenhaDTO;
import br.upe.acs.dominio.enums.EixoEnum;
import br.upe.acs.servico.UsuarioServico;
import br.upe.acs.utils.AcsExcecao;
import br.upe.acs.utils.MensagemUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/usuario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioControlador {

    private final UsuarioServico servico;
    
    private final JwtService jwtService;
    
    @Operation(summary = "Buscar usuário por id")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResposta> buscarUsuarioPorId(@PathVariable("id") Long id){
    	return ResponseEntity.ok(new UsuarioResposta(servico.buscarUsuarioPorId(id)));
    }
    
    @Operation(summary = "Listar requisicões do aluno páginada")
    @GetMapping("/requisicao/paginacao")
    public  ResponseEntity<?> listarRequisicaoPorAlunoPaginacao(
            @RequestParam Long alunoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade
    ) {
        return ResponseEntity.ok(servico.listarRequisicoesPorAlunoPaginadas(alunoId, pagina, quantidade));
    }
    
    @Operation(summary = "Listar requisicões do aluno páginada por eixo")
    @GetMapping("/requisicao/eixo")
    public  ResponseEntity<?> listarRequisicaoPorAlunoPaginacaoEixo(
            @RequestParam Long alunoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade,
            @RequestParam EixoEnum eixo
    ) {
        return ResponseEntity.ok(servico.listarRequisicoesPorAlunoPaginadasEixo(alunoId, eixo, pagina, quantidade));

    }

    @Operation(summary = "Retornar dados de perfil do usuário")
    @GetMapping("/me")
    public ResponseEntity<?> retornarPerfilDoUsuario(HttpServletRequest request) {
    	String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        return ResponseEntity.ok(new UsuarioResposta(servico.buscarUsuarioPorEmail(email)));
    }


    @Operation(summary = "Alterar informações de cadastro")
    @PutMapping("/informacoes")
    public ResponseEntity<?> alterarInformacoes(
            HttpServletRequest request,
            @RequestParam String nomeCompleto,
            @RequestParam String telefone,
            @RequestParam Long cursoId
    ) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        servico.alterarDados(email, nomeCompleto, telefone, cursoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desativar meu Perfil")
    @DeleteMapping
    public ResponseEntity<?> desativarPerfilDoUsuário(HttpServletRequest request) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        servico.desativarPerfilDoUsuario(email);
        return ResponseEntity.noContent().build();

    }

}
