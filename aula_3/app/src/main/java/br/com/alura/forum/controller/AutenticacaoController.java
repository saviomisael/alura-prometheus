package br.com.alura.forum.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.alura.forum.config.security.TokenService;
import br.com.alura.forum.controller.dto.TokenDto;
import br.com.alura.forum.controller.form.LoginForm;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/auth")
// @Profile(value = {"prod", "test"})
public class AutenticacaoController {

	Counter authUserSuccess;
	Counter authUserErrors;

	private MeterRegistry meterRegistry;

	public AutenticacaoController(MeterRegistry registry) {
		meterRegistry = registry;

	}

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private TokenService tokenService;

	@PostMapping
	public ResponseEntity<TokenDto> autenticar(@RequestBody @Valid LoginForm form) {
		UsernamePasswordAuthenticationToken dadosLogin = form.converter();

		String username = form.getEmail().split("@")[0];

		authUserSuccess = Counter.builder("auth_user_success_" + username)
				.description("usuarios autenticados")
				.register(meterRegistry);

		authUserErrors = Counter.builder("auth_user_error_" + username)
				.description("erros de login")
				.register(meterRegistry);

		try {
			Authentication authentication = authManager.authenticate(dadosLogin);
			String token = tokenService.gerarToken(authentication);
			authUserSuccess.increment();
			return ResponseEntity.ok(new TokenDto(token, "Bearer"));

		} catch (AuthenticationException e) {
			authUserErrors.increment();
			return ResponseEntity.badRequest().build();
		}
	}
}