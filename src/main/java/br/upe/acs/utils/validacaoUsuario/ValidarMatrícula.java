package br.upe.acs.utils.validacaoUsuario;

import br.upe.acs.utils.AcsExcecao;

public class ValidarMatrícula implements IValidacaoUsuario {
	
	@Override
	public void isValidacao(String matricula) {
		
		if(!matricula.matches("[0-9]+")) {
			throw new AcsExcecao("Por favor, insira uma matrícula válida");
		}

		if(matricula.length() < 4 || matricula.length() > 9) {
			throw new AcsExcecao("Por favor, insira uma matrícula válida");			
		}

		if(Integer.parseInt(matricula) < 1) {
			throw new AcsExcecao("Por favor, insira uma matrícula válida");	
		}		
			
	}


}
