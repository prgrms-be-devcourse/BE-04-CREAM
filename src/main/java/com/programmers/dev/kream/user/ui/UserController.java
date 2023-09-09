package com.programmers.dev.kream.user.ui;


import com.programmers.dev.kream.common.jwt.JwtAuthentication;
import com.programmers.dev.kream.common.jwt.JwtAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    public record LoginRequest(String email, String password) { }

    public record LoginResponse(String email, String accessToken, String refreshToken) { }

    private final AuthenticationManager authenticationManager;

    public UserController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok("my userId is " + userId);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        JwtAuthenticationToken beforeAuthenticationToken = new JwtAuthenticationToken(loginRequest.email, loginRequest.password());
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authenticationManager.authenticate(beforeAuthenticationToken);

        return ResponseEntity.ok().body(tokenConvertToLoginResponse(authenticationToken));
    }

    private LoginResponse tokenConvertToLoginResponse(JwtAuthenticationToken authenticationToken) {
        JwtAuthentication principal = (JwtAuthentication) authenticationToken.getPrincipal();

        return new LoginResponse(principal.username(), principal.accessToken(), principal.refreshToken());
    }
}

