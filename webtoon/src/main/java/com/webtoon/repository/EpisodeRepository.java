package com.webtoon.repository;

import com.webtoon.model.Episode;
import com.webtoon.model.Episode.ReviewStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Integer> {

    // 특정 artworkId(aId와 일치하는)와 일치하는 에피소드들을 찾는 메서드
    List<Episode> findByAId(Integer aId);
    
    List<Episode> findByAIdAndEpisodeCount(Integer aId, Integer episodeCount);
    
    // EpisodeCount로 에피소드를 찾는 메서드 추가    
    List<Episode> findByEpisodeCount(Integer episodeCount);
    
    Optional<Episode> findTopByAIdOrderByEpisodeCountDesc(Integer aId);
    
    // 특정 aId에 대한 에피소드 수 조회
    Integer countByAId(Integer aId);
    
    // ReviewStatus가 'Review'인 에피소드만 검색
    List<Episode> findByReviewStatus(ReviewStatus reviewStatus);
}
