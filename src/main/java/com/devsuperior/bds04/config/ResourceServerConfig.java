package com.devsuperior.bds04.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{

	// Environment - Ambiente de execusão da aplicação
	@Autowired
	private Environment env;
	
	
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	//Defindo constantes para liberar as rotas
	private static final String[] PUBLIC = { "/oauth/token", "/h2-console/**" };
	
	private static final String[] PUBLIC_GET = { "/cities/**", "/events/**" };
	
	private static final String[] EVENTS_POST = { "/events" };
	

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		// Configurar o tokenStore
		// Com isso o resource server será capaz de decodificar o token e analisar se o token é válido bate com o secret se está expirado e etc... 
		// tokenStore é um beam criado na classe appConfig
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// Configurar as rotas 
		// Quem pode acessar?
		
		// Para liberar o H2 -  se nos profile ativos contem o profile test
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}
		
		http.authorizeRequests()
		.antMatchers(PUBLIC).permitAll()
		.antMatchers(HttpMethod.GET, PUBLIC_GET).permitAll()
		.antMatchers(HttpMethod.POST, EVENTS_POST).hasAnyRole("CLIENT", "ADMIN")
		.anyRequest().hasAnyRole("ADMIN");
	}

	
}
