package com.stationery.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Autowired
    private RouterValidator routerValidator;

    @Value("${jwt.secret}")
    private String secret;

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (routerValidator.isSecured.test(exchange.getRequest())) {
                // Check if authorization header is present
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                try {
                    // Validate Token and Extract Claims
                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(secret)))
                            .build()
                            .parseClaimsJws(authHeader)
                            .getBody();
                    
                    // Add user details to headers so microservices can use them
                    exchange.getRequest().mutate()
                            .header("loggedInUser", claims.getSubject())
                            .header("loggedInRole", claims.get("role", String.class))
                            .build();

                } catch (Exception e) {
                    System.out.println("Invalid token... " + e.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {
        // Configuration properties can go here
    }
}
