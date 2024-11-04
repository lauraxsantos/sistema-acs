package br.upe.acs.servico.interfaces;

import br.upe.acs.dominio.Requisicao;
import br.upe.acs.dominio.Usuario;
import br.upe.acs.dominio.dto.EmailDTO;

public interface IEmailService {
    void enviar(EmailDTO emailInfo);

	void enviarEmailCodigoVerificacao(String email, String codigoVerificacao);

	void enviarEmailAlteracaoStatusRequisicao(Requisicao requisicao);

	void enviarEmailDeRecuperacaoDeSenha(Usuario usuario, String token);
}
