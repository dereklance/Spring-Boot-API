package com.smoothstack.orchestrator.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smoothstack.orchestrator.entity.LoginCredentials;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    // triggered on POST request to /login
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        LoginCredentials credentials = null;
        try {
            credentials = new ObjectMapper().readValue(request.getReader(), LoginCredentials.class);
        } catch (JsonParseException | JsonMappingException ex) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                credentials.getEmail(), credentials.getPassword(), new ArrayList<>());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();

        String token = JWT.create().withSubject(userDetails.getUserId().toString())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()));

        System.out.println("successfully logged in. created jwt cookie...");
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setMaxAge(JwtProperties.EXPIRATION_TIME);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);

        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        response.getWriter().printf("{ \"firstName\" : \"%s\", \"lastName\" : \"%s\" }",
            userDetails.getFirstName(), userDetails.getLastName());
    }
}
