package com.example.samuraitravel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests((requests) -> requests                
	            // 静的リソース、一般公開ページ、Stripe Webhook には誰でもアクセス可能
	            .requestMatchers(
	                "/css/**", "/images/**", "/js/**", "/storage/**",
	                "/", "/signup/**", "/houses", "/houses/{id}", "/stripe/webhook"
	            ).permitAll()

	            // /admin/** は管理者のみ
	            .requestMatchers("/admin/**").hasRole("ADMIN")

	            // その他のリクエストはログインが必要
	            .anyRequest().authenticated()
	        )

	        .formLogin((form) -> form
	            .loginPage("/login")                // ログインページ
	            .loginProcessingUrl("/login")       // ログイン処理のURL
	            .defaultSuccessUrl("/?loggedIn")    // ログイン成功時
	            .failureUrl("/login?error")         // ログイン失敗時
	            .permitAll()
	        )

	        .logout((logout) -> logout
	            .logoutSuccessUrl("/?loggedOut")    // ログアウト成功時
	            .permitAll()
	        )

	        // CSRF対策の例外指定：Stripe Webhook は外部サービスからのPOSTなのでCSRF対象外にする必要あり
	        // .csrf((csrf) -> csrf
	            // .ignoringRequestMatchers("/stripe/webhook")
	        // );
	    
	    .csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/stripe/webhook")));

	    return http.build();
	}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}