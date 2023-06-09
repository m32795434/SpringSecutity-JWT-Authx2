package com.springSecurityjwtexample.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springSecurityjwtexample.config.JwtService;
import com.springSecurityjwtexample.user.Role;
import com.springSecurityjwtexample.user.Users;
import com.springSecurityjwtexample.user.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepository repository;
    private final PasswordEncoder passwordEnconder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // create user, save to db, and return a token.
    public AuthenticationResponse register(RegisterRequest request) {
        var user = Users.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEnconder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        // If user doenst' exist, or information is wrong, an exception is thrown.
        // Otherwise it continues.
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwToken)
                .build();
    }

}
