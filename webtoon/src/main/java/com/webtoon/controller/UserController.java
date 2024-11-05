package com.webtoon.controller;

import com.webtoon.model.User;
import com.webtoon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    // 회원 가입 기능 (POST http://localhost:8080/api/users/register)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
	// 회원 탈퇴 기능 (DELETE http://localhost:8080/api/users/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> withdrawUser(@PathVariable int id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok("User with ID " + id + " has been successfully withdrawn.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while processing your request: " + e.getMessage());
        }
    }

    @GetMapping("/email")
    public User getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);  // 이메일로 유저 정보 반환
    }
}
