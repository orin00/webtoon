package com.webtoon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import com.webtoon.model.User;
import com.webtoon.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User registerUser(User user) {
        // 비밀번호 암호화
        String encryptedPassword = PasswordUtils.encryptPassword(user.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    public boolean isActive(int id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null && "USER".equals(user.getRole()) && User.AccountStatus.ACTIVE.equals(user.getAccountStatus());
    }
    
    // 로그인 메소드 예시
    @Transactional
    public boolean login(String email, String password) {
        // 입력된 비밀번호 암호화
        String encryptedPassword = PasswordUtils.encryptPassword(password);

        // 이메일과 암호화된 비밀번호로 사용자 검색
        User user = userRepository.findByEmailAndPassword(email, encryptedPassword);
        
        return user != null;
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);  // 이메일로 유저 정보 조회
    }

}
