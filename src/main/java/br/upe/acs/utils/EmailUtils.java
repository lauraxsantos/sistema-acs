package br.upe.acs.utils;

import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.servico.interfaces.IEmailService;
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
		emailService.enviarEmailCodigoVerificacao(email, codigoVerificacao);
	}

	public void enviarEmailAlteracaoStatusRequisicao(Requisicao requisicao) {
		emailService.enviarEmailAlteracaoStatusRequisicao(requisicao);
	}

	public void enviarEmailDeRecuperacaoDeSenha(Usuario usuario, String token) {
		emailService.enviarEmailDeRecuperacaoDeSenha(usuario, token);
	}
}
