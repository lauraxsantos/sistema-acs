package br.upe.acs.requisicao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import br.upe.acs.dominio.enums.EixoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.RequisicaoControlador;
import br.upe.acs.controlador.respostas.RequisicaoSimplesResposta;
import br.upe.acs.dominio.Certificado;
import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.enums.CertificadoStatusEnum;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;
import br.upe.acs.repositorio.UsuarioRepositorio;
import br.upe.acs.servico.RequisicaoServico;
import br.upe.acs.servico.UsuarioServico;
import br.upe.acs.utils.AcsExcecao;
import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(RequisicaoControlador.class)
public class RequisicaoControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequisicaoServico servico; 
    
    @MockBean
    private UsuarioRepositorio usuarioRepositorio;
    
    @MockBean
    private UsuarioServico usuarioServico;
    
    @MockBean
    private JwtService jwtService;
    
    @MockBean
    private HttpServletRequest request; 

    @InjectMocks
    private RequisicaoControladorTest requisicaoController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser
    public void testListarRequisicoes() throws Exception {

        Requisicao requisicao1 = new Requisicao();
        requisicao1.setId(1L);
        requisicao1.setIdRequisicao("123");
        requisicao1.setToken("tokenExemplo");
        requisicao1.setArquivada(false);
        requisicao1.setStatusRequisicao(RequisicaoStatusEnum.RASCUNHO);
        requisicao1.setObservacao("observação exemplo");


        Certificado certificado = new Certificado();
        certificado.setId(1L);
        certificado.setTitulo("Título Exemplo");
        certificado.setObservacao("observação exemplo");
        certificado.setStatusCertificado(CertificadoStatusEnum.RASCUNHO);
        certificado.setCargaHoraria(0);
        
        requisicao1.setCertificados(List.of(certificado));

        when(servico.listarRequisicoes()).thenReturn(List.of(requisicao1));
               

        mockMvc.perform(get("/api/requisicao")
        		 .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9." +
                         "eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ." +
                         "L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requisicao1.getId()))
                .andExpect(jsonPath("$[0].idRequisicao").value(requisicao1.getIdRequisicao()))
                .andExpect(jsonPath("$[0].token").value(requisicao1.getToken()))
                .andExpect(jsonPath("$[0].arquivada").value(requisicao1.isArquivada()))
                .andExpect(jsonPath("$[0].requisicaoStatus").value(requisicao1.getStatusRequisicao().toString()))
                .andExpect(jsonPath("$[0].observacao").value(requisicao1.getObservacao()))
                .andExpect(jsonPath("$[0].certificados[0].id").value(certificado.getId()))
                .andExpect(jsonPath("$[0].certificados[0].titulo").value(certificado.getTitulo()))
                .andExpect(jsonPath("$[0].certificados[0].observacao").value(certificado.getObservacao()))
                .andExpect(jsonPath("$[0].certificados[0].statusCertificado").value(certificado.getStatusCertificado().toString()))
                .andExpect(jsonPath("$[0].certificados[0].cargaHoraria").value(certificado.getCargaHoraria()));


        verify(servico, times(1)).listarRequisicoes();
     }
    
    @Test
    @WithMockUser
    public void testListarRequisicoesPaginas() throws Exception {
        // Arrange
        int pagina = 0;
        int quantidade = 10;
        
        Requisicao requisicao = new Requisicao();
        requisicao.setId(102L);
        requisicao.setIdRequisicao(null);
        requisicao.setStatusRequisicao(RequisicaoStatusEnum.TRANSITO);
        requisicao.setDataDeSubmissao(new Date());
        
        Requisicao requisicao2 = new Requisicao();
        requisicao2.setId(102L);
        requisicao2.setIdRequisicao(null);
        requisicao2.setStatusRequisicao(RequisicaoStatusEnum.TRANSITO);
        requisicao2.setDataDeSubmissao(new Date());
        

        Certificado certificado = new Certificado();
        certificado.setId(1L);
        certificado.setTitulo("Título Exemplo");
        certificado.setObservacao("observação exemplo");
        certificado.setStatusCertificado(CertificadoStatusEnum.RASCUNHO);
        certificado.setCargaHoraria(0);
        
        requisicao.setCertificados(List.of(certificado));
        requisicao2.setCertificados(List.of(certificado));
        

        Map<String, Object> resultadoEsperado = new HashMap<>();
        resultadoEsperado.put("paginaAtual", pagina);
        resultadoEsperado.put("totalItems", 2);
        resultadoEsperado.put("totalPaginas", 1);
        resultadoEsperado.put("requisicoes", List.of(new RequisicaoSimplesResposta(requisicao), new RequisicaoSimplesResposta(requisicao2)));
        when(servico.listarRequisicoesPaginadas(pagina, quantidade)).thenReturn(resultadoEsperado);

        // Act & Assert
        mockMvc.perform(get("/api/requisicao/paginacao")
                .param("pagina", String.valueOf(pagina))
                .param("quantidade", String.valueOf(quantidade))
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9." +
                        "eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ." +
                        "L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalPaginas").value(1))
                .andExpect(jsonPath("$.paginaAtual").value(pagina))
                .andExpect(jsonPath("$.requisicoes[0].id").value(102))
                .andExpect(jsonPath("$.requisicoes[0].status").value("TRANSITO"));
    }
    
    @Test
    @WithMockUser
    void listarRequisicoesPorAlunoPaginadasTest() throws Exception {
        Long alunoId = 1L;
        int pagina = 0;
        int quantidade = 10;

        Usuario usuarioMock = new Usuario();
        Requisicao requisicao1 = new Requisicao();
        requisicao1.setId(101L);
        requisicao1.setStatusRequisicao(RequisicaoStatusEnum.TRANSITO);
        requisicao1.setDataDeSubmissao(new Date()); 
        requisicao1.setUsuario(usuarioMock);

        Requisicao requisicao2 = new Requisicao();
        requisicao2.setId(102L);
        requisicao2.setStatusRequisicao(RequisicaoStatusEnum.TRANSITO);
        requisicao2.setDataDeSubmissao(new Date()); 
        requisicao2.setUsuario(usuarioMock);
        
        Certificado certificado = new Certificado();
        certificado.setId(1L);
        certificado.setTitulo("Título Exemplo");
        certificado.setObservacao("observação exemplo");
        certificado.setStatusCertificado(CertificadoStatusEnum.ENCAMINHADO_ESCOLARIDADE);
        certificado.setCargaHoraria(0);
        
        requisicao1.setCertificados(List.of(certificado));
        requisicao2.setCertificados(List.of(certificado));

        List<Requisicao> requisicoes = List.of(requisicao1, requisicao2);
        usuarioMock.setRequisicoes(requisicoes); 

        
        Map<String, Object> resultadoEsperado = new HashMap<>();
        resultadoEsperado.put("requisicoes", List.of(new RequisicaoSimplesResposta(requisicao1), new RequisicaoSimplesResposta(requisicao2)));
        when(servico.listarRequisicoesPorAlunoPaginadas(alunoId, pagina, quantidade)).thenReturn(resultadoEsperado);
        when(usuarioServico.buscarUsuarioPorId(alunoId)).thenReturn(usuarioMock);
        
        mockMvc.perform(get("/api/requisicao/aluno/paginacao")
                .param("alunoId", alunoId.toString())
                .param("pagina", String.valueOf(pagina))
                .param("quantidade", String.valueOf(quantidade))
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9." +
                "eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ." +
                "L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requisicoes[0].id").value(101L))
                .andExpect(jsonPath("$.requisicoes[1].id").value(102L));

        verify(servico, times(1)).listarRequisicoesPorAlunoPaginadas(alunoId, pagina, quantidade);
    }
    
    @Test
    @WithMockUser
    public void testListarRequisicoesArquivadas() throws Exception {
    	String usuarioEmail = "joao.silva@example.com";
    	
    	Usuario usuarioMock = new Usuario();
    	usuarioMock.setEmail(usuarioEmail);
    	
        Requisicao requisicao1 = new Requisicao();
        requisicao1.setId(1L);
        requisicao1.setIdRequisicao("123");
        requisicao1.setToken("tokenExemplo");
        requisicao1.setArquivada(false);
        requisicao1.setStatusRequisicao(RequisicaoStatusEnum.ACEITO);
        requisicao1.setArquivada(true);
        requisicao1.setObservacao("observação exemplo");

        Certificado certificado = new Certificado();
        certificado.setId(1L);
        certificado.setTitulo("Título Exemplo");
        certificado.setObservacao("observação exemplo");
        certificado.setStatusCertificado(CertificadoStatusEnum.CONCLUIDO);
        certificado.setCargaHoraria(0);
        
        requisicao1.setCertificados(List.of(certificado));
        
        usuarioMock.setRequisicoes(List.of(requisicao1));
        
        when(jwtService.extractUsername(anyString())).thenReturn(usuarioEmail);
        when(usuarioServico.buscarUsuarioPorEmail(usuarioEmail)).thenReturn(usuarioMock);
        when(servico.listarRequisicoesArquivadas(usuarioEmail)).thenReturn(List.of(requisicao1));        
               
        mockMvc.perform(get("/api/requisicao/arquivar") 
        		 .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9." +
                         "eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ." +
                         "L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(requisicao1.getId()))
                .andExpect(jsonPath("$.[0].idRequisicao").value(requisicao1.getIdRequisicao()))
                .andExpect(jsonPath("$.[0].arquivada").value(true));

        verify(servico, times(1)).listarRequisicoesArquivadas(usuarioEmail);
     }
    
    
    @Test
    @WithMockUser
    public void testAdicionarRequisicao_Success() throws Exception {
        // Arrange
        String email = "aluno@example.com";
        String token = "Bearer someToken";
        
        when(request.getHeader("Authorization")).thenReturn(token);
        
        when(jwtService.extractUsername(anyString())).thenReturn(email);
        when(servico.adicionarRequisicao(email)).thenReturn(1L); 

        // Act & Assert
        mockMvc.perform(post("/api/requisicao") 
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(1L)); 
    }

    @Test
    @WithMockUser
    public void testAdicionarRequisicao_HoursExceeded() throws Exception {
        // Arrange
        String email = "aluno@example.com";
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsYXVyYS5zc2FudG9zQHVwZS5iciIsImlhdCI6MTczMDUxODQ1NiwiZXhwIjoxNzMwNjkxMjU2fQ.DGhx4r40mY9ZWH6qkQS1F2ZOxwmvzByL8ltOF9COe68";
        when(request.getHeader("Authorization")).thenReturn(token);

        when(jwtService.extractUsername(anyString())).thenReturn(email);
        doThrow(new AcsExcecao("O aluno já cumpriu suas horas complementares!"))
                .when(servico).adicionarRequisicao(email);

        mockMvc.perform(post("/api/requisicao") 
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void submeterRequisicao() throws Exception {
        Long requisicaoId = 1L;
        String tokenEsperado = "tokenExemplo";
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsYXVyYS5zc2FudG9zQHVwZS5iciIsImlhdCI6MTczMDUxODQ1NiwiZXhwIjoxNzMwNjkxMjU2fQ.DGhx4r40mY9ZWH6qkQS1F2ZOxwmvzByL8ltOF9COe68";
        when(request.getHeader("Authorization")).thenReturn(token);

        // Mock do comportamento do serviço
        when(servico.submeterRequisicao(requisicaoId)).thenReturn(tokenEsperado);

        mockMvc.perform(put("/api/requisicao/submissão/{id}", requisicaoId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value(tokenEsperado));

        verify(servico, times(1)).submeterRequisicao(requisicaoId);
    }
    
    @Test
    @WithMockUser
    void excluirRequisicao() throws Exception {
        Long requisicaoId = 1L;
        String email = "usuario@example.com";
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsYXVyYS5zc2FudG9zQHVwZS5iciIsImlhdCI6MTczMDUxODQ1NiwiZXhwIjoxNzMwNjkxMjU2fQ.DGhx4r40mY9ZWH6qkQS1F2ZOxwmvzByL8ltOF9COe68";
        when(request.getHeader("Authorization")).thenReturn(token);

        when(jwtService.extractUsername(anyString())).thenReturn(email);

//        doNothing().when(servico).excluirRequisicao(requisicaoId, email);

        mockMvc.perform(delete("/api/requisicao/{id}", requisicaoId)
                .header("Authorization", token)
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(servico, times(1)).excluirRequisicao(requisicaoId, email);
    }

    @Test
    @WithMockUser
    public void listarRequisicaoPorAlunoTest() throws Exception {
        Long alunoId = 1L;
        int pagina = 0;
        int quantidade = 10;
        EixoEnum eixo = EixoEnum.ENSINO;

        String token = "Bearer eyJhbGciOiJIUzI1NiJ9." +
                "eyJlbWFpbCI6ImVtYWlsX2RvX3VzdWFyaW9AZXhhbXBsZS5jb20ifQ." +
                "L3Zf85Hz4MF_yS5nByo2lY9GSCeZpmfrCbO_TnJQ-I0";

        Requisicao requisicao = new Requisicao();
        requisicao.setId(152L);
        requisicao.setIdRequisicao(null);
        requisicao.setStatusRequisicao(RequisicaoStatusEnum.TRANSITO);
        requisicao.setCriacao(new Date());
        requisicao.setDataDeSubmissao(new Date());
        requisicao.setStatusRequisicao(RequisicaoStatusEnum.TRANSITO);
        requisicao.setToken("token123");

        List<Requisicao> requisicoes = Arrays.asList(requisicao);

        Map<String, Object> requisicoesMap = new HashMap<>();
        requisicoesMap.put("requisicoes", requisicoes);
        requisicoesMap.put("paginaAtual", 0);
        requisicoesMap.put("totalItens", 1);
        requisicoesMap.put("totalPaginas", 1);

        when(servico.listarRequisicoesPorAlunoPaginadasEixo(alunoId, eixo, pagina, quantidade)).thenReturn(requisicoesMap);

        mockMvc.perform(get("/api/requisicao/eixo")
                        .param("alunoId", alunoId.toString())
                        .param("pagina", String.valueOf(pagina))
                        .param("quantidade", String.valueOf(quantidade))
                        .param("eixo", eixo.toString())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requisicoes.size()").value(requisicoes.size()))
                .andExpect(jsonPath("$.requisicoes[0].id").value(152L))
                .andExpect(jsonPath("$.requisicoes[0].idRequisicao").doesNotExist())
                .andExpect(jsonPath("$.requisicoes[0].statusRequisicao").value("TRANSITO"))
                .andExpect(jsonPath("$.paginaAtual").value(0))
                .andExpect(jsonPath("$.totalItens").value(1))
                .andExpect(jsonPath("$.totalPaginas").value(1));
    }


    
}
