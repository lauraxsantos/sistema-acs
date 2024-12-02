package br.upe.acs.comissao;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.ComissaoControlador;
import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.enums.CertificadoStatusEnum;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.servico.ComissaoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComissaoControlador.class)
public class ComissaoControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComissaoServico comissaoServico;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioRepositorio usuarioRepositorio;

    private String token;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImNvbWlzc2FvQGV4YW1wbGUuY29tIn0.abc123";
        when(this.jwtService.isTokenValid(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any()
        )).thenReturn(true);
    }

    @Test
    @WithMockUser
    public void listarRequisicoesPaginadasTest() throws Exception {
        when(jwtService.extractUsername(ArgumentMatchers.anyString()))
                .thenReturn("comissao@example.com");

        Map<String, Object> mockResponse = Map.of(
                "total", 3,
                "requisicoes", List.of(
                        Map.of("id", 1, "status", "TRANSITO"),
                        Map.of("id", 2, "status", "ACEITO"),
                        Map.of("id", 3, "status", "NEGADO")
                )
        );

        when(comissaoServico.listarRequisicoesPaginadas(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
        )).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comissao")
                        .header("Authorization", token)
                        .param("pagina", "0")
                        .param("quantidade", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.requisicoes").isArray())
                .andExpect(jsonPath("$.requisicoes[0].status").value("TRANSITO"))
                .andExpect(jsonPath("$.requisicoes[1].status").value("ACEITO"))
                .andExpect(jsonPath("$.requisicoes[2].status").value("NEGADO"));
    }

    @Test
    @WithMockUser
    public void listarRequisicoesPaginadasTransitoTest() throws Exception {

        when(jwtService.extractUsername(ArgumentMatchers.anyString()))
                .thenReturn("comissao@example.com");

        Map<String, Object> mockResponse = Map.of(
                "total", 2,
                "requisicoes", List.of(
                        Map.of("id", 1, "status", "TRANSITO"),
                        Map.of("id", 2, "status", "TRANSITO")
                )
        );


        when(comissaoServico.listarRequisicoesPaginadasTransito(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
        )).thenReturn(mockResponse);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/comissao/transito")
                        .header("Authorization", token)
                        .param("pagina", "0")
                        .param("quantidade", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.requisicoes").isArray())
                .andExpect(jsonPath("$.requisicoes[0].status").value("TRANSITO"))
                .andExpect(jsonPath("$.requisicoes[1].status").value("TRANSITO"));
    }

    @Test
    @WithMockUser
    public void listarRequisicoesPaginadasConcluidasTest() throws Exception {
        when(jwtService.extractUsername(ArgumentMatchers.anyString()))
                .thenReturn("comissao@example.com");

        Map<String, Object> mockResponse = Map.of(
                "total", 1,
                "requisicoes", List.of(
                        Map.of("id", 1, "status", "ACEITO")
                )
        );


        when(comissaoServico.listarRequisicoesPaginadasConcluidas(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
        )).thenReturn(mockResponse);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/comissao/concluidas")
                        .header("Authorization", token)
                        .param("pagina", "0")
                        .param("quantidade", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.requisicoes").isArray())
                .andExpect(jsonPath("$.requisicoes[0].status").value("ACEITO"));
    }



    @Test
    @WithMockUser
    public void avaliarCertificadosTest() throws Exception {
        Long certificadoId = 1L;
        CertificadoStatusEnum status = CertificadoStatusEnum.CONCLUIDO;
        String observacao = "Certificado aprovado.";
        float cargaHoraria = 20.0f;
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImNvbWlzc2FvQGV4YW1wbGUuY29tIn0.abc123";


        Certificado certificadoMock = Mockito.mock(Certificado.class);
        when(certificadoMock.getId()).thenReturn(certificadoId);
        when(certificadoMock.getTitulo()).thenReturn("Certificado Teste");
        when(certificadoMock.getObservacao()).thenReturn(observacao);
        when(certificadoMock.getDataInicial()).thenReturn(new Date());
        when(certificadoMock.getDataFinal()).thenReturn(new Date());
        when(certificadoMock.getCargaHoraria()).thenReturn(cargaHoraria);
        when(certificadoMock.getStatusCertificado()).thenReturn(status);


        when(comissaoServico.avaliarCertificado(
                ArgumentMatchers.eq(certificadoId),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(status),
                ArgumentMatchers.eq(observacao),
                ArgumentMatchers.eq(cargaHoraria)
        )).thenReturn(certificadoMock);


        when(jwtService.extractUsername(ArgumentMatchers.anyString())).thenReturn("usuario@example.com");

    
        mockMvc.perform(MockMvcRequestBuilders.post("/api/comissao/certificados")
                        .header("Authorization", token)
                        .param("certificadoId", String.valueOf(certificadoId))
                        .param("status", status.name())
                        .param("observacao", observacao)
                        .param("cargaHoraria", String.valueOf(cargaHoraria))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(certificadoId))
                .andExpect(jsonPath("$.titulo").value("Certificado Teste"))
                .andExpect(jsonPath("$.statusCertificado").value(status.name()))
                .andExpect(jsonPath("$.cargaHoraria").value(cargaHoraria))
                .andExpect(jsonPath("$.observacao").value(observacao));
    }



    @Test
    @WithMockUser
    public void avaliarRequisicaoTest() throws Exception {
        Long requisicaoId = 1L;
        RequisicaoStatusEnum status = RequisicaoStatusEnum.ACEITO;
        String observacao = "Requisição aprovada.";
        String emailSimulado = "usuario@example.com";
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImNvbWlzc2FvQGV4YW1wbGUuY29tIn0.abc123";

        Requisicao requisicaoMock = Mockito.mock(Requisicao.class);
        when(requisicaoMock.getId()).thenReturn(requisicaoId);
        when(requisicaoMock.getStatusRequisicao()).thenReturn(RequisicaoStatusEnum.ACEITO);
        when(requisicaoMock.getObservacao()).thenReturn(observacao);

        when(comissaoServico.avaliarRequisicao(
                ArgumentMatchers.eq(requisicaoId),
                ArgumentMatchers.eq(emailSimulado),
                ArgumentMatchers.eq(status),
                ArgumentMatchers.eq(observacao)
        )).thenReturn(requisicaoMock);

        when(jwtService.extractUsername(ArgumentMatchers.anyString())).thenReturn(emailSimulado);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comissao/requisicao")
                        .header("Authorization", token)
                        .param("requisicaoId", String.valueOf(requisicaoId))
                        .param("status", status.name())
                        .param("observacao", observacao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requisicaoId))
                .andExpect(jsonPath("$.requisicaoStatus").value(status.name()))
                .andExpect(jsonPath("$.observacao").value(observacao));
    }


}

