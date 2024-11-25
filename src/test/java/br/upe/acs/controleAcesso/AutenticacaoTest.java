package br.upe.acs.controleAcesso;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.AutenticacaoControlador;
import br.upe.acs.controlador.UsuarioControlador;
import br.upe.acs.controlador.respostas.AutenticacaoResposta;
import br.upe.acs.dominio.dto.AlterarSenhaDTO;
import br.upe.acs.dominio.dto.LoginDTO;
import br.upe.acs.dominio.dto.RegistroDTO;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.servico.AutenticacaoServico;
import br.upe.acs.servico.UsuarioServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutenticacaoControlador.class)
public class AutenticacaoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioServico usuarioServico;

    @MockBean
    private AutenticacaoServico servico;

    @MockBean
    private UsuarioRepositorio usuarioRepositorio;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);
    }

    @Test
    @WithMockUser
    public void cadastrarUsuarioTest() throws Exception {

        RegistroDTO registro = new RegistroDTO();
        registro.setNomeCompleto("Jamuelton Angelim");
        registro.setCpf("156.545.474-02");
        registro.setMatricula("2023123456");
        registro.setPeriodo(1);
        registro.setTelefone("(81) 98765-4321");
        registro.setEmail("jamuelton@upe.br");
        registro.setSenha("teste12345!");
        registro.setCep("55294-153");
        registro.setRua("Rua São Paulo");
        registro.setBairro("Magano");
        registro.setCidade("Garanhuns");
        registro.setComplemento("Apto C");
        registro.setNumero(145);
        registro.setCursoId(1L);
        registro.setUF("PE");

        AutenticacaoResposta resposta = new AutenticacaoResposta("tokenExemplo123");

        when(servico.cadastrarUsuario(any(RegistroDTO.class))).thenReturn(resposta);

        String registroJson = new ObjectMapper().writeValueAsString(registro);

        mockMvc.perform(post("/api/auth/acesso/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registroJson)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("tokenExemplo123"));
    }

    @Test
    @WithMockUser
    public void loginUsuarioTest() throws Exception {

        LoginDTO login = new LoginDTO();
        login.setEmail("jamuelton.angelim@upe.br");
        login.setSenha("Teste12345!");

        AutenticacaoResposta resposta = new AutenticacaoResposta("tokenExemplo123");

        when(servico.loginUsuario(any(LoginDTO.class))).thenReturn(resposta);

        String loginJson = new ObjectMapper().writeValueAsString(login);

        mockMvc.perform(post("/api/auth/acesso/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("tokenExemplo123"));
    }

    @Test
    @WithMockUser
    public void verificarUsuarioTest() throws Exception {
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9." +
                "eyJlbWFpbCI6ImphbXVlbHRvbkB1cGUuYnIifQ." +
                "L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";
        String email = "jamuelton@upe.br";
        String codigoDeVerificacao = "123456";


        when(jwtService.extractUsername(anyString())).thenReturn(email);


        String mensagemDeSucesso = "Usuário verificado com sucesso";
        when(servico.verificarUsuario(email, codigoDeVerificacao)).thenReturn(mensagemDeSucesso);


        mockMvc.perform(post("/api/auth/verificacao")
                        .header("Authorization", token)
                        .param("codigoDeVerificacao", codigoDeVerificacao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value(mensagemDeSucesso));
    }

    @Test
    @WithMockUser
    public void enviarCodigoVericiacaoTest() throws Exception {
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9." +
                "eyJlbWFpbCI6ImphbXVlbHRvbkB1cGUuYnIifQ." +
                "L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";
        String email = "jamuelton@upe.br";


        when(jwtService.extractUsername(anyString())).thenReturn(email);

        String mensagemDeSucesso = "Código de verificação alterado com sucesso";
        when(servico.alterarCodigoVerificacao(email)).thenReturn(mensagemDeSucesso);


        mockMvc.perform(patch("/api/auth/verificacao/novo")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value(mensagemDeSucesso));
    }

    @Test
    @WithMockUser
    public void esquecerSenhaTest() throws Exception {

        String token = "Bearer eyJhbGciOiJIUzI1NiJ9." +
                "eyJlbWFpbCI6ImphbXVlbHRvbkB1cGUuYnIifQ." +
                "L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";

        String email = "jamuelton@upe.br";

        when(jwtService.extractUsername(anyString())).thenReturn(email);

        doNothing().when(servico).esquecerSenha(email);




        mockMvc.perform(post("/api/auth/acesso/senha/esquecer")
                        .header("Authorization", token)
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

}