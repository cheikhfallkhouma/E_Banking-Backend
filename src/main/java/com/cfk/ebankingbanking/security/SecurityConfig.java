package com.cfk.ebankingbanking.security;

import com.cfk.ebankingbanking.entities.Customer;
import com.cfk.ebankingbanking.repositories.CustomerRepository;
import com.cfk.ebankingbanking.security.entities.AppUser;
import com.cfk.ebankingbanking.security.repositories.AppUserRepository;
import com.cfk.ebankingbanking.security.service.ClaimsService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
//@EnableSwagger2

//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String secretKey;


 /*   @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
       PasswordEncoder passwordEncoder = passwordEncoder();
        return new InMemoryUserDetailsManager(
                User.withUsername("user1").password(passwordEncoder.encode("12345")).authorities("USER").build(),
                User.withUsername("admin").password(passwordEncoder.encode("12345")).authorities("USER", "ADMIN").build());
    }*/

    @Autowired
    private ClaimsService claimsService;
    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private CustomerRepository customerRepository;

    protected void configure() throws Exception {
        authenticationManagerBuilder.userDetailsService(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Customer appUser =  customerRepository.findByName(username);
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                appUser.getAppRoles().forEach(r->{
                    authorities.add(new SimpleGrantedAuthority(r.getRoleName()));
                });
                return new User(appUser.getName(), appUser.getPassword(), authorities);
            }
        });
    }
    //private CustomerRepository customerRepository;

    public SecurityConfig(CustomerRepository customerRepository, AppUserRepository appUserRepository) {
        this.customerRepository = customerRepository;
        //this.appUserRepository = appUserRepository;
    }

   // private AppUserRepository appUserRepository;

/*
    public UserDetails loadUserByUserName(String username){
        AppUser appUser =  appUserRepository.findByUserName(username);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        appUser.getAppRoles().forEach(r->{
            authorities.add(new SimpleGrantedAuthority(r.getRoleName()));
        });
        return new User(appUser.getUserName(), appUser.getPassword(), authorities);
    }
*/

    @Bean()
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        /*return httpSecurity
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(ar->  ar.antMatchers("http://localhost:8085/auth/login").permitAll())
                .authorizeHttpRequests(ar-> ar.anyRequest().authenticated())
                //.httpBasic(Customizer.withDefaults())
                //.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .oauth2ResourceServer(oa -> oa.jwt(Customizer.withDefaults()))
                .build();*/


        return httpSecurity
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(ar -> ar
                      //  .antMatchers("/auth/login").permitAll() // Autoriser l'accès à /auth/login à tout le monde

                                //.antMatchers(HttpMethod.GET,"/swagger-resources/**").permitAll().antMatchers(HttpMethod.GET,"/swagger-ui/**").permitAll().antMatchers(HttpMethod.GET,  "/swagger-ui.html").permitAll()
                        //.antMatchers(HttpMethod.GET,"/v3/api-docs").permitAll()
                      // .antMatchers(HttpMethod.GET,"/swagger-ui.html","/webjars/**", "/v2/api-docs", "/swagger-resources/**").permitAll()
                        .anyRequest().permitAll() // Toutes les autres requêtes nécessitent une authentification
                )

                //.httpBasic(Customizer.withDefaults())
                //.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .oauth2ResourceServer(oa -> oa.jwt(Customizer.withDefaults()))
                .build();

    }


    @Bean
    JwtEncoder jwtEncoder(){
      // H512 EST symétrique  String secretKey = "6c9ed72d57f833001c7044537557f18c127d876c12f1ccfe6eef41c33188cce8d823690c26f1908ac9599d8d9ec9b38fdd77b6cae8dfa1aec9b97b5be0416124";
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey.getBytes()));
    }

    @Bean
    JwtDecoder jwtDecoder(){
        //String secretKey = "6c9ed72d57f833001c7044537557f18c127d876c12f1ccfe6eef41c33188cce8d823690c26f1908ac9599d8d9ec9b38fdd77b6cae8dfa1aec9b97b5be0416124";
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "RSA");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm((MacAlgorithm.HS512)).build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

     //@Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setExposedHeaders(List.of("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}

