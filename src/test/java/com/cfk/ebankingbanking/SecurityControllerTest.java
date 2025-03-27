package com.cfk.ebankingbanking;

import com.cfk.ebankingbanking.security.SecurityController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private SecurityController securityController;

    @Test
    public void testLogin() {
        // Mock authentication result
        String username = "testUser";
        String password = "testPassword";
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username,
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Mock JWT encoding
        Instant instant = Instant.now();
        when(jwtEncoder.encode(any())).thenAnswer(invocation -> {
            // Simulate JWT encoding
            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .issuedAt(instant)
                    .expiresAt(instant.plus(10, ChronoUnit.MINUTES))
                    .subject(username)
                    .claim("scope", "ROLE_USER")
                    .build();
            return new Jwt("dummyToken");
        });

        // Call the login method
        Map<String, String> result = securityController.login(username, password);

        // Verify authentication manager was called with the correct parameters
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // Verify JWT encoder was called with the correct parameters
        verify(jwtEncoder).encode(any());

        // Assert the result
        assertEquals(1, result.size());
        assertEquals("dummyToken", result.get("access-token"));
    }

    // Dummy class for representing a JWT
    private static class Jwt {
        private final String tokenValue;

        public Jwt(String tokenValue) {
            this.tokenValue = tokenValue;
        }

        public String getTokenValue() {
            return tokenValue;
        }
    }
}
