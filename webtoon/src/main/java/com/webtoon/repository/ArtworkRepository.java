package com.webtoon.repository;

import com.webtoon.model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Integer> {
	// artworkId로 Artwork을 조회하는 메서드 추가
    Artwork findByArtworkId(Integer artworkId);
    
    List<Artwork> findByIdIn(List<Integer> ids);
    
    List<Artwork> findByStatusIn(List<Artwork.Status> statuses);
    
    List<Artwork> findByEmailAndStatusNot(String email, Artwork.Status status);
    
    List<Artwork> findByStatus(Artwork.Status status);
    
    List<Artwork> findByReviewStatus(Artwork.ReviewStatus reviewStatus);
    
    List<Artwork> findByReviewStatusAndStatusIn(Artwork.ReviewStatus reviewStatus, List<Artwork.Status> statuses);
    
    List<Artwork> findByEmailAndReviewStatusAndStatus(String email, Artwork.ReviewStatus reviewStatus, Artwork.Status status);
    
    List<Artwork> findByEmailAndReviewStatusAndStatusIn(String email, Artwork.ReviewStatus reviewStatus, List<Artwork.Status> statuses);
    
    // artworkId가 주어진 리스트에 포함된 Artwork 목록을 가져오는 메서드
    List<Artwork> findByArtworkIdIn(List<Integer> artistIds);
    
    // 가장 큰 artwork_id를 조회하는 쿼리
    @Query("SELECT MAX(a.artworkId) FROM Artwork a")
    Integer findMaxArtworkId();
    
    List<Artwork> findTop3ByStatusAndReviewStatusOrderByCreatedAtDesc(Artwork.Status status, Artwork.ReviewStatus reviewStatus);
}
