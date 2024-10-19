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
    
    @Operation(
            summary = "Buscar usuário por id",
            description = "Esta rota permite buscar um usuário via seu id. As informações retornadas incluem " +
                    "informações como id, nome completo, endereço, número de matricula, telefone, email, perfis, curso, " +
                    "periodo e se é verificado. Essa rota séra util para gerenciamento de usuarios."
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarUsuarioPorId(@PathVariable("id") Long id){
    	ResponseEntity<?> resposta;
    	try {
    		UsuarioResposta usuarioResposta = new UsuarioResposta(servico.buscarUsuarioPorId(id));
    		resposta = ResponseEntity.ok(usuarioResposta);
    	} catch(AcsExcecao e){
    		resposta = ResponseEntity.badRequest().body(new MensagemUtil(e.getMessage()));
    	}
    	
    	return resposta;
    }
    
    @Operation(
            summary = "Listar requisicões do aluno páginada",
            description = "Esta rota permite buscar requisições já submetidas por um aluno de forma páginada. " +
                    "Possui com retorno uma Map 'requisicoes' com uma lista de de requisições, paginaAtual, totalItens e totalPaginas. " +
                    "Essa rota séra util para análise de requisições por parte dos coordenação e comissão."
    )
    @GetMapping("/requisicao/paginacao")
    public  ResponseEntity<?> listarRequisicaoPorAlunoPaginacao(
            @RequestParam Long alunoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade
    ) {
        ResponseEntity<?> resposta;
        try {
            resposta = ResponseEntity.ok(servico.listarRequisicoesPorAlunoPaginadas(alunoId, pagina, quantidade));

        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(new MensagemUtil(e.getMessage()));
        }

        return resposta;
    }
    
    @GetMapping("/requisicao/eixo")
    public  ResponseEntity<?> listarRequisicaoPorAlunoPaginacaoEixo(
            @RequestParam Long alunoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int quantidade,
            @RequestParam EixoEnum eixo
    ) {
        ResponseEntity<?> resposta;
        try {
            resposta = ResponseEntity.ok(servico.listarRequisicoesPorAlunoPaginadasEixo(alunoId, eixo, pagina, quantidade));

        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(new MensagemUtil(e.getMessage()));
        }

        return resposta;
    }

    @Operation(
            summary = "Retornar dados de perfil do usuário",
            description = "Esta Rota permite o usuário ter acesso a suas informações no registrados. Possui com retorno " +
                    "informações como id, nome completo, número de matricula, telefone, email, perfis, curso, " +
                    "periodo e se é verificado. Essa rota séra util para acesso de suas informações pelo usuário."
    )
    @GetMapping("/me")
    public ResponseEntity<?> retornarPerfilDoUsuario(HttpServletRequest request) {

        ResponseEntity<?> resposta;
        try {
            String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
            var usuarioResposta = new UsuarioResposta(servico.buscarUsuarioPorEmail(email));
            resposta = ResponseEntity.ok(usuarioResposta);
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(new MensagemUtil(e.getMessage()));
        }

        return resposta;
    }

    @Operation(
            summary = "Verificar usuário",
            description = "Esta rota permite o usuário se verificar via código enviando para o email." +
                    "Essa rota é util para o sistema se certificar que o usuário se cadastro com um email que " +
                    "ele possui acesso."
    )
    @PostMapping("/verificacao")
    public ResponseEntity<?> verificarUsuario(HttpServletRequest request,
                                              @RequestParam(value = "codigoDeVerificacao") String codigo) {
        ResponseEntity<?> resposta;
        try {
            String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
            resposta = ResponseEntity.ok(new MensagemUtil(servico.verificarUsuario(email, codigo)));
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.status(406).body(new MensagemUtil(e.getMessage()));
        }

        return resposta;
    }

    @Operation(
            summary = "Alterar senha do usuário",
            description = "Esta rota permite ao usuário modificar sua senha por uma nova e está disponíveis " +
                    "para todos os usuários."
    )
    @PatchMapping("/senha")
    public ResponseEntity<?> alterarSenha(
            HttpServletRequest request,
            @RequestBody AlterarSenhaDTO alterarSenhaDTO
    ) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        ResponseEntity<?> resposta;
        try {
            servico.alterarSenha(email, alterarSenhaDTO.getSenha(), alterarSenhaDTO.getNovaSenha());
            resposta = ResponseEntity.noContent().build();
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(new MensagemUtil(e.getMessage()));
        }

        return resposta;
    }

    @Operation(
            summary = "Solicitar novo código de veríficação",
            description = """
                    Esta rota permite ao usuário solicitar o envio de um novo código de veríficação para seu email.
                    \nPré-condição: É necessário que o usuário esteja logado
                    \nPós-condição: Usuario deve receber um email"""
    )
    @PatchMapping("/verificacao/novo")
    public ResponseEntity<?> alterarCodigoVerificacao(HttpServletRequest request) {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        ResponseEntity<?> resposta;
        try {

            resposta = ResponseEntity.ok(new MensagemUtil(servico.alterarCodigoVerificacao(email)));
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(new MensagemUtil(e.getMessage()));
        }

        return resposta;
    }

    @Operation(summary = "Alterar informações de cadastro")
    @PutMapping("/informacoes")
    public ResponseEntity<?> alterarInformacoes(
            HttpServletRequest request,
            @RequestParam String nomeCompleto,
            @RequestParam String telefone,
            @RequestParam Long cursoId
    ) throws AcsExcecao {
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        ResponseEntity<?> resposta;
        try {
            servico.alterarDados(email, nomeCompleto, telefone, cursoId);
            resposta = ResponseEntity.noContent().build();
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(new MensagemUtil(e.getMessage()));
        }
        return resposta;
    }

    @Operation( summary = "Desativar meu Perfil")
    @DeleteMapping("/me")
    public ResponseEntity<?> desativarPerfilDoUsuário(HttpServletRequest request) {
        ResponseEntity<?> resposta;
        String email = jwtService.extractUsername(request.getHeader("Authorization").substring(7));
        try {
            servico.desativarPerfilDoUsuario(email);
            resposta = ResponseEntity.noContent().build();
        } catch (AcsExcecao e) {
            resposta = ResponseEntity.badRequest().body(new MensagemUtil(e.getMessage()));
        }

        return resposta;
    }

}
