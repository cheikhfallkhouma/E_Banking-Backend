package com.cfk.ebankingbanking.security;

import com.nimbusds.jose.shaded.json.JSONUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.format.Printer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class SecurityController {
    private final AuthenticationManager authenticationManager;

    private final AuthenticationProvider authenticationProvider;
    private  final JwtEncoder jwtEncoder;
    @GetMapping("/profile")
    public Authentication authentication(Authentication authentication){
        return authentication;
    }

    @PostMapping("/login")
    public Map<String, String> login(String username, String password){
       //Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        //System.out.println("authenticationnnnnn:"+authentication);

        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        Instant instant = Instant.now();
        String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        //scope est la liste des autorités ou rôles qui sont séparées par un espace
        //GrantedAuthority::getAuthority = a->a.getAuthority()

      /*  Collection<GrantedAuthority> authorities = new ArrayList<>();
        appUser.getAppRoles().forEach(r-> {
            authorities.add(new SimpleGrantedAuthority(r.getRoleName()));
        });*/
        /*FunctionnalInterface fi1 = msg -> System.out.println(msg);
        FunctionnalInterface fi2 = System.out::println; */

        JwtClaimsSet jwtClaimsSet=JwtClaimsSet.builder()
                .issuedAt(instant)
                .expiresAt(instant.plus(10, ChronoUnit.MINUTES))
                .subject(username)
                .claim("scope", scope)
                .build();
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(),
                    jwtClaimsSet);//signature du jwt
        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        return Map.of("access-token", jwt);
    }

}


/*
    public class Main {
        static void print(String message){
            System.out.println(message);
        }
        public static void main(String[] args) {
            Printer p2 = Main::print;
        }

    }*/
/*
    public class Main {
         void print(String message){
            System.out.println(message);
        }
        public static void main(String[] args) {
        Main main = new Main();
            Printer p2 = main::print;
        }
    }*/

/*
    public class Main {
        Main(String message){
            System.out.println(message);
        }
        public static void main(String[] args) {
            Printer p2 = Main::new;
        }
    }*/


/*public interface Printer {
    void print(String message);
}*/
