package br.upe.acs.requisicao;

import static com.sun.org.apache.xerces.internal.util.PropertyState.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.upe.acs.controlador.respostas.RequisicaoResposta;
import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.Requisicao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.RequisicaoControlador;
import br.upe.acs.servico.RequisicaoServico;
import br.upe.acs.certificado.CertificadoControladorTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RequisicaoControladorTest {

    @Mock
    private RequisicaoServico requisicaoServico;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RequisicaoControlador requisicaoControlador;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(requisicaoControlador).build();
    }

    @Test
    public void testListarRequisicoes() throws Exception {

        Certificado certificado = CertificadoControladorTest.createMockCertificado();
        // Arrange
        Requisicao requisicao1 = new Requisicao();
        requisicao1.setId(1L);
        requisicao1.setIdRequisicao("REQ001");
        requisicao1.setCriacao(new Date());
        requisicao1.setStatusRequisicao(RequisicaoStatusEnum.RASCUNHO);
        requisicao1.setArquivada(false);
        requisicao1.setCertificados(Collections.singletonList(certificado));

        Requisicao requisicao2 = new Requisicao();
        requisicao2.setId(2L);
        requisicao2.setIdRequisicao("REQ002");
        requisicao2.setCriacao(new Date());
        requisicao2.setStatusRequisicao(RequisicaoStatusEnum.RASCUNHO);
        requisicao2.setArquivada(false);
        requisicao1.setCertificados(Collections.singletonList(certificado));

        List<Requisicao> requisicoes = Arrays.asList(requisicao1, requisicao2);

        when(requisicaoServico.listarRequisicoes()).thenReturn(requisicoes);

        // Act & Assert
        mockMvc.perform(get("/api/requisicao")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect((ResultMatcher) jsonPath("$[0].idRequisicao", is("REQ001")))
                .andExpect((ResultMatcher) jsonPath("$[0].certificados[0].titulo", is("Certificado Teste")))
                .andExpect((ResultMatcher) jsonPath("$[0].certificados[0].observacao", is("Observação do certificado")))
                .andExpect((ResultMatcher) jsonPath("$[0].certificados[0].cargaHoraria", is(40.0)))
                .andExpect((ResultMatcher) jsonPath("$[1].idRequisicao", is("REQ002")))
                .andExpect(jsonPath("$[1].certificados", hasSize(0)));

        verify(requisicaoServico, times(1)).listarRequisicoes();
    }

}
