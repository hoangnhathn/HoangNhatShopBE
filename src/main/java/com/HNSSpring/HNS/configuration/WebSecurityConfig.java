package com.HNSSpring.HNS.configuration;

import com.HNSSpring.HNS.filters.JwtTokenFilter;
import com.HNSSpring.HNS.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableMethodSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request->{
                    request
                            .requestMatchers(
                                    String.format("%s/users/register",apiPrefix),
                                    String.format("%s/users/login",apiPrefix)
                            )
                            .permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/categories**",apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/categories/**",apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/categories/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/categories/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/categories/**",apiPrefix)).hasAnyRole(Role.ADMIN)


                            .requestMatchers(GET,
                                    String.format("%s/products/images/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/products/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/products**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/products/uploads/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST,
                                    String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(PUT,
                                    String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)


                            .requestMatchers(GET,
                                    String.format("%s/orders/get-orders-by-keyword/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/orders/user/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Role.USER)
                            .requestMatchers(PUT,
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/orders/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/orders/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/orders**", apiPrefix)).permitAll()




                            .requestMatchers(GET,
                                    String.format("%s/order_details/**",apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .requestMatchers(POST,
                                    String.format("%s/order_details/**",apiPrefix)).hasAnyRole(Role.USER)
                            .requestMatchers(PUT,
                                    String.format("%s/order_details/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/order_details/**",apiPrefix)).hasAnyRole(Role.ADMIN)

                            .requestMatchers(GET,
                                    String.format("%s/roles**",apiPrefix)).permitAll()



                            .requestMatchers(GET, String.format("%s/slides/images/**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/slides**", apiPrefix)).permitAll()
                            .requestMatchers(GET, String.format("%s/slides/**", apiPrefix)).permitAll()
                            .requestMatchers(POST, String.format("%s/slides/uploads/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(POST, String.format("%s/slides/**", apiPrefix)).hasRole(Role.ADMIN)
                            .requestMatchers(DELETE, String.format("%s/slides/**", apiPrefix)).hasRole(Role.ADMIN)

                            .requestMatchers(PUT,
                                    String.format("%s/users/details/**",apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .requestMatchers(PUT,
                                    String.format("%s/users/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/users/details",apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .requestMatchers(GET,
                                    String.format("%s/users/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(DELETE,
                                    String.format("%s/users/**",apiPrefix)).hasAnyRole(Role.ADMIN)
                            .requestMatchers(GET,
                                    String.format("%s/users**",apiPrefix)).hasAnyRole(Role.ADMIN)


                            .requestMatchers(GET,
                                    String.format("%s/reviews**",apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/reviews/**",apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .requestMatchers(PUT,
                                    String.format("%s/reviews/**",apiPrefix)).hasAnyRole(Role.ADMIN,Role.USER)
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                //.cors(AbstractHttpConfigurer::disable)
        ;
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization","content-type","x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
        });
        return http.build();
    }
}
