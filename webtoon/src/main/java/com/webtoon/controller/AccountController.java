package com.webtoon.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.webtoon.model.User;
import com.webtoon.repository.UserRepository;
import com.webtoon.service.JwtService;
import com.webtoon.service.PasswordUtils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> params, HttpServletResponse res) {
        String email = params.get("email");
        String password = PasswordUtils.encryptPassword(params.get("password")); // 암호화된 비밀번호 사용

        User user = userRepository.findByEmailAndPassword(email, password);
        
        if (user != null) {
            int id = user.getId();
            String nickname = user.getNickname();
            String role = user.getRole();
            
            userRepository.updateLastLogin(id, LocalDateTime.now());
            
            String token = jwtService.getToken("id", id);
            
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            res.addCookie(cookie);

            Map<String, Object> response = new HashMap<>();
            response.put("id", id);
            response.put("nickname", nickname);
            response.put("role", role);
            response.put("email", email);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse res) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        res.addCookie(cookie);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> check(@CookieValue(value = "token", required = false) String token) {
        Claims claims = jwtService.getClaims(token);
        
        if (claims != null) {
            int id = Integer.parseInt(claims.get("id").toString());
            User user = userRepository.findById(id).orElse(null);  // User 객체로 수정
            
            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", id);
                response.put("nickname", user.getNickname());
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    
    @GetMapping("/admin/check")
    public ResponseEntity<Boolean> isAdmin(@CookieValue(value = "token", required = false) String token) {
        Claims claims = jwtService.getClaims(token);

        if (claims != null) {
            int id = Integer.parseInt(claims.get("id").toString());
            User user = userRepository.findById(id).orElse(null);  // User 객체로 수정

            if (user != null && "ADMIN".equals(user.getRole())) {
                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.ok(false);
    }
}