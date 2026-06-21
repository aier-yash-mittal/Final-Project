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
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {  // Custom filter for JWT authentication

    @Autowired
    private RouterValidator routerValidator; // RouterValidator is used to determine if the request is for a secured endpoint or not

    @Value("${jwt.secret}")  
    private String secret;  

    public JwtAuthenticationFilter() { 
        super(Config.class);        
    }

    @Override
    public GatewayFilter apply(Config config) { 
        return ((exchange, chain) -> {  //
            if (routerValidator.isSecured.test(exchange.getRequest())) {                              //jwt chahiye ya nahi
                // Check if authorization header is present                                    
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {    // Agar nahi hai to 401 Unauthorized
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();                                     //return complete response without going to next filter
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);  // Get the token from the header
                if (authHeader != null && authHeader.startsWith("Bearer ")) {                                   // Remove "Bearer " prefix to get the actual token
                    authHeader = authHeader.substring(7);                                                       // Extract the token from the header
                }

                try {
                    // Validate Token and Extract Claims
                    Claims claims = Jwts.parserBuilder()  // Parse the JWT token and validate it using the secret key
                            .setSigningKey(io.jsonwebtoken.security.Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(secret))) // Secret key ko decode karke signing key set karna
                            .build() // Build the JWT parser
                            .parseClaimsJws(authHeader) // Parse the token and extract claims if valid, otherwise throw an exception
                            .getBody(); // Get the claims from the token
                    
                    // Add user details to headers so microservices can use them
                    exchange.getRequest().mutate() 
                            .header("loggedInUser", claims.getSubject()) // Subject usually contains the username or email of the user
                            .header("loggedInRole", claims.get("role", String.class)) // Custom claim "role" se user role extract karna
                            .build();// Build the mutated request with new headers

                } catch (Exception e) { // If token is invalid or expired, return 401 Unauthorized
                    System.out.println("Invalid token... " + e.getMessage());// Log the exception for debugging purposes
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);// Set the response status to 401 Unauthorized
                    return exchange.getResponse().setComplete();// Return complete response without going to next filter
                }
            }
            return chain.filter(exchange); // If the endpoint is not secured or token is valid, continue to the next filter in the chain
        });
    }

    public static class Config { 
        // Configuration properties can go here
    }
}
