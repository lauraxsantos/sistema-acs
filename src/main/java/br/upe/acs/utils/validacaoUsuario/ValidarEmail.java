package br.upe.acs.utils.validacaoUsuario;

import br.upe.acs.utils.AcsExcecao;

public class ValidarEmail implements IValidacaoUsuario {

	@Override
	public void isValidacao(String email) {
		System.out.println(email);
		if (!email.split("@")[1].equals("upe.br")) {
			throw new AcsExcecao("Email inválido! Por favor insira o email institucional válido.");
		}		
	}

}
