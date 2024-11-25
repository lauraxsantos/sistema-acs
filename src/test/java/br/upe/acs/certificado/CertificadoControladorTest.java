package br.upe.acs.certificado;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.CertificadoControlador;
import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.dto.CertificadoDTO;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.servico.CertificadoServico;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest({CertificadoControlador.class})
public class CertificadoControladorTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CertificadoServico servico;
    @MockBean
    private UsuarioRepositorio usuarioRepositorio;
    @MockBean
    private JwtService jwtService;
    private String token;

    public CertificadoControladorTest() {
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ.L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";
        Mockito.when(this.jwtService.isTokenValid((String)ArgumentMatchers.any(), (UserDetails)ArgumentMatchers.any())).thenReturn(true);
    }

    @Test
    @WithMockUser
    public void buscarCertificadoPorIdTest() throws Exception {
        Long certificadoId = 1L;
        Certificado certificadoMock = new Certificado();
        certificadoMock.setId(certificadoId);
        certificadoMock.setTitulo("Título do Certificado");
        Mockito.when(this.servico.buscarCertificadoPorId(certificadoId)).thenReturn(certificadoMock);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/certificado/{id}", new Object[]{certificadoId}).header("Authorization", new Object[]{this.token}).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.id", new Object[0]).value(certificadoId)).andExpect(MockMvcResultMatchers.jsonPath("$.titulo", new Object[0]).value("Título do Certificado"));
    }

    @Test
    @WithMockUser
    public void buscarPdfDoCertificadoPorIdTest() throws Exception {
        Long certificadoId = 1L;
        byte[] pdfContent = "dummyPdfContent".getBytes();
        Mockito.when(this.servico.buscarPdfDoCertificadoPorId(certificadoId)).thenReturn(pdfContent);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/certificado/1/pdf", new Object[0]).header("Authorization", new Object[]{"Bearer seu_token_aqui"}).accept(new MediaType[]{MediaType.APPLICATION_JSON})).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.jsonPath("$.arquivo", new Object[0]).exists()).andExpect(MockMvcResultMatchers.jsonPath("$.arquivo", new Object[0]).value("ZHVtbXlQZGZDb250ZW50"));
    }

    @Test
    @WithMockUser
    public void adicionarCertificadoTest() throws Exception {
        Long requisicaoId = 1L;
        Long certificadoId = 1L;
        MockMultipartFile mockCertificado = new MockMultipartFile("certificado", "certificado.pdf", "application/pdf", "conteudo_dummy_do_pdf".getBytes());
        Mockito.when(this.servico.adicionarCertificado((MultipartFile)ArgumentMatchers.any(), (Long)ArgumentMatchers.any(), (String)ArgumentMatchers.any())).thenReturn(certificadoId);
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/certificado", new Object[0]).file(mockCertificado).param("requisicaoId", new String[]{String.valueOf(requisicaoId)}).header("Authorization", new Object[]{this.token}).with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(MockMvcResultMatchers.status().isCreated()).andExpect(MockMvcResultMatchers.jsonPath("$", new Object[0]).value(certificadoId));
    }

    @Test
    @WithMockUser
    public void alterarCertificadoTest() throws Exception {
        Long certificadoId = 1L;
        CertificadoDTO certificadoDTO = new CertificadoDTO();
        certificadoDTO.setTitulo("Novo Título do Certificado");
        certificadoDTO.setAtividadeId(2L);
        certificadoDTO.setDataIncial("2024-01-01");
        certificadoDTO.setDataFinal("2024-01-10");
        certificadoDTO.setQuantidadeDeHoras(10.0F);
        ((CertificadoServico)Mockito.doNothing().when(this.servico)).alterarCertificado(certificadoId, certificadoDTO, "email_do_usuario@example.com");
        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/certificado/{id}", new Object[]{certificadoId}).header("Authorization", new Object[]{this.token}).contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(certificadoDTO)).with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithMockUser
    public void excluirCertificadoTest() throws Exception {
        Long certificadoId = 1L;
        ((CertificadoServico)Mockito.doNothing().when(this.servico)).excluirCertificado(certificadoId, "email_do_usuario@example.com");
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/certificado/{id}", new Object[]{certificadoId}).header("Authorization", new Object[]{this.token}).with(SecurityMockMvcRequestPostProcessors.csrf())).andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
