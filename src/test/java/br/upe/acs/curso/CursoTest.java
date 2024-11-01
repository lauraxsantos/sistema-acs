package br.upe.acs.curso;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.CursoControlador;
import br.upe.acs.dominio.Curso;

import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.servico.CursoServico;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CursoControlador.class)
public class CursoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CursoServico servico;

    @MockBean
    private UsuarioRepositorio usuarioRepositorio;

    @MockBean
    private JwtService jwtService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);
    }

    @Test
    @WithMockUser
    public void listarCursos_DeveRetornarListaDeCursos() throws Exception {
        // Arrange

        Curso curso1 = new Curso();
        curso1.setId(1L);
        curso1.setNome("Matemática");


        Curso curso2 = new Curso();
        curso2.setId(2L);
        curso2.setNome("Engenharia");


        List<Curso> cursos = Arrays.asList(curso1, curso2);
        when(servico.listarCursos()).thenReturn(cursos);

        // Act & Assert
        mockMvc.perform(get("/api/curso")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Matemática"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Engenharia"));

    }

    @Test
    @WithMockUser
    public void listarCurso_DeveRetornarCursoPeloId() throws Exception {
        // Arrange

        Long cursoId = 1L;
        Curso curso = new Curso();
        curso.setId(cursoId);
        curso.setNome("Matemática");
        curso.setSigla("MAT");

        when(servico.buscarCursoPorId(cursoId)).thenReturn(curso);

        // Act & Assert
        mockMvc.perform(get("/api/curso/{id}", cursoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cursoId))
                .andExpect(jsonPath("$.nome").value("Matemática"));

    }
}

