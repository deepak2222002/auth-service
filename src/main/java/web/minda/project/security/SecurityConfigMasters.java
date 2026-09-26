package web.minda.project.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.CorsBeanDefinitionParser;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfigMasters {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPointObject;

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilterObject;

	@Autowired
	JwtAccessDeniedHandler jwtAccessDeniedHandlerObject;

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

					config.setAllowedOrigins(List.of(
						    "http://localhost:3000",
						    "https://localhost:8090"
						));

					config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

					config.setAllowedHeaders(List.of("*"));
					config.setAllowCredentials(true);

					return config;
				}))

				// ✅ ADD THIS BLOCK
//	        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

				/* ============== HEADERS ============== */
				.headers(headers -> headers.frameOptions(frame -> frame.disable()))

				/* ============== CSRF ============== */
				.csrf(csrf -> csrf.disable())

				/* ============== AUTHORIZATION RULES ============== */
				.authorizeHttpRequests(auth -> auth

						/* ---------- PUBLIC PAGES & STATIC ---------- */
						.requestMatchers("/loginpage", "/traininglogin", "/usertraininglogin", "/home",
								"/unauthorizeaccess", "/favicon.ico")
						.permitAll()

						.requestMatchers("/js/**", "/css/**", "/images/**", "/image/**", "/uploadImages/**",
								"/trainingResouces/**")
						.permitAll()

						/* ---------- PUBLIC CONTROLLERS ---------- */
						.requestMatchers("/auth/**", "/Controllers/image/**", "/Controllers/pd/image/**",
								"/Controllers/excel/images/**", "/Controllers/sign-status/**", "/Controllers/save/**",
								"/Controllers/getAuthorities", "/Controllers/MachineCheckSheetFieldImage/**",
								"/Controllers/rqcuploadImages/**", "/api/zpl/**")
						.permitAll()

						/* ---------- PUBLIC MODULES ---------- */
						.requestMatchers("/TrainingAndTest/**", "/TestAndTraining/**", "/trainingResouces/**",
								"/excelMasterSheet/**", "/projectLoginpage/**", "/projectManagementLoginpage/**",
								"/projectManagementDashboard/**", "/projectModuleReports/**")
						.permitAll()

						.requestMatchers("/TrainingDashboard/**").permitAll()

						.requestMatchers("/ExamTraining/**").permitAll()

//	            .requestMatchers("/dashboard/jobCardInformationReport")
//	            .permitAll()
//	            
						.requestMatchers("/Controllers/**").permitAll()

						.requestMatchers("/kafka/**").permitAll()

						/* ---------- ROLE BASED APIs ---------- */
						.requestMatchers("/dashboard/**").hasRole("SUPER ADMIN")

						.requestMatchers(HttpMethod.GET, "/masters/dashboard")
						.hasAnyRole("SUPER ADMIN", "RQC", "Process Engineering", "Engineering", "Maintenance", "PPC",
								"Quality", "HR", "Store", "Operations", "Production", "Plant Head", "Purchase")

						.requestMatchers(HttpMethod.GET, "/receivequalitychecking/dashboard")
						.hasAnyRole("SUPER ADMIN", "RQC", "Engineering", "Process Engineering", "Operations",
								"Plant Head")

						.requestMatchers(HttpMethod.GET, "/reports/dashboard")
						.hasAnyRole("SUPER ADMIN", "Production", "Quality", "PPC", "RQC", "Maintenance", "HR", "Store",
								"Engineering", "Process Engineering", "Operations", "Plant Head", "Purchase")

						/* 🔥 IMPORTANT FIX 🔥 */
						/* Let unknown URLs reach MVC → 404 */
						.anyRequest().permitAll())

				/* ============== EXCEPTION HANDLING ============== */
				.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPointObject)
						.accessDeniedHandler(jwtAccessDeniedHandlerObject))

				/* ============== SESSION ============== */
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		/* ============== JWT FILTER ============== */
		http.addFilterAfter(jwtAuthenticationFilterObject, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//
//		httpSecurity.authorizeHttpRequests((requests) -> requests.requestMatchers("/loginpage").permitAll()
//				.requestMatchers("/traininglogin").permitAll()
//				.requestMatchers("/usertraininglogin").permitAll()
//				.requestMatchers("/TrainingAndTest/**").permitAll()
//				.requestMatchers("/Controllers/excel/images/**").permitAll()
//				.requestMatchers("/home").permitAll()
//				.requestMatchers("/js/**").permitAll()
//				.requestMatchers("/css/**").permitAll()				
//				.requestMatchers("/images/**").permitAll()
//				.requestMatchers("/auth/**").permitAll()
//				.requestMatchers("/Controllers/pd/image/**").permitAll() // must be written before next line
//				.requestMatchers("/Controllers/sign-status/**").permitAll()
//				.requestMatchers("/favicon.ico").permitAll()
//				.requestMatchers("/image/**").permitAll()
//				.requestMatchers("/Controllers/image/**").permitAll()
//				.requestMatchers("/uploadImages/**").permitAll()
//			    .requestMatchers("/Controllers/MachineCheckSheetFieldImage/**").permitAll()
//			    .requestMatchers("/Controllers/rqcuploadImages/**").permitAll()
//			    .requestMatchers("/trainingResouces/**").permitAll()
//				.requestMatchers("/excelMasterSheet/**").permitAll()
//				.requestMatchers("/trainingResouces/**").permitAll()
//				.requestMatchers("/Controllers/save/**").permitAll()
//				.requestMatchers("/Controllers/**").authenticated()
//				
//
//				
////				.requestMatchers(HttpMethod.POST, "/admin/**")
////				.hasAnyRole("SUPER ADMIN")
//				
////				  these are on basis of department ( modules are accessible based on the department)
//				.requestMatchers(HttpMethod.GET, "/masters/dashboard")
//				.hasAnyRole("SUPER ADMIN", "RQC", "Process Engineering", "Engineering", "Maintenance", "PPC",
//						"Quality", "HR", "Store", "Operations", "Production", "Plant Head", "Purchase")
//				
//				.requestMatchers(HttpMethod.GET, "/receivequalitychecking/dashboard")
//				.hasAnyRole("SUPER ADMIN", "RQC", "Engineering", "Process Engineering", "Operations","Plant Head")
//				
//				.requestMatchers(HttpMethod.GET, "/reports/dashboard")
//				.hasAnyRole("SUPER ADMIN", "Production", "Quality", "PPC", "RQC", "Maintenance", "HR", "Store",
//						"Engineering", "Process Engineering", "Operations","Plant Head","Purchase")
//
//				.anyRequest().authenticated())
//				.csrf(csrf -> csrf.disable())
//
//				.exceptionHandling(ex -> ex.accessDeniedPage("/unauthorizeaccess")
//						.authenticationEntryPoint(jwtAuthenticationEntryPointObject))
//				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//		httpSecurity.addFilterAfter(jwtAuthenticationFilterObject, UsernamePasswordAuthenticationFilter.class);
//
//		return httpSecurity.build();
//	}

}
