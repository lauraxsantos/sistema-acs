package br.upe.acs.utils;

import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.servico.interfaces.IEmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.upe.acs.dominio.dto.EmailDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailUtils {

	private final IEmailService emailService;

	public void enviarEmail(EmailDTO emailInfo) {
		emailService.enviar(emailInfo);
	}

	public void enviarEmailCodigoVerificacao(String email, String codigoVerificacao) {
		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setAssunto("Código de verificação - Sistema ACs UPE");
		emailDTO.setDestinatario(email);
		emailDTO.setMensagem(
				"Para confirmar seu email no Sistema ACs UPE:\n" +
						"1 - Realize o login no sistema\n" +
						"2 - Insira e envie este código na página de verificação do sistema: " + codigoVerificacao);
		enviarEmail(emailDTO);
	}

	public void enviarEmailAlteracaoStatusRequisicao(Requisicao requisicao) {
		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setDestinatario(requisicao.getUsuario().getEmail());
		emailDTO.setAssunto("Modificação na sua requisição " + requisicao.getId() + " - Sistema ACs UPE");
		emailDTO.setMensagem("Gostaríamos de informar que sua requisição " + requisicao.getId()
				+ " teve seu status alterado para "  + requisicao.getStatusRequisicao().name() + ".\n" +
				"Para mais informações acesse o Sistema de ACs. " +
				"Em caso de erros entre em contato com o turmaestest@gmail.com.\n" +
				"Atenciosamente,\nCoordenação de " + requisicao.getCurso().getNome() + ".");
		enviarEmail(emailDTO);
	}

	public void enviarEmailDeRecuperacaoDeSenha(Usuario usuario, String token) {
		EmailDTO emailDTO = new EmailDTO();
		emailDTO.setDestinatario(usuario.getEmail());
		emailDTO.setAssunto("Solicitação de recuperação de conta - Sistema ACs UPE");
		emailDTO.setMensagem("Olá, " + usuario.getNomeCompleto() + ", \n" +
				"Nós recebemos um pedido para redefinir a senha do e-mail " + usuario.getEmail()+".\n" +
				"Clique no link abaixo para redefinir sua senha:\n" +
				System.getenv("FRONTEND_URL") + "/account/reset/" + token);
		enviarEmail(emailDTO);
	}
}
