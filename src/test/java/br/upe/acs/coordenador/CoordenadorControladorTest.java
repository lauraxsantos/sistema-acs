package br.upe.acs.coordenador;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.CoordenadorControlador;
import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.Curso;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.enums.PerfilEnum;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.servico.CoordenadorServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoordenadorControlador.class)
public class CoordenadorControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoordenadorServico coordenadorServico;

    @MockBean
    private UsuarioRepositorio usuarioRepositorio;

    @MockBean
    private JwtService jwtService;

    private String token;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ.L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";
        Mockito.when(this.jwtService.isTokenValid((String) ArgumentMatchers.any(), (UserDetails) ArgumentMatchers.any())).thenReturn(true);
    }

    @Test
    @WithMockUser
    public void listarRequisicoesPorCursoTest() throws Exception {
        Mockito.when(jwtService.extractUsername(ArgumentMatchers.anyString()))
                .thenReturn("email_do_coordenador@exemplo.com");

        Map<String, Object> mockResponse = Map.of(
                "total", 2,
                "requisicoes", List.of(
                        Map.of("id", 1, "curso", "Engenharia", "status", "PENDENTE"),
                        Map.of("id", 2, "curso", "Engenharia", "status", "APROVADO")
                )
        );

        Mockito.when(coordenadorServico.listarRequisicoesPaginadasCurso(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
        )).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coordenador")
                        .header("Authorization", token)
                        .param("pagina", "0")
                        .param("quantidade", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[0].curso").value("Engenharia"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[0].status").value("PENDENTE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[1].curso").value("Engenharia"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[1].status").value("APROVADO"));
    }

    @Test
    @WithMockUser
    public void listarRequisicoesPaginadasTest() throws Exception {
        Mockito.when(jwtService.extractUsername(ArgumentMatchers.anyString()))
                .thenReturn("email_do_coordenador@exemplo.com");

        Map<String, Object> mockResponse = Map.of(
                "total", 3,
                "requisicoes", List.of(
                        Map.of("id", 1, "curso", "Engenharia de Software", "status", "PENDENTE"),
                        Map.of("id", 2, "curso", "Engenharia de Software", "status", "PENDENTE"),
                        Map.of("id", 3, "curso", "Engenharia de Software", "status", "PENDENTE")
                )
        );


        Mockito.when(coordenadorServico.listarRequisicoesPaginadasCurso(
                ArgumentMatchers.eq("email_do_coordenador@exemplo.com"),
                ArgumentMatchers.eq(0),
                ArgumentMatchers.eq(10)
        )).thenReturn(mockResponse);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/coordenador")
                        .header("Authorization", token)
                        .param("pagina", "0")
                        .param("quantidade", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[0].curso").value("Engenharia de Software"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[0].status").value("PENDENTE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[1].status").value("PENDENTE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[2].status").value("PENDENTE"));
    }

    @Test
    @WithMockUser
    public void listarRequisicoesPaginadasComissaoTest() throws Exception {

        Mockito.when(jwtService.extractUsername(ArgumentMatchers.anyString()))
                .thenReturn("email_do_coordenador@exemplo.com");


        Map<String, Object> mockResponse = Map.of(
                "total", 4,
                "requisicoes", List.of(
                        Map.of("id", 1, "curso", "Engenharia de Software", "status", "ACEITO"),
                        Map.of("id", 2, "curso", "Engenharia de Software", "status", "PENDENTE"),
                        Map.of("id", 3, "curso", "Engenharia de Software", "status", "ACEITO"),
                        Map.of("id", 4, "curso", "Engenharia de Software", "status", "NEGADO")
                )
        );


        Mockito.when(coordenadorServico.listarRequisicoesComissao(
                ArgumentMatchers.eq("email_do_coordenador@exemplo.com"),
                ArgumentMatchers.eq(0),
                ArgumentMatchers.eq(10)
        )).thenReturn(mockResponse);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/coordenador/requisicoes/comissao")
                        .header("Authorization", token)
                        .param("pagina", "0")
                        .param("quantidade", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[0].curso").value("Engenharia de Software"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[0].status").value("ACEITO"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[1].status").value("PENDENTE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requisicoes[3].status").value("NEGADO"));
    }


    @Test
    @WithMockUser
    public void listarComissaoTest() throws Exception {
        Mockito.when(jwtService.extractUsername(ArgumentMatchers.anyString()))
                .thenReturn("email_do_coordenador@exemplo.com");

        Curso curso1 = new Curso();
        curso1.setId(1L);
        curso1.setNome("Curso de Engenharia");

        Curso curso2 = new Curso();
        curso2.setId(2L);
        curso2.setNome("Curso de Ciência da Computação");

        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setNomeCompleto("Professor 1");
        usuario1.setCpf("12345678900");
        usuario1.setMatricula("matricula123");
        usuario1.setPeriodo(5);
        usuario1.setTelefone("123456789");
        usuario1.setEmail("professor1@exemplo.com");
        usuario1.setSenha("senha123");
        usuario1.setCodigoVerificacao("codigo123");
        usuario1.setVerificado(true);
        usuario1.setHorasEnsino(10.0f);
        usuario1.setHorasExtensao(5.0f);
        usuario1.setHorasGestao(2.0f);
        usuario1.setHorasPesquisa(1.0f);
        usuario1.setEnabled(true);
        usuario1.setPerfil(PerfilEnum.COMISSAO);
        usuario1.setCurso(curso1);

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setNomeCompleto("Professor 2");
        usuario2.setCpf("98765432100");
        usuario2.setMatricula("matricula124");
        usuario2.setPeriodo(6);
        usuario2.setTelefone("987654321");
        usuario2.setEmail("professor2@exemplo.com");
        usuario2.setSenha("senha124");
        usuario2.setCodigoVerificacao("codigo124");
        usuario2.setVerificado(true);
        usuario2.setHorasEnsino(12.0f);
        usuario2.setHorasExtensao(6.0f);
        usuario2.setHorasGestao(3.0f);
        usuario2.setHorasPesquisa(1.5f);
        usuario2.setEnabled(true);
        usuario2.setPerfil(PerfilEnum.COMISSAO);
        usuario2.setCurso(curso2);

        List<Usuario> mockUsuarios = List.of(usuario1, usuario2);

        Mockito.when(coordenadorServico.listarProfessores(ArgumentMatchers.anyString())).thenReturn(mockUsuarios);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/coordenador/comissao")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nomeCompleto").value("Professor 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("professor1@exemplo.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].nomeCompleto").value("Professor 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value("professor2@exemplo.com"));
    }

    @Test
    @WithMockUser
    public void mandarRequisicaoTest() throws Exception {
        Long comissaoId = 1L;
        Long requisicaoId = 2L;

        Mockito.doNothing().when(coordenadorServico).mandarRequisicaoComissao(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/coordenador/requisicao")
                        .header("Authorization", token)
                        .param("comissaoId", String.valueOf(comissaoId))
                        .param("requisicaoId", String.valueOf(requisicaoId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }


}