package br.upe.acs.servico;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import br.upe.acs.dominio.*;
import br.upe.acs.repositorio.UsuarioRepositorio;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.upe.acs.config.JwtService;
import br.upe.acs.controlador.respostas.AutenticacaoResposta;
import br.upe.acs.dominio.dto.EnderecoDTO;
import br.upe.acs.dominio.dto.LoginDTO;
import br.upe.acs.dominio.dto.RegistroDTO;
import br.upe.acs.dominio.enums.PerfilEnum;
import br.upe.acs.utils.AcsExcecao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticacaoServico {

	private final UsuarioRepositorio usuarioRepositorio;

    private final JwtService jwtService;

    private final EnderecoServico enderecoServico;

    private final CursoServico cursoServico;

    private final EmailServico emailServico;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AutenticacaoResposta cadastrarUsuario(RegistroDTO registro) throws AcsExcecao {
        verificarDadosUnicos(registro.getEmail(), registro.getCpf());
		validarSenha(registro.getSenha());
		validarEmailInstitucional(registro.getEmail());
		validarMatricula(registro.getMatricula());
		validarPeriodo(registro.getPeriodo());

		Usuario usuarioSalvar = new Usuario();
		Endereco enderecoSalvo = adicionarEnderecoUsuario(registro);
		String codigoVerificacao = gerarCodigoVerificacao();
		Curso cursoSalvar = cursoServico.buscarCursoPorId(registro.getCursoId());

		usuarioSalvar.setNomeCompleto(registro.getNomeCompleto());
		usuarioSalvar.setCpf(registro.getCpf());
		usuarioSalvar.setMatricula(registro.getMatricula());
		usuarioSalvar.setPeriodo(registro.getPeriodo());
		usuarioSalvar.setTelefone(registro.getTelefone());
		usuarioSalvar.setEmail(registro.getEmail());
		usuarioSalvar.setSenha(passwordEncoder.encode(registro.getSenha()));
		usuarioSalvar.setCodigoVerificacao(codigoVerificacao);
		usuarioSalvar.setVerificado(false);
		usuarioSalvar.setEnabled(true);
		usuarioSalvar.setEndereco(enderecoSalvo);
        usuarioSalvar.setCurso(cursoSalvar);
        usuarioSalvar.setPerfil(PerfilEnum.ALUNO);

       	usuarioRepositorio.save(usuarioSalvar);

        CompletableFuture.runAsync(() -> emailServico.enviarEmailCodigoVerificacao(registro.getEmail(), codigoVerificacao));

        return gerarAutenticacaoResposta(usuarioSalvar);
    }

    public AutenticacaoResposta loginUsuario(LoginDTO login){
    	Usuario usuario = usuarioRepositorio.findByEmail(login.getEmail()).orElseThrow(() -> new AcsExcecao("Email não cadastrado") );
    	
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getSenha()));

		return gerarAutenticacaoResposta(usuario);
    }
    
    public String verificarUsuario(String email, String codigoVerificacao) {
        Usuario usuario = usuarioRepositorio.findByEmail(email).orElseThrow(() -> new AcsExcecao("Email não cadastrado"));

		if (usuario.isVerificado()) {
			throw new AcsExcecao("Este usuário já é verificado!");
		} else if (!codigoVerificacao.equals(usuario.getCodigoVerificacao())) {
			throw new AcsExcecao("O código de verificação está incorreto!");
		}
		usuario.setVerificado(true);
		usuarioRepositorio.save(usuario);
		return "Aluno verificado com sucesso!";
    }
    
	public String alterarCodigoVerificacao(String email) {
		Usuario usuario = usuarioRepositorio.findByEmail(email).orElseThrow(() -> new AcsExcecao("Usuario não encontrado"));

		if (usuario.isVerificado()) {
			throw new AcsExcecao("Usuário já é verificado!");
		}

		String novoCodigoVerificacao = gerarCodigoVerificacao();

		usuario.setCodigoVerificacao(novoCodigoVerificacao);

		usuarioRepositorio.save(usuario);

		CompletableFuture.runAsync(() -> emailServico.enviarEmailCodigoVerificacao(email, novoCodigoVerificacao));

		return "Código de verificação reenviado.";
	}
    
	public void alterarSenha(String email, String senha, String novaSenha) {
		validarSenha(novaSenha);
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, senha));
		Usuario usuario = usuarioRepositorio.findByEmail(email).orElseThrow(() -> new AcsExcecao("Usuario não encontrado"));
		usuario.setSenha(passwordEncoder.encode(novaSenha));
		usuarioRepositorio.save(usuario);
	}
	
    public void esquecerSenha(String email) {
        Usuario usuario = usuarioRepositorio.findByEmail(email).orElseThrow(() -> new AcsExcecao("USuario não encontrado"));

        Map<String, Object> claims = new HashMap<>();
        claims.put("recuperacao", true);
        String token = jwtService.generateToken(claims, usuario, 1000 * 60 * 15);
        CompletableFuture.runAsync(() -> emailServico.enviarEmailDeRecuperacaoDeSenha(usuario, token));

    }
    
    public void recuperarSenha(String token, String novaSenha) {
        boolean isTokenDeRecuperacao;

        try {
            isTokenDeRecuperacao = jwtService.extractClaim(token,
                    (claims -> claims.get("recuperacao", Boolean.class)));
        } catch (Exception e) {
            throw new AcsExcecao("Token inválido!");
        }

        if (!isTokenDeRecuperacao) {
            throw new AcsExcecao("Token inválido!");
        }

        String email = jwtService.extractUsername(token);

        Usuario usuario = usuarioRepositorio.findByEmail(email).orElseThrow(() -> new AcsExcecao("USuario não encontrado"));

         validarSenha(novaSenha);

        usuario.setSenha(passwordEncoder.encode(novaSenha));

        usuarioRepositorio.save(usuario);
    }

	private void verificarDadosUnicos(String email, String cpf) {
		String mensagem = "";
		
		String cpfFormatado = cpf.replaceAll("[^0-9]", "");
		
		if (usuarioRepositorio.findByCpf(cpfFormatado).isPresent()) {
			mensagem += "cpf";
		}

		if (usuarioRepositorio.findByEmail(email).isPresent()) {
			mensagem += "/email";
		}

		if (!mensagem.isBlank()) {
			throw new AcsExcecao("Os dados a seguir " + mensagem + " já estão cadastrados!");
		}
	}

	protected static void validarSenha(String senha) {
		boolean comMaiuscula = false, comMinuscula = false, comNumerico = false, comEspecial = false;

		if (senha.length() < 8) {
			throw new AcsExcecao("A senha informada deve ter 8 ou mais caracteres!");
		}

		for (char caracteres : senha.toCharArray()) {
			if (Character.isDigit(caracteres)) {
				comNumerico = true;
			} else if (Character.isUpperCase(caracteres)) {
				comMaiuscula = true;
			} else if (Character.isLowerCase(caracteres)) {
				comMinuscula = true;
			} else {
				comEspecial = true;
			}
		}

		if (!(comMaiuscula && comMinuscula && comNumerico && comEspecial)) {
			StringBuilder error = new StringBuilder("Senha inválida: A senha necessita de caracteres");

			if (!comMaiuscula) {
				error.append(" maiúsculos;");
			}
			if (!comMinuscula) {
				error.append(" minúsculos;");
			}
			if (!comNumerico) {
				error.append(" numéricos;");
			}
			if (!comEspecial) {
				error.append(" especiais;");
			}

			throw new AcsExcecao(error.toString());
		}
	}

	private void validarEmailInstitucional(String email) {
		if (!email.split("@")[1].equals("upe.br")) {
			throw new AcsExcecao("Email inválido! Por favor insira o email institucional válido.");
		}
	}
	
	private void validarMatricula(String matricula) {
		
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
	
	private void validarPeriodo(int periodo){
		if(periodo < 1 || periodo > 12) {
			throw new AcsExcecao("Por favor, insira um período válido");
		}
	}

	private Endereco adicionarEnderecoUsuario(RegistroDTO registro) {
		EnderecoDTO enderecoSalvar = new EnderecoDTO();
		enderecoSalvar.setCep(registro.getCep());
		enderecoSalvar.setCidade(registro.getCidade());
		enderecoSalvar.setBairro(registro.getBairro());
		enderecoSalvar.setRua(registro.getRua());
		enderecoSalvar.setNumero(registro.getNumero());
		enderecoSalvar.setUF(registro.getUF());
		enderecoSalvar.setComplemento(registro.getComplemento());

		return enderecoServico.adicionarEndereco(enderecoSalvar);
	}

	protected static String gerarCodigoVerificacao() {
		String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder codigo = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < 10; i++) {
			int indice = random.nextInt(caracteres.length());
			char caractere = caracteres.charAt(indice);
			codigo.append(caractere);
		}

		return codigo.toString();
	}

	private AutenticacaoResposta gerarAutenticacaoResposta(Usuario usuario) {
		String jwtToken = jwtService.generateToken(usuario);
		AutenticacaoResposta autenticacaoResposta = new AutenticacaoResposta();
		autenticacaoResposta.setToken(jwtToken);

		return autenticacaoResposta;
	}
}
