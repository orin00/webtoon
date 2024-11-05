package com.webtoon.controller;

import com.webtoon.model.Rate;
import com.webtoon.service.RateService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rates")
public class RateController {

    @Autowired
    private RateService rateService;

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkIfRated(
            @RequestParam String email,
            @RequestParam int artworkId,
            @RequestParam int episodeCount) {
        boolean isRated = rateService.checkIfRated(email, artworkId, episodeCount);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isRated", isRated);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveRating(
            @RequestParam int artworkId,
            @RequestParam String email,
            @RequestParam int star,
            @RequestParam int episodeCount) {
        Rate rate = new Rate();
        rate.setArtworkId(artworkId);
        rate.setEmail(email);
        rate.setStar(star);
        rate.setEpisodeCount(episodeCount);
        rate.setCreatedAt(LocalDateTime.now());
        rateService.saveRating(rate);
        return ResponseEntity.ok("별점이 등록되었습니다.");
    }
    
    @GetMapping("/average")
    public ResponseEntity<Map<String, Object>> getAverageRating(@RequestParam("artworkId") int artworkId, @RequestParam("episodeCount") int episodeCount) {
        Double averageRating = rateService.getAverageRating(artworkId, episodeCount);
        Map<String, Object> response = new HashMap<>();
        
        if (averageRating == null) {
            response.put("averageRating", "averageRating is null");
        } else {
            response.put("averageRating", averageRating);
            
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-rating")
    public ResponseEntity<?> getUserRating(
            @RequestParam("email") String email,
            @RequestParam("artworkId") int artworkId,
            @RequestParam("episodeCount") int episodeCount) {
        try {
            System.out.println("Received request with email: " + email + ", artworkId: " + artworkId + ", episodeCount: " + episodeCount);

            if (email == null || email.isEmpty() || artworkId <= 0 || episodeCount <= 0) {
                return ResponseEntity.badRequest().body("Invalid input parameters");
            }

            boolean isRated = rateService.checkIfRated(email, artworkId, episodeCount);
            if (isRated) {
                List<Rate> rates = rateService.findByArtworkIdAndEpisodeCount(artworkId, episodeCount);
                for (Rate rate : rates) {
                    if (rate.getEmail().equals(email)) {
                        return ResponseEntity.ok(rate.getStar());
                    }
                }
            }
            
            return ResponseEntity.noContent().build(); // 204 No Content를 반환
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류 발생: " + e.getMessage());
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Integer> getRatingCount(
            @RequestParam("artworkId") int artworkId,
            @RequestParam("episodeCount") int episodeCount) {

        int ratingCount = rateService.getRatingCount(artworkId, episodeCount);
        return ResponseEntity.ok(ratingCount);
    }
}
