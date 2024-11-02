package br.upe.acs.atividade;

import br.upe.acs.controlador.AtividadeControlador;
import br.upe.acs.dominio.Atividade;
import br.upe.acs.dominio.dto.AtividadeDTO;
import br.upe.acs.servico.AtividadeServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AtividadeControlador.class)
public class AtividadeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtividadeServico servico;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser
    public void listarAtividades_DeveRetornarListaDeAtividades() throws Exception {
        // Arrange
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
        when(servico.listarAtividades()).thenReturn(atividades);

        // Act & Assert
        mockMvc.perform(get("/api/atividade")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].descricao").value("Atividade de Matemática"))
                .andExpect(jsonPath("$[0].criteriosParaAvaliacao").value("Critérios para Matemática"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].descricao").value("Atividade de Engenharia"))
                .andExpect(jsonPath("$[1].criteriosParaAvaliacao").value("Critérios para Engenharia"));
    }

    @Test
    @WithMockUser
    public void buscarAtividadePorId_DeveRetornarAtividade() throws Exception {
        // Arrange
        Long atividadeId = 1L;
        Atividade atividade = new Atividade();
        atividade.setId(atividadeId);
        atividade.setDescricao("Atividade de Matemática");
        atividade.setCriteriosParaAvaliacao("Critérios para Matemática");

        when(servico.buscarAtividadePorId(atividadeId)).thenReturn(atividade);

        // Act & Assert
        mockMvc.perform(get("/api/atividade/{id}", atividadeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(atividadeId))
                .andExpect(jsonPath("$.descricao").value("Atividade de Matemática"))
                .andExpect(jsonPath("$.criteriosParaAvaliacao").value("Critérios para Matemática"));
    }

    @Test
    @WithMockUser
    public void buscarAtividadePorEixo_DeveRetornarListaDeAtividadesPorEixo() throws Exception {
        // Arrange
        String eixo = "MATEMATICA";
        Atividade atividade1 = new Atividade();
        atividade1.setId(1L);
        atividade1.setDescricao("Atividade de Matemática");

        List<Atividade> atividades = Arrays.asList(atividade1);
        when(servico.buscarAtividadePorEixo(eixo)).thenReturn(atividades);

        // Act & Assert
        mockMvc.perform(get("/api/atividade/eixo")
                        .param("eixo", eixo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].descricao").value("Atividade de Matemática"));
    }

    @Test
    @WithMockUser
    public void criarAtividade_DeveRetornarAtividadeCriada() throws Exception {
        // Arrange
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

        when(servico.criarAtividade(any(AtividadeDTO.class))).thenReturn(atividade);

        // Act & Assert
        mockMvc.perform(post("/api/atividade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\": \"Nova Atividade\", \"criteriosParaAvaliacao\": \"Critérios para Nova Atividade\", \"chMaxima\": 60, \"chPorCertificado\": 20}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.descricao").value("Nova Atividade"))
                .andExpect(jsonPath("$.criteriosParaAvaliacao").value("Critérios para Nova Atividade"))
                .andExpect(jsonPath("$.chMaxima").value(60))
                .andExpect(jsonPath("$.chPorCertificado").value(20));
    }

    @Test
    @WithMockUser
    public void alterarAtividade_DeveRetornarAtividadeAlterada() throws Exception {
        // Arrange
        Long atividadeId = 1L;
        AtividadeDTO atividadeDTO = new AtividadeDTO();
        atividadeDTO.setDescricao("Atividade Alterada");

        Atividade atividade = new Atividade();
        atividade.setId(atividadeId);
        atividade.setDescricao(atividadeDTO.getDescricao());

        when(servico.alterarAtividade(anyLong(), any(AtividadeDTO.class))).thenReturn(atividade);

        // Act & Assert
        mockMvc.perform(put("/api/atividade/{id}", atividadeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descricao\": \"Atividade Alterada\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(atividadeId))
                .andExpect(jsonPath("$.descricao").value("Atividade Alterada"));
    }

    @Test
    @WithMockUser
    public void excluirAtividade_DeveRetornarNoContent() throws Exception {
        // Arrange
        Long atividadeId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/atividade/{id}", atividadeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
