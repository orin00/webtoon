package com.webtoon.controller;

import com.webtoon.model.Artwork;
import com.webtoon.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/artworks")
public class ArtworkController {

    private final ArtworkService artworkService;

    @Autowired
    public ArtworkController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @GetMapping("/")
    public List<Artwork> getAllArtworks() {
        return artworkService.getAllArtworks();
    }
    
    @GetMapping("/approved")
    public List<Artwork> getApprovedArtworks() {
        return artworkService.getApprovedArtworks();
    }
    
    // artworkId로 Artwork 정보를 가져오는 엔드포인트
    @GetMapping("/{artworkId}")
    public Artwork getArtworkById(@PathVariable Integer artworkId) {
        return artworkService.getArtworkById(artworkId);
    }
    
    @GetMapping("/status")
    public List<Artwork> getArtworksByApprovedAndStatus() {
        return artworkService.getArtworksByApprovedAndStatus();
    }
    
    @GetMapping("/completed")
    public List<Artwork> getArtworksByCompleted() {
        List<Artwork> artworks = artworkService.getArtworksByCompleted();
        return artworks;
    }
    
    @GetMapping("/notCompleted")
    public List<Artwork> getArtworksByEmail(@RequestParam String email) {
        return artworkService.getArtworksByEmailAndNotCompletedStatus(email);
    }
    
    // 다음 artwork_id를 반환하는 API 엔드포인트
    @GetMapping("/next-id")
    public Integer getNextArtworkId() {
        return artworkService.getNextArtworkId();
    }
    
    @PostMapping("/submit")
    public ResponseEntity<String> submitArtwork(@RequestBody Artwork artwork) {
        artworkService.saveArtwork(artwork);
        return ResponseEntity.ok("Artwork가 성공적으로 등록되었습니다.");
    }
    
    @GetMapping("/review-status")
    public List<Artwork> getReviewStatusArtworks() {
        return artworkService.getReviewStatusArtworks();
    }
    
    @PostMapping("/approve/{artworkId}")
    public ResponseEntity<String> approveArtwork(@PathVariable Integer artworkId) {
        boolean success = artworkService.approveArtwork(artworkId);
        if (success) {
            return ResponseEntity.ok("Artwork approved successfully.");
        } else {
            return ResponseEntity.badRequest().body("Artwork not found.");
        }
    }
    
    // Artwork 삭제 엔드포인트 추가
    @DeleteMapping("/{artworkId}")
    public ResponseEntity<String> deleteArtwork(@PathVariable Integer artworkId) {
        boolean deleted = artworkService.deleteArtwork(artworkId);
        if (deleted) {
            return ResponseEntity.ok("Artwork deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/approved-normal")
    public List<Artwork> getApprovedNormalArtworks(@RequestParam String email) {
        return artworkService.getApprovedNormalArtworksByEmail(email);
    }
    
    @GetMapping("/approved-break")
    public List<Artwork> getApprovedBreakArtworks(@RequestParam String email) {
        return artworkService.getApprovedBreakArtworksByEmail(email);
    }
    
    // 이메일로 승인된 작품 중 status가 Normal 또는 Break인 목록을 반환
    @GetMapping("/approved-normalBreak")
    public ResponseEntity<List<Artwork>> getApprovedNormalOrBreakArtworksByEmail(@RequestParam String email) {
        List<Artwork> artworks = artworkService.getApprovedNormalOrBreakArtworksByEmail(email);
        return ResponseEntity.ok(artworks);
    }
    
    // status 업데이트 API 추가
    @PutMapping("/{artworkId}/status-BreakReview")
    public Artwork updateArtworkStatusBreak(@PathVariable Integer artworkId) {
        return artworkService.updateArtworkStatusBreakReview(artworkId);
    }
    
    // status 업데이트 API 추가
    @PutMapping("/{artworkId}/status-NormalReview")
    public Artwork updateArtworkStatusNormal(@PathVariable Integer artworkId) {
        return artworkService.updateArtworkStatusNormalReview(artworkId);
    }
    
    // status 업데이트 API 추가
    @PutMapping("/{artworkId}/status-CompletedReview")
    public Artwork updateArtworkStatusCompleted(@PathVariable Integer artworkId) {
        return artworkService.updateArtworkStatusCompletedReview(artworkId);
    }
    
    // BreakReview 상태인 작품을 가져오는 API 추가
    @GetMapping("/BreakReview")
    public List<Artwork> getBreakReviewArtworks() {
        return artworkService.getBreakReviewArtworks();
    }
    
    // Break 상태인 작품을 가져오는 API 추가
    @GetMapping("/Break")
    public List<Artwork> getBreakArtworks() {
        return artworkService.getBreakArtworks();
    }
    
    // NormalReview 상태인 작품을 가져오는 API 추가
    @GetMapping("/NormalReview")
    public List<Artwork> getNormalReviewArtworks() {
        return artworkService.getNormalReviewArtworks();
    }
    
    // CompletedReview 상태인 작품을 가져오는 API 추가
    @GetMapping("/CompletedReview")
    public List<Artwork> getCompletedReviewArtworks() {
        return artworkService.getCompletedReviewArtworks();
    }

    @PutMapping("/{artworkId}/status-Break")
    public Artwork updateStatusToBreak(@PathVariable Integer artworkId) {
        return artworkService.updateStatusToBreak(artworkId);
    }
    
    @PutMapping("/{artworkId}/status-Break-Denied")
    public Artwork updateStatusToBreakDenied(@PathVariable Integer artworkId) {
        return artworkService.updateStatusToBreakDenied(artworkId); // JSON 형식으로 Artwork 객체를 반환
    }
    
    
    @PutMapping("/{artworkId}/status-Normal")
    public Artwork updateStatusToNormal(@PathVariable Integer artworkId) {
        return artworkService.updateStatusToNormal(artworkId);
    }
    
    @PutMapping("/{artworkId}/status-Normal-Denied")
    public Artwork updateStatusToNormalDenied(@PathVariable Integer artworkId) {
        return artworkService.updateStatusToNormalDenied(artworkId); // JSON 형식으로 Artwork 객체를 반환
    }
    
    @PutMapping("/{artworkId}/status-Completed")
    public Artwork updateStatusToCompleted(@PathVariable Integer artworkId) {
        return artworkService.updateStatusToCompleted(artworkId);
    }
    
    @PutMapping("/{artworkId}/status-Completed-Denied")
    public Artwork updateStatusToCompletedDenied(@PathVariable Integer artworkId) {
        return artworkService.updateStatusToCompletedDenied(artworkId); // JSON 형식으로 Artwork 객체를 반환
    }
    
    @GetMapping("/latest")
    public List<Artwork> getLatestNormalApprovedArtworks() {
        return artworkService.getLatestNormalApprovedArtworks();
    }
}
