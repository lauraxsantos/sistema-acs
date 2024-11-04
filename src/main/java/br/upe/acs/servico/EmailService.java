package br.upe.acs.servico;

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
}
