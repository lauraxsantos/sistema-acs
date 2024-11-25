package br.upe.acs.atividade;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.AtividadeControlador;
import br.upe.acs.dominio.Atividade;
import br.upe.acs.dominio.dto.AtividadeDTO;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.servico.AtividadeServico;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest({AtividadeControlador.class})
public class AtividadeTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AtividadeServico servico;
    @MockBean
    private UsuarioRepositorio usuarioRepositorio;
    @MockBean
    private JwtService jwtService;
    private String token;

    public AtividadeTest() {
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ.L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";
        Mockito.when(this.jwtService.isTokenValid((String)ArgumentMatchers.any(), (UserDetails)ArgumentMatchers.any())).thenReturn(true);
    }

    @Test
    @WithMockUser
    public void listarAtividadesTest() throws Exception {
        Atividade atividade1 = new Atividade();
        atividade1.setId(1L);
        atividade1.setDescricao("Atividade de Matemática");
        atividade1.setCriteriosParaAvaliacao("Critérios para Matemática");
        atividade1.setChMaxima(40);
        atividade1.setChPorCertificado(20);
        Atividade atividade2 = new Atividade();
        atividade2.setId(2L);
        atividade2.setDescricao("Atividade de Engenharia");
        atividade2.setCriteriosParaAvaliacao("Critérios para Engenharia");
        atividade2.setChMaxima(50);
        atividade2.setChPorCertificado(25);
        List<Atividade> atividades = Arrays.asList(atividade1, atividade2);
        Mockito.when(this.servico.listarAtividades()).thenReturn(atividades);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/atividade", new Object[0]).header("Authorization", new Object[]{this.token}).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))).andExpect(MockMvcResultMatchers.jsonPath("$[0].id", new Object[0]).value(1)).andExpect(MockMvcResultMatchers.jsonPath("$[0].descricao", new Object[0]).value("Atividade de Matemática")).andExpect(MockMvcResultMatchers.jsonPath("$[0].criteriosParaAvaliacao", new Object[0]).value("Critérios para Matemática")).andExpect(MockMvcResultMatchers.jsonPath("$[1].id", new Object[0]).value(2)).andExpect(MockMvcResultMatchers.jsonPath("$[1].descricao", new Object[0]).value("Atividade de Engenharia")).andExpect(MockMvcResultMatchers.jsonPath("$[1].criteriosParaAvaliacao", new Object[0]).value("Critérios para Engenharia"));
    }

    @Test
    @WithMockUser
    public void buscarAtividadePorIdTest() throws Exception {
        Long atividadeId = 1L;
        Atividade atividade = new Atividade();
        atividade.setId(atividadeId);
        atividade.setDescricao("Atividade de Matemática");
        atividade.setCriteriosParaAvaliacao("Critérios para Matemática");
        Mockito.when(this.servico.buscarAtividadePorId(atividadeId)).thenReturn(atividade);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/atividade/{id}", new Object[]{atividadeId}).header("Authorization", new Object[]{this.token}).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id", new Object[0]).value(atividadeId)).andExpect(MockMvcResultMatchers.jsonPath("$.descricao", new Object[0]).value("Atividade de Matemática")).andExpect(MockMvcResultMatchers.jsonPath("$.criteriosParaAvaliacao", new Object[0]).value("Critérios para Matemática"));
    }

    @Test
    @WithMockUser
    public void buscarAtividadePorEixoTest() throws Exception {
        String eixo = "MATEMATICA";
        Atividade atividade1 = new Atividade();
        atividade1.setId(1L);
        atividade1.setDescricao("Atividade de Matemática");
        List<Atividade> atividades = Arrays.asList(atividade1);
        Mockito.when(this.servico.buscarAtividadePorEixo(eixo)).thenReturn(atividades);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/atividade/eixo", new Object[0]).param("eixo", new String[]{eixo}).header("Authorization", new Object[]{this.token}).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1))).andExpect(MockMvcResultMatchers.jsonPath("$[0].id", new Object[0]).value(1)).andExpect(MockMvcResultMatchers.jsonPath("$[0].descricao", new Object[0]).value("Atividade de Matemática"));
    }

    @Test
    @WithMockUser
    public void criarAtividadeTest() throws Exception {
        AtividadeDTO atividadeDTO = new AtividadeDTO();
        atividadeDTO.setDescricao("Nova Atividade");
        atividadeDTO.setCriteriosParaAvaliacao("Critérios para Nova Atividade");
        atividadeDTO.setChMaxima(60);
        atividadeDTO.setChPorCertificado(20);
        Atividade atividade = new Atividade();
        atividade.setId(1L);
        atividade.setDescricao(atividadeDTO.getDescricao());
        atividade.setCriteriosParaAvaliacao(atividadeDTO.getCriteriosParaAvaliacao());
        atividade.setChMaxima(atividadeDTO.getChMaxima());
        atividade.setChPorCertificado(atividadeDTO.getChPorCertificado());
        Mockito.when(this.servico.criarAtividade((AtividadeDTO)ArgumentMatchers.any(AtividadeDTO.class))).thenReturn(atividade);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/atividade", new Object[0]).header("Authorization", new Object[]{this.token}).contentType(MediaType.APPLICATION_JSON).content("{\"descricao\": \"Nova Atividade\", \"criteriosParaAvaliacao\": \"Critérios para Nova Atividade\", \"chMaxima\": 60, \"chPorCertificado\": 20}").with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id", new Object[0]).value(1)).andExpect(MockMvcResultMatchers.jsonPath("$.descricao", new Object[0]).value("Nova Atividade")).andExpect(MockMvcResultMatchers.jsonPath("$.criteriosParaAvaliacao", new Object[0]).value("Critérios para Nova Atividade")).andExpect(MockMvcResultMatchers.jsonPath("$.chMaxima", new Object[0]).value(60)).andExpect(MockMvcResultMatchers.jsonPath("$.chPorCertificado", new Object[0]).value(20));
    }

    @Test
    @WithMockUser
    public void alterarAtividadeTest() throws Exception {
        Long atividadeId = 1L;
        AtividadeDTO atividadeDTO = new AtividadeDTO();
        atividadeDTO.setDescricao("Atividade Alterada");
        Atividade atividade = new Atividade();
        atividade.setId(atividadeId);
        atividade.setDescricao(atividadeDTO.getDescricao());
        Mockito.when(this.servico.alterarAtividade(ArgumentMatchers.anyLong(), (AtividadeDTO)ArgumentMatchers.any(AtividadeDTO.class))).thenReturn(atividade);
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/atividade/{id}", new Object[]{atividadeId}).header("Authorization", new Object[]{this.token}).contentType(MediaType.APPLICATION_JSON).content("{\"descricao\": \"Atividade Alterada\"}").with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id", new Object[0]).value(atividadeId)).andExpect(MockMvcResultMatchers.jsonPath("$.descricao", new Object[0]).value("Atividade Alterada"));
    }

    @Test
    @WithMockUser
    public void excluirAtividadeTest() throws Exception {
        Long atividadeId = 1L;
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/atividade/{id}", new Object[]{atividadeId}).header("Authorization", new Object[]{this.token}).contentType(MediaType.APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
