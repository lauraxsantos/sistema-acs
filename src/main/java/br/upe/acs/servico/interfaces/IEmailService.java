package br.upe.acs.servico.interfaces;

import br.upe.acs.dominio.dto.EmailDTO;

public interface IEmailService {
    void enviar(EmailDTO emailInfo);
}
