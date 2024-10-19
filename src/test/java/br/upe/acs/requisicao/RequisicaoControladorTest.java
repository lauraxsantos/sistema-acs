package br.upe.acs.requisicao;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.RequisicaoControlador;
import br.upe.acs.servico.RequisicaoServico;

@ExtendWith(MockitoExtension.class)
public class RequisicaoControladorTest {

    @Mock
    private RequisicaoServico requisicaoServico;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RequisicaoControlador requisicaoControlador;

    private MockMvc mockMvc;

    @Test
    public void testAdicionarRequisicao() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(requisicaoControlador).build();

        // Simula o comportamento do jwtService.extractUsername
        when(jwtService.extractUsername(anyString())).thenReturn("user@example.com");

     

        // Executa a requisição POST para "/api/requisicao" com o cabeçalho de autorização "Bearer token"
        // e verifica se a resposta é 201 Created
        mockMvc.perform(post("/api/requisicao")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

}
