package br.upe.acs.certificado;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.CertificadoControlador;
import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.enums.CertificadoStatusEnum;
import br.upe.acs.servico.CertificadoServico;
import br.upe.acs.utils.AcsExcecao;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CertificadoControladorTest {

    @Mock
    private CertificadoServico servico;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CertificadoControlador controlador;

    public static Certificado createMockCertificado() {
        Certificado certificado = new Certificado();
        certificado.setId(1L);
        certificado.setTitulo("Certificado Teste");
        certificado.setObservacao("Observação do certificado");
        certificado.setDataInicial(new Date());
        certificado.setDataFinal(new Date());
        certificado.setCargaHoraria(40.0f);
        certificado.setCertificado(new byte[]{1, 2, 3});
        certificado.setStatusCertificado(CertificadoStatusEnum.RASCUNHO);
        return certificado;
    }

    @Test
    public void testAdicionarCertificado_RequisicaoNaoPertenceAoUsuario_ReturnBadRequest() throws IOException, AcsExcecao {
        // Simulando as entradas
        HttpServletRequest request = mock(HttpServletRequest.class);
        MultipartFile certificado = mock(MultipartFile.class);
        Long requisicaoId = 1L;

        // Simulando um token JWT válido com o email do usuário que não pertence à requisição
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhh"
        		+ "bXBsZS5jb20ifQ.L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";
  
        

        // Definindo o comportamento do mock do jwtService para extrair o email do token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token); // Adicionando o prefixo "Bearer"
        when(jwtService.extractUsername(eq(token))).thenReturn("email_do_usuario_logado"); // Usando o token sem o prefixo "Bearer"

        // Definindo o comportamento do mock do servico para lançar exceção quando a Requisição não pertencer ao usuário
        when(servico.adicionarCertificado(eq(certificado), eq(requisicaoId), eq("email_do_usuario_logado")))
                .thenThrow(new AcsExcecao("Esse id não pertence a nenhuma requisição do aluno!"));

        // Executando o método do controlador
        ResponseEntity<?> resposta = controlador.adicionarCertificado(request, requisicaoId, certificado);

        // Verificando se a resposta é a esperada
        assertEquals(404, resposta.getStatusCodeValue());
        assertEquals("Esse id não pertence a nenhuma requisição do aluno!", resposta.getBody());
    }

    @Test
    public void testAdicionarCertificado_RequisicaoPertenceAoUsuario_ReturnCreated() throws IOException, AcsExcecao {
        // Simulando as entradas
        HttpServletRequest request = mock(HttpServletRequest.class);
        MultipartFile certificado = mock(MultipartFile.class);
        Long requisicaoId = 1L;

        // Simulando um token JWT válido com o email do usuário que pertence à requisição
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5"
        		+ "jb20ifQ.L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";

        // Definindo o comportamento do mock do jwtService para extrair o email do token
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtService.extractUsername(eq("eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5j"
        		+ "b20ifQ.L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0"))).thenReturn("email_do_usuario_logado");

        // Definindo o comportamento do mock do servico para retornar o id do Certificado quando a Requisição pertencer ao usuário
        when(servico.adicionarCertificado(eq(certificado), eq(requisicaoId), eq("email_do_usuario_logado"))).thenReturn(1234L);

        // Executando o método do controlador
        ResponseEntity<?> resposta = controlador.adicionarCertificado(request, requisicaoId, certificado);

        // Verificando se a resposta é a esperada
        assertEquals(201, resposta.getStatusCodeValue());
        assertEquals(1234L, resposta.getBody());
    }
  
}
