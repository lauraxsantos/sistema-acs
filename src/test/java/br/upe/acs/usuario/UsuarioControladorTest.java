package br.upe.acs.usuario;

import br.upe.acs.config.JwtService;

import br.upe.acs.controlador.CursoControlador;
import br.upe.acs.controlador.UsuarioControlador;
import br.upe.acs.controlador.respostas.UsuarioResposta;
import br.upe.acs.dominio.Curso;
import br.upe.acs.dominio.Endereco;
import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.dto.AlterarSenhaDTO;
import br.upe.acs.dominio.enums.EixoEnum;
import br.upe.acs.dominio.enums.PerfilEnum;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.servico.CursoServico;
import br.upe.acs.servico.UsuarioServico;
import br.upe.acs.utils.AcsExcecao;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.upe.acs.dominio.enums.RequisicaoStatusEnum;

@WebMvcTest(UsuarioControlador.class)
public class UsuarioControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioServico servico;



    @MockBean
    private CursoServico cursoServico;

    @MockBean
    private UsuarioRepositorio usuarioRepositorio;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);
    }

    @Test
    @WithMockUser
    public void listarUsuarioId() throws Exception {
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();

        usuario.setId(usuarioId);
        usuario.setNomeCompleto("João da Silva");
        usuario.setMatricula("2023123456");
        usuario.setPeriodo(5);
        usuario.setTelefone("(81) 98765-4321");
        usuario.setEmail("joao.silva@example.com");
        usuario.setVerificado(true);
        usuario.setPerfil(PerfilEnum.ALUNO);
        usuario.setCurso(new Curso());

        when(servico.buscarUsuarioPorId(usuarioId)).thenReturn(usuario);

        mockMvc.perform(get("/api/usuario/{id}", usuarioId)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9." +
                                "eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ." +
                                "L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId))
                .andExpect(jsonPath("$.nomeCompleto").value("João da Silva"))
                .andExpect(jsonPath("$.matricula").value("2023123456"))
                .andExpect(jsonPath("$.periodo").value(5))
                .andExpect(jsonPath("$.telefone").value("(81) 98765-4321"))
                .andExpect(jsonPath("$.email").value("joao.silva@example.com"))
                .andExpect(jsonPath("$.verificado").value(true))
                .andExpect(jsonPath("$.perfis").value("ALUNO"))
                .andExpect(jsonPath("$.curso").exists()); ;
    }

    @Test
    @WithMockUser
    public void listarUsuario() throws Exception {

        String usuarioEmail = "joao.silva@example.com";
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9." +
                "eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ." +
                "L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";
        Usuario usuario = new Usuario();

        usuario.setId(1L);
        usuario.setNomeCompleto("João da Silva");
        usuario.setMatricula("2023123456");
        usuario.setPeriodo(5);
        usuario.setTelefone("(81) 98765-4321");
        usuario.setEmail(usuarioEmail);
        usuario.setVerificado(true);
        usuario.setPerfil(PerfilEnum.ALUNO);
        usuario.setCurso(new Curso());

        when(jwtService.extractUsername(anyString())).thenReturn(usuarioEmail);

        when(servico.buscarUsuarioPorEmail(usuarioEmail)).thenReturn(usuario);

        mockMvc.perform(get("/api/usuario/me")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeCompleto").value("João da Silva"))
                .andExpect(jsonPath("$.matricula").value("2023123456"))
                .andExpect(jsonPath("$.periodo").value(5))
                .andExpect(jsonPath("$.telefone").value("(81) 98765-4321"))
                .andExpect(jsonPath("$.email").value(usuarioEmail))
                .andExpect(jsonPath("$.verificado").value(true))
                .andExpect(jsonPath("$.perfis").value("ALUNO"))
                .andExpect(jsonPath("$.curso").exists());
    }

    @Test
    @WithMockUser// Certifique-se de que o usuário tem a role necessária
    public void alterarInformacoes() throws Exception {
        String usuarioEmail = "joao.silva@example.com";
        String nomeCompleto = "Ana";
        String telefone = "(81) 98765-4321";
        Long cursoId = 1L;

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNomeCompleto("João da Silva");
        usuario.setMatricula("2023123456");
        usuario.setPeriodo(5);
        usuario.setTelefone("(81) 98765-4321");
        usuario.setEmail(usuarioEmail);
        usuario.setVerificado(true);
        usuario.setPerfil(PerfilEnum.ALUNO);
        usuario.setCurso(new Curso());

        // Simula o comportamento do jwtService
        when(jwtService.extractUsername(anyString())).thenReturn(usuarioEmail);
        when(servico.buscarUsuarioPorEmail(usuarioEmail)).thenReturn(usuario);

        // Executa a requisição usando MockMvc
        mockMvc.perform(put("/api/usuario/informacoes")
                .header("Authorization", "Bearer fake.token")
                .param("nomeCompleto", nomeCompleto)
                .param("telefone", telefone)
                .param("cursoId", cursoId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(servico).alterarDados(usuarioEmail, nomeCompleto, telefone, cursoId);

    }
}




