package br.upe.acs.controlador.respostas;

import br.upe.acs.dominio.Endereco;
import lombok.Getter;

@Getter
public class EnderecoResposta {

	private final Long id;
	
	private final String cep;
	
	private final String rua;
	
	private final String bairro;
	
	private final String cidade;
	
	private final String UF;
	
	private final int numero;

	public EnderecoResposta(Endereco endereco) {
		super();
		this.id = endereco.getId();
		this.cep = endereco.getCep();
		this.rua = endereco.getRua();
		this.bairro = endereco.getBairro();
		this.cidade = endereco.getCidade();
		this.UF = endereco.getUF();
		this.numero = endereco.getNumero();
	}
	
}
