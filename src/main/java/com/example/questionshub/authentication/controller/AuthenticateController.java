package com.example.questionshub.authentication.controller;

import com.example.questionshub.authentication.models.AuthenticationRequest;
import com.example.questionshub.authentication.models.MessageResponse;
import com.example.questionshub.authentication.models.RefreshTokenEntity;
import com.example.questionshub.authentication.util.ApplicationUserDetailsUtil;
import com.example.questionshub.authentication.service.RefreshTokenService;
import com.example.questionshub.authentication.util.JwtUtil;
import com.example.questionshub.authentication.util.RefreshTokenUtil;
import com.example.questionshub.user.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
class AuthenticateController {
    private final ApplicationUserDetailsUtil userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenUtil refreshTokenUtil;

    @RequestMapping(value = "/authenticate")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest req) throws Exception {
        UserEntity user;

        try {
            user = userDetailsService.authenticate(req.getEmail(), req.getPassword());
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        ResponseCookie jwtCookie = refreshTokenUtil.getJwtCookie(jwt);
        UUID userId = user.getId();
        refreshTokenService.deleteRefreshTokenByUserIdOrElseThrow(userId);
        String refreshToken = refreshTokenService.generateRefreshToken(userId).getToken();
        ResponseCookie refreshTokenCookie = refreshTokenUtil.getRefreshTokenCookie(refreshToken);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new MessageResponse("Authenticated successfully"));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws BadRequestException {
        String refreshToken = refreshTokenUtil.getRefreshTokenFromCookies(request);
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.findByRefreshTokenOrElseThrow(refreshToken);

        refreshTokenUtil.verifyExpiration(refreshTokenEntity);

        UserEntity user = refreshTokenEntity.getUser();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        ResponseCookie jwtCookie = refreshTokenUtil.getJwtCookie(jwt);
        UUID userId = user.getId();
        refreshTokenService.deleteRefreshTokenByUserIdOrElseThrow(userId);
        String newRefreshToken = refreshTokenService.generateRefreshToken(userId).getToken();
        ResponseCookie refreshTokenCookie = refreshTokenUtil.getRefreshTokenCookie(newRefreshToken);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new MessageResponse("Token is refreshed successfully"));
    }
}
