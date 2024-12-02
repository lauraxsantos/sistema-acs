package br.upe.acs.servico;

import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.dto.EmailDTO;
import br.upe.acs.servico.interfaces.IEmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {

    private final JavaMailSender emailRemetente;

    public EmailService(JavaMailSender emailRemetente) {
        this.emailRemetente = emailRemetente;
    }

    @Override
    public void enviar(EmailDTO emailInfo) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("lapesupe@gmail.com");
        email.setTo(emailInfo.getDestinatario());
        email.setText(emailInfo.getMensagem());
        email.setSubject(emailInfo.getAssunto());

        emailRemetente.send(email);
    }
    
    @Override
    public void enviarEmailCodigoVerificacao(String email, String codigoVerificacao) {
		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setAssunto("Código de verificação - Sistema ACs UPE");
		emailDTO.setDestinatario(email);
		emailDTO.setMensagem(
				"Para confirmar seu email no Sistema ACs UPE:\n" +
						"1 - Realize o login no sistema\n" +
						"2 - Insira e envie este código na página de verificação do sistema: " + codigoVerificacao);
		enviar(emailDTO);
	}

    @Override
	public void enviarEmailAlteracaoStatusRequisicao(Requisicao requisicao) {
		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setDestinatario(requisicao.getUsuario().getEmail());
		emailDTO.setAssunto("Modificação na sua requisição " + requisicao.getId() + " - Sistema ACs UPE");
		emailDTO.setMensagem("Gostaríamos de informar que sua requisição " + requisicao.getId()
				+ " teve seu status alterado para "  + requisicao.getStatusRequisicao().name() + ".\n" +
				"Para mais informações acesse o Sistema de ACs. " +
				"Em caso de erros entre em contato com o turmaestest@gmail.com.\n" +
				"Atenciosamente,\nCoordenação de " + requisicao.getCurso().getNome() + ".");
		enviar(emailDTO);
	}

    @Override
	public void enviarEmailDeRecuperacaoDeSenha(Usuario usuario, String token) {
		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setDestinatario(usuario.getEmail());
		emailDTO.setAssunto("Solicitação de recuperação de conta - Sistema ACs UPE");
		emailDTO.setMensagem("Olá, " + usuario.getNomeCompleto() + ", \n" +
				"Nós recebemos um pedido para redefinir a senha do e-mail " + usuario.getEmail()+".\n" +
				"Clique no link abaixo para redefinir sua senha:\n" +
				System.getenv("FRONTEND_URL") + "/account/reset/" + token);
		enviar(emailDTO);
	}
}
