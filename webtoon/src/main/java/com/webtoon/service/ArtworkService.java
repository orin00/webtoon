package com.webtoon.service;

import com.webtoon.model.Artwork;
import com.webtoon.repository.ArtworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;

    @Autowired
    public ArtworkService(ArtworkRepository artworkRepository) {
        this.artworkRepository = artworkRepository;
    }

    public List<Artwork> getAllArtworks() {
        return artworkRepository.findAll();
    }
    
    public Artwork getArtworkById(Integer artworkId) {
        return artworkRepository.findByArtworkId(artworkId);
    }
    
    public List<Artwork> getArtworksByIds(List<Integer> ids) {
        return artworkRepository.findByIdIn(ids);
    }
    
    // artistIds를 사용하여 artwork 목록을 가져오는 메서드
    public List<Artwork> getArtworksByArtistIds(List<Integer> artistIds) {
        return artworkRepository.findByArtworkIdIn(artistIds);
    }
    
    public List<Artwork> getArtworksByApprovedAndStatus() {
        List<Artwork> artworks = artworkRepository.findByReviewStatusAndStatusIn(
            Artwork.ReviewStatus.Approved, 
            List.of(Artwork.Status.Normal, Artwork.Status.Break, Artwork.Status.NormalReview, Artwork.Status.BreakReview, Artwork.Status.CompletedReview)
        );
        return artworks;
    }
    
    public List<Artwork> getArtworksByCompleted() {
    	List<Artwork> artworks = artworkRepository.findByReviewStatusAndStatusIn(
             Artwork.ReviewStatus.Approved, 
             List.of(Artwork.Status.Completed)
        );
        return artworks;
    }
    
    public List<Artwork> getArtworksByEmailAndNotCompletedStatus(String email) {
        return artworkRepository.findByEmailAndStatusNot(email, Artwork.Status.Completed);
    }
    
    public Artwork getArtworkByArtworkId(Integer artworkId) {
        return artworkRepository.findByArtworkId(artworkId);
    }
    
    // 가장 큰 artwork_id 조회 후 +1을 반환하는 메서드
    public Integer getNextArtworkId() {
        Integer maxArtworkId = artworkRepository.findMaxArtworkId();
        return (maxArtworkId != null) ? maxArtworkId + 1 : 1;
    }

    public Artwork saveArtwork(Artwork artwork) {
        return artworkRepository.save(artwork);
    }
    
    public List<Artwork> getApprovedArtworks() {
        return artworkRepository.findByReviewStatus(Artwork.ReviewStatus.Approved);
    }
    
    public List<Artwork> getReviewStatusArtworks() {
        return artworkRepository.findByReviewStatus(Artwork.ReviewStatus.Review);
    }
    
    public boolean approveArtwork(Integer artworkId) {
        Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) { // null 체크
            artwork.setReviewStatus(Artwork.ReviewStatus.Approved);
            artworkRepository.save(artwork);
            return true;
        }
        return false; // artwork가 없는 경우
    }
 
    // Artwork 데이터 삭제 메서드 추가
    public boolean deleteArtwork(Integer artworkId) {
        Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) {
            artworkRepository.delete(artwork);
            return true;
        }
        return false; // artwork가 없는 경우
    }
    
    public List<Artwork> getApprovedNormalArtworksByEmail(String email) {
        return artworkRepository.findByEmailAndReviewStatusAndStatus(email, Artwork.ReviewStatus.Approved, Artwork.Status.Normal);
    }
    
    public List<Artwork> getApprovedBreakArtworksByEmail(String email) {
        return artworkRepository.findByEmailAndReviewStatusAndStatus(email, Artwork.ReviewStatus.Approved, Artwork.Status.Break);
    }
    
    // 이메일로 필터링한 후, review_status가 Approved이고, status가 Normal 또는 Break인 작품을 조회
    public List<Artwork> getApprovedNormalOrBreakArtworksByEmail(String email) {
        List<Artwork.Status> statuses = List.of(Artwork.Status.Normal, Artwork.Status.Break);
        return artworkRepository.findByEmailAndReviewStatusAndStatusIn(email, Artwork.ReviewStatus.Approved, statuses);
    }
    
    // status 업데이트 메서드 추가
    public Artwork updateArtworkStatusBreakReview(Integer artworkId) {
        Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) {
            artwork.setStatus(Artwork.Status.BreakReview);
            return artworkRepository.save(artwork);
        }
        return null; // artwork가 없으면 null 반환
    }
    
    // status 업데이트 메서드 추가
    public Artwork updateArtworkStatusNormalReview(Integer artworkId) {
        Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) {
            artwork.setStatus(Artwork.Status.NormalReview);
            return artworkRepository.save(artwork);
        }
        return null; // artwork가 없으면 null 반환
    }
    
    // status 업데이트 메서드 추가
    public Artwork updateArtworkStatusCompletedReview(Integer artworkId) {
        Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) {
            artwork.setStatus(Artwork.Status.CompletedReview);
            return artworkRepository.save(artwork);
        }
        return null; // artwork가 없으면 null 반환
    }
    
    // BreakReview 상태인 작품을 가져오는 메서드 추가
    public List<Artwork> getBreakReviewArtworks() {
        return artworkRepository.findByStatus(Artwork.Status.BreakReview);
    }
    
    // Break 상태인 작품을 가져오는 메서드 추가
    public List<Artwork> getBreakArtworks() {
        return artworkRepository.findByStatus(Artwork.Status.Break);
    }
    
    // NormalReview 상태인 작품을 가져오는 메서드 추가
    public List<Artwork> getNormalReviewArtworks() {
        return artworkRepository.findByStatus(Artwork.Status.NormalReview);
    }
    
    // Normal or Break 상태인 작품을 가져오는 메서드 추가
    public List<Artwork> getNormalOrBreakArtworks() {
        // 조회할 상태 목록: Normal과 Break
        List<Artwork.Status> statuses = List.of(Artwork.Status.Normal, Artwork.Status.Break);
        return artworkRepository.findByStatusIn(statuses);
    }
    
    // CompletedReview 상태인 작품을 가져오는 메서드 추가
    public List<Artwork> getCompletedReviewArtworks() {
        return artworkRepository.findByStatus(Artwork.Status.CompletedReview);
    }
    
    // BreakReview -> Break 로 변경하는 메소드
    public Artwork updateStatusToBreak(Integer artworkId) {
    	Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) {
            artwork.setStatus(Artwork.Status.Break); // 상태를 Break로 설정
            return artworkRepository.save(artwork);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found");
        }
    }
    
    // BreakReview -> Normal 로 변경하는 메소드
    public Artwork updateStatusToBreakDenied(Integer artworkId) {
        Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) {
            artwork.setStatus(Artwork.Status.Normal); // 상태를 Normal로 설정
            return artworkRepository.save(artwork);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found");
        }
    }
    
    // NormalReview -> Normal로 변경하는 메소드
    public Artwork updateStatusToNormal(Integer artworkId) {
    	Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) {
            artwork.setStatus(Artwork.Status.Normal); // 상태를 Normal로 설정
            return artworkRepository.save(artwork);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found");
        }
    }
    
    // NormalReview -> Break로 변경하는 메소드
    public Artwork updateStatusToNormalDenied(Integer artworkId) {
        Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) {
            artwork.setStatus(Artwork.Status.Break); // 상태를 Normal로 설정
            return artworkRepository.save(artwork);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found");
        }
    }
    
    // CompletedReview -> Completed로 변경하는 메소드
    public Artwork updateStatusToCompleted(Integer artworkId) {
    	Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) {
            artwork.setStatus(Artwork.Status.Completed); // 상태를 Completed로 설정
            artwork.setSerialDay(Artwork.SerialDay.Finish); // serialDay를 Finish로 설정
            return artworkRepository.save(artwork);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found");
        }
    }
    
    // CompletedReview -> Normal로 변경하는 메소드
    public Artwork updateStatusToCompletedDenied(Integer artworkId) {
        Artwork artwork = artworkRepository.findByArtworkId(artworkId);
        if (artwork != null) {
            artwork.setStatus(Artwork.Status.Normal); // 상태를 Normal로 설정
            return artworkRepository.save(artwork);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found");
        }
    }
    
    public List<Artwork> getLatestNormalApprovedArtworks() {
        return artworkRepository.findTop3ByStatusAndReviewStatusOrderByCreatedAtDesc(
                Artwork.Status.Normal, Artwork.ReviewStatus.Approved);
    }
}
