package br.upe.acs.endereco;

import br.upe.acs.dominio.dto.ViaCepDTO;
import br.upe.acs.repositorio.EnderecoRepositorio;
import br.upe.acs.servico.EnderecoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.upe.acs.utils.CepInvalidoExcecao;

@RestClientTest(EnderecoServico.class)
public class EnderecoTest {

    @Autowired
    private EnderecoServico enderecoServico;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockRestServiceServer mockServer;

    @MockBean
    private EnderecoRepositorio enderecoRepositorio;
    @TestConfiguration
    static class Config {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void deveBuscarEnderecoPorCepComSucesso() throws Exception {

        String cep = "55294-153";
        ViaCepDTO viaCepDTO = new ViaCepDTO();
        viaCepDTO.setCep(cep);
        viaCepDTO.setLogradouro("Rua Jardim São Paulo");
        viaCepDTO.setBairro("Magano");
        viaCepDTO.setLocalidade("Garanhuns");
        viaCepDTO.setUf("PE");

        String viaCepResponseJson = objectMapper.writeValueAsString(viaCepDTO);

        mockServer.expect(requestTo("https://viacep.com.br/ws/12345678/json/"))
                .andRespond(withSuccess(viaCepResponseJson, MediaType.APPLICATION_JSON));

        // Act
        ViaCepDTO resultado = enderecoServico.buscarEnderecoPorCep(cep);

        // Assert
        assertEquals("55294-153", resultado.getCep());
        assertEquals("Rua Jardim São Paulo", resultado.getLogradouro());
        assertEquals("Magano", resultado.getBairro());
        assertEquals("Garanhuns", resultado.getLocalidade());
        assertEquals("PE", resultado.getUf());
    }

    @Test
    public void deveLancarExcecaoParaCepInvalido() {
        // Arrange
        String cep = "00000000";
        mockServer.expect(requestTo("https://viacep.com.br/ws/00000000/json/"))
                .andRespond(withBadRequest());

        // Act & Assert
        assertThrows(CepInvalidoExcecao.class, () -> enderecoServico.buscarEnderecoPorCep(cep));
    }
}
