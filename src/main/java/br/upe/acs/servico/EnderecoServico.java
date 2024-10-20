package br.upe.acs.servico;

import br.upe.acs.dominio.dto.ViaCepDTO;
import br.upe.acs.utils.CepInvalidoExcecao;
import org.springframework.stereotype.Service;

import br.upe.acs.dominio.Endereco;
import br.upe.acs.dominio.dto.EnderecoDTO;
import br.upe.acs.repositorio.EnderecoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class EnderecoServico {

	private final EnderecoRepositorio repositorio;

	public Endereco adicionarEndereco(EnderecoDTO enderecoDTO) {
		Endereco enderecoSalvar = new Endereco();		
		enderecoSalvar.setCep(enderecoDTO.getCep());
		enderecoSalvar.setUF(enderecoDTO.getUF());
		enderecoSalvar.setCidade(enderecoDTO.getCidade());
		enderecoSalvar.setBairro(enderecoDTO.getBairro());
		enderecoSalvar.setRua(enderecoDTO.getRua());
		enderecoSalvar.setNumero(enderecoDTO.getNumero());
		enderecoSalvar.setComplemento(enderecoDTO.getComplemento());
		return repositorio.save(enderecoSalvar);
	}

	public ViaCepDTO buscarEnderecoPorCep(String cep) {
		ViaCepDTO viaCepDTO;

		try {
			viaCepDTO = new RestTemplate().getForEntity(String.format("https://viacep.com.br/ws/%s/json/", cep), ViaCepDTO.class).getBody();
			assert viaCepDTO != null;
			if (viaCepDTO.getLocalidade().isEmpty()) {
				throw new CepInvalidoExcecao("CEP não encontrado!");
			}
		} catch (Exception e) {
			throw new CepInvalidoExcecao("CEP inválido!");
		}
		return viaCepDTO;
	}

}
