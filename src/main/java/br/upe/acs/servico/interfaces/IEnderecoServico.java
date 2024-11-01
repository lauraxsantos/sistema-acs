package br.upe.acs.servico.interfaces;

import br.upe.acs.dominio.Endereco;
import br.upe.acs.dominio.dto.EnderecoDTO;
import br.upe.acs.dominio.dto.ViaCepDTO;

public interface IEnderecoServico {
	
	public Endereco adicionarEndereco(EnderecoDTO enderecoDTO);
	
	public ViaCepDTO buscarEnderecoPorCep(String cep); 

}
