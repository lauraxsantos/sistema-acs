package br.upe.acs.servico;

import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.enums.RequisicaoStatusEnum;
import br.upe.acs.servico.interfaces.IRequisicaoPDF;
import br.upe.acs.utils.AcsExcecao;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class RequisicaoPDFCasoDeUso implements IRequisicaoPDF {

    private final RequisicaoServico servico;

    private final ITemplateEngine templateEngine;

    public RequisicaoPDFCasoDeUso(RequisicaoServico requisicaoServico, TemplateEngine templateEngine) {
        this.servico = requisicaoServico;
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(templateResolver);
        this.templateEngine = templateEngine;
    }

    public byte[] gerarRequisicaoPDF(Long id) {
        Requisicao requisicao = servico.buscarRequisicaoPorId(id);

        if (requisicao.getStatusRequisicao() != RequisicaoStatusEnum.TRANSITO) {
            throw new AcsExcecao("Não possivel gerar um pdf de uma requisição que não esteja trânsito!");
        }

        Context contexto = definirValoresTemplateHTML(requisicao);

        String htmlDoDocumento = templateEngine.process("requisicao.html", contexto);
        Path pathDoPdf = Paths.get("src/main/resources/requisicao" + id +".pdf");

        try (FileOutputStream outputStream = new FileOutputStream("src/main/resources/requisicao" + id +".pdf")) {
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);
            renderer.setDocumentFromString(htmlDoDocumento);
            renderer.layout();
            renderer.createPDF(outputStream);
            outputStream.close();

            byte[] arquivoPDF = Files.readAllBytes(pathDoPdf);
            Files.delete(pathDoPdf);
            return arquivoPDF;

        } catch (Exception e) {
            throw new AcsExcecao("Erro ao gerar o arquivo pdf!");
        }

    }

    private Context definirValoresTemplateHTML(Requisicao requisicao) {
        Context contexto = new Context();

        contexto.setVariable("protocolo", requisicao.getToken());
        contexto.setVariable("cpf", requisicao.getUsuario().getCpf());
        contexto.setVariable("data", requisicao.getDataDeSubmissao().toString());
        contexto.setVariable("nome", requisicao.getUsuario().getNomeCompleto());
        contexto.setVariable("curso", requisicao.getUsuario().getCurso().getNome());
        contexto.setVariable("periodo", requisicao.getUsuario().getPeriodo());
        contexto.setVariable("rua", requisicao.getUsuario().getEndereco().getRua());
        contexto.setVariable("bairro", requisicao.getUsuario().getEndereco().getBairro());
        contexto.setVariable("numero", requisicao.getUsuario().getEndereco().getNumero());
        contexto.setVariable("cidade", requisicao.getUsuario().getEndereco().getCidade());
        contexto.setVariable("uf", requisicao.getUsuario().getEndereco().getUF());
        contexto.setVariable("cep", requisicao.getUsuario().getEndereco().getCep());
        contexto.setVariable("email", requisicao.getUsuario().getEmail());
        contexto.setVariable("telefone", requisicao.getUsuario().getTelefone());

        return contexto;
    }

}
