package br.upe.acs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
	
	private final JwtAuthenticationFilter jwtAuthFilter;
	
	private final AuthenticationProvider authenticationProvider;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers("/api/auth/acesso/**", "/v3/**", "/swagger-ui/**")
						.permitAll()
						.requestMatchers(HttpMethod.GET,"/api/endereco/**", "/api/curso/**")
						.permitAll()
						.requestMatchers("api/usuario/requisicao/paginacao/**")
						.hasAnyAuthority("COORDENADOR", "ADMINISTRADOR")
					.anyRequest().authenticated()
			)
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.cors(cors -> {
				try {
					cors.init(http);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});

		return http.build();
	}
}
