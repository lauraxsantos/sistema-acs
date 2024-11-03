package br.upe.acs.certificado;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.CertificadoControlador;
import br.upe.acs.controlador.respostas.ArquivoResposta;
import br.upe.acs.controlador.respostas.CertificadoResposta;
import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.dto.CertificadoDTO;
import br.upe.acs.dominio.enums.CertificadoStatusEnum;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.servico.CertificadoServico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CertificadoControlador.class)
public class CertificadoControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificadoServico servico;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioRepositorio usuarioRepositorio;


    private String token;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ.L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";
        when(jwtService.isTokenValid(any(), any())).thenReturn(true);
    }

    @Test
    @WithMockUser
    public void buscarCertificadoPorIdTest() throws Exception {
        Long certificadoId = 1L;

        Certificado certificadoMock = Mockito.mock(Certificado.class);
        when(certificadoMock.getId()).thenReturn(certificadoId);
        when(certificadoMock.getTitulo()).thenReturn("Título do Certificado");

        when(servico.buscarCertificadoPorId(certificadoId)).thenReturn(certificadoMock);

        CertificadoResposta respostaEsperada = new CertificadoResposta(certificadoMock);

        mockMvc.perform(get("/api/certificado/{id}", certificadoId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(respostaEsperada.getId()))
                .andExpect(jsonPath("$.titulo").value(respostaEsperada.getTitulo()));
    }



    @Test
    @WithMockUser
    public void buscarPdfDoCertificadoPorIdTest() throws Exception {
        Long certificadoId = 1L;
        byte[] pdfContent = "dummyPdfContent".getBytes();

        when(servico.buscarPdfDoCertificadoPorId(certificadoId)).thenReturn(pdfContent);

        mockMvc.perform(get("/api/certificado/{id}/pdf", certificadoId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(content().bytes(pdfContent));
    }


    @Test
    @WithMockUser
    public void adicionarCertificadoTest() throws Exception {
        Long requisicaoId = 1L;
        String email = "usuario@exemplo.com";
        MockMultipartFile certificado = new MockMultipartFile("certificado", "dummy.pdf", "application/pdf", "dummy content".getBytes());

        Long certificadoId = 1L;
        when(servico.adicionarCertificado(any(MultipartFile.class), anyLong(), any(String.class))).thenReturn(certificadoId);

        mockMvc.perform(multipart("/api/certificado")
                        .file(certificado)
                        .param("requisicaoId", requisicaoId.toString())
                        .param("email", email)
                        .header("Authorization", token)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/certificado/" + certificadoId));
    }

    @Test
    @WithMockUser
    public void alterarCertificadoTest() throws Exception {
        Long certificadoId = 1L;
        String email = "usuario@exemplo.com";

        Certificado certificadoMock = new Certificado();
        certificadoMock.setId(certificadoId);
        certificadoMock.setTitulo("Título Original");

        CertificadoDTO certificadoDTO = new CertificadoDTO();
        certificadoDTO.setTitulo("Certificado Alterado");

        when(servico.buscarCertificadoPorId(certificadoId)).thenReturn(certificadoMock);

        mockMvc.perform(put("/api/certificado/{id}", certificadoId)
                        .header("Authorization", token)
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\": \"Certificado Alterado\"}")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(servico).alterarCertificado(eq(certificadoId), any(CertificadoDTO.class), eq(email));
    }

    @Test
    @WithMockUser
    public void excluirCertificadoTest() throws Exception {
        Long certificadoId = 1L;
        String email = "usuario@exemplo.com";

        Certificado certificadoMock = new Certificado();
        certificadoMock.setId(certificadoId);
        certificadoMock.setStatusCertificado(CertificadoStatusEnum.RASCUNHO);

        when(servico.buscarCertificadoPorId(certificadoId)).thenReturn(certificadoMock);

        mockMvc.perform(delete("/api/certificado/{id}", certificadoId)
                        .header("Authorization", token)
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(servico).excluirCertificado(certificadoId, email);
    }

}
