package br.upe.acs.utils.validacaoUsuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ValidacaoUtils {
	
    private Map<String, IValidacaoUsuario> validators;

    public ValidacaoUtils() {
        validators = new HashMap<>();
        validators.put("senha", new ValidarSenha());
        validators.put("email", new ValidarEmail());
        validators.put("matricula", new ValidarMatrícula());
        validators.put("periodo", new ValidarPeriodo());
    }

    public void validate(String tipo, String input) {
        IValidacaoUsuario validator = validators.get(tipo);
        if (validator != null) {
            validator.isValidacao(input);
        } else {
            throw new IllegalArgumentException("Validador não encontrado para o tipo: " + tipo);
        }
    }

}
