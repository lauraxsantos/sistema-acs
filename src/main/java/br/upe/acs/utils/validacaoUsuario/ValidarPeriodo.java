package br.upe.acs.utils.validacaoUsuario;

import br.upe.acs.utils.AcsExcecao;

public class ValidarPeriodo implements IValidacaoUsuario {
	
	@Override
	public void isValidacao(String periodo){		
		if(Integer.parseInt(periodo) < 1 || Integer.parseInt(periodo) > 12) {
			throw new AcsExcecao("Por favor, insira um período válido");
		}
	}

}
