package web.minda.project.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfigMasters {

	@Value("${app.cors.allowed-origins}")
	private String allowedOrigins;

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPointObject;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilterObject;

	@Autowired
	private JwtAccessDeniedHandler jwtAccessDeniedHandlerObject;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomeUserDetailService();
	}

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

		daoAuthenticationProvider.setUserDetailsService(userDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());

		return daoAuthenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {

		return builder.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http

				/* ================= CORS ================= */

				/* ================= CORS ================= */

				.cors(cors -> cors.configurationSource(request -> {

					CorsConfiguration config = new CorsConfiguration();

					config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

					config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

					config.setAllowedHeaders(List.of("*"));
					config.setAllowCredentials(true);

					return config;
				}))

				/* ================= HEADERS ================= */

				.headers(headers -> headers.frameOptions(frame -> frame.disable()))

				/* ================= CSRF ================= */

				.csrf(csrf -> csrf.disable())

				/* ================= AUTHORIZATION ================= */

				.authorizeHttpRequests(auth -> auth

						/* ---------- PUBLIC PAGES & STATIC ---------- */

						.requestMatchers(new AntPathRequestMatcher("/loginpage"),
								new AntPathRequestMatcher("/traininglogin"),
								new AntPathRequestMatcher("/usertraininglogin"), new AntPathRequestMatcher("/home"),
								new AntPathRequestMatcher("/unauthorizeaccess"),
								new AntPathRequestMatcher("/favicon.ico"))
						.permitAll()

						.requestMatchers(new AntPathRequestMatcher("/js/**"), new AntPathRequestMatcher("/css/**"),
								new AntPathRequestMatcher("/images/**"), new AntPathRequestMatcher("/image/**"),
								new AntPathRequestMatcher("/uploadImages/**"),
								new AntPathRequestMatcher("/trainingResouces/**"))
						.permitAll()

						/* ---------- PUBLIC CONTROLLERS ---------- */

						.requestMatchers(new AntPathRequestMatcher("/auth/**"),
								new AntPathRequestMatcher("/Controllers/image/**"),
								new AntPathRequestMatcher("/Controllers/pd/image/**"),
								new AntPathRequestMatcher("/Controllers/excel/images/**"),
								new AntPathRequestMatcher("/Controllers/sign-status/**"),
								new AntPathRequestMatcher("/Controllers/save/**"),
								new AntPathRequestMatcher("/Controllers/getAuthorities"),
								new AntPathRequestMatcher("/Controllers/MachineCheckSheetFieldImage/**"),
								new AntPathRequestMatcher("/Controllers/rqcuploadImages/**"),
								new AntPathRequestMatcher("/api/zpl/**"))
						.permitAll()

						/* ---------- PUBLIC MODULES ---------- */

						.requestMatchers(new AntPathRequestMatcher("/TrainingAndTest/**"),
								new AntPathRequestMatcher("/TestAndTraining/**"),
								new AntPathRequestMatcher("/trainingResouces/**"),
								new AntPathRequestMatcher("/excelMasterSheet/**"),
								new AntPathRequestMatcher("/projectLoginpage/**"),
								new AntPathRequestMatcher("/projectManagementLoginpage/**"),
								new AntPathRequestMatcher("/projectManagementDashboard/**"),
								new AntPathRequestMatcher("/projectEmployeeDashboard/**"),
								new AntPathRequestMatcher("/projectModuleReports/**"))
						.permitAll()

						.requestMatchers(new AntPathRequestMatcher("/TrainingDashboard/**")).permitAll()

						.requestMatchers(new AntPathRequestMatcher("/ExamTraining/**")).permitAll()

						.requestMatchers(new AntPathRequestMatcher("/Controllers/**")).permitAll()

						.requestMatchers(new AntPathRequestMatcher("/kafka/**")).permitAll()

						/* ---------- ROLE BASED APIs ---------- */

						.requestMatchers(new AntPathRequestMatcher("/dashboard/**")).hasRole("SUPER ADMIN")

						.requestMatchers(new AntPathRequestMatcher("/masters/dashboard", HttpMethod.GET.name()))
						.hasAnyRole("SUPER ADMIN", "RQC", "Process Engineering", "Engineering", "Maintenance", "PPC",
								"Quality", "HR", "Store", "Operations", "Production", "Plant Head", "Purchase")

						.requestMatchers(
								new AntPathRequestMatcher("/receivequalitychecking/dashboard", HttpMethod.GET.name()))
						.hasAnyRole("SUPER ADMIN", "RQC", "Engineering", "Process Engineering", "Operations",
								"Plant Head")

						.requestMatchers(new AntPathRequestMatcher("/reports/dashboard", HttpMethod.GET.name()))
						.hasAnyRole("SUPER ADMIN", "Production", "Quality", "PPC", "RQC", "Maintenance", "HR", "Store",
								"Engineering", "Process Engineering", "Operations", "Plant Head", "Purchase")

						/* ---------- DEFAULT ---------- */

						.anyRequest().permitAll())

				/* ================= EXCEPTION HANDLING ================= */

				.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPointObject)
						.accessDeniedHandler(jwtAccessDeniedHandlerObject))

				/* ================= SESSION ================= */

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		/* ================= JWT FILTER ================= */

		http.addFilterAfter(jwtAuthenticationFilterObject, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}