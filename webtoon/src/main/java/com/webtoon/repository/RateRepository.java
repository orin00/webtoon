package com.webtoon.repository;

import com.webtoon.model.Rate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RateRepository extends JpaRepository<Rate, Integer> {
	
	// 특정 이메일, artworkId, episodeCount로 Rate가 존재하는지 확인하는 메서드
    boolean existsByEmailAndArtworkIdAndEpisodeCount(String email, int artworkId, int episodeCount);

    // artworkId와 episodeCount로 Rate 목록 조회
    List<Rate> findByArtworkIdAndEpisodeCount(int artworkId, int episodeCount);
    
    // 이메일, artworkId, episodeCount로 Rate를 조회하는 메서드 추가
    List<Rate> findByArtworkIdAndEpisodeCountAndEmail(int artworkId, int episodeCount, String email);
    
    // 특정 작품과 에피소드에 대해 별점을 남긴 인원 수
    int countByArtworkIdAndEpisodeCount(int artworkId, int episodeCount);
}
