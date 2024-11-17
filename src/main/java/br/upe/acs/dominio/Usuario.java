package br.upe.acs.dominio;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.upe.acs.dominio.enums.PerfilEnum;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String nomeCompleto;
	
	private String cpf;
	
	private String matricula;
	
	private int periodo;
	
	private String telefone;
	
	private String email;
	
	private String senha;
	
	private String codigoVerificacao;
	
	private boolean isVerificado;
	
    private float horasEnsino;

    private float horasExtensao;

    private float horasGestao;

    private float horasPesquisa;

	private boolean enabled;

	@Enumerated(EnumType.STRING)
    private PerfilEnum perfil;
    
    @OneToMany(mappedBy = "usuario")
    private List<Requisicao> requisicoes;
    
    @OneToMany(mappedBy = "comissao")
    private List<Requisicao> requisicoesCoordenador;
    
    @ManyToOne
    private Endereco endereco;
    
	@ManyToOne
	private Curso curso;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(getPerfil().name()));
	};

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
}
