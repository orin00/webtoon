package com.webtoon.service;

import com.webtoon.model.Episode;
import com.webtoon.model.Episode.ReviewStatus;
import com.webtoon.repository.EpisodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpisodeService {

    private final EpisodeRepository episodeRepository;

    @Autowired
    public EpisodeService(EpisodeRepository episodeRepository) {
        this.episodeRepository = episodeRepository;
    }

    // artworkId(aId와 일치하는)로 에피소드 조회
    public List<Episode> getEpisodesByArtworkId(Integer artworkId) {
        return episodeRepository.findByAId(artworkId);
    }
    
    public List<Episode> getEpisodesByArtworkIdAndEpisodeCount(Integer artworkId, Integer episodeCount) {
        return episodeRepository.findByAIdAndEpisodeCount(artworkId, episodeCount);
    }

    public Episode createEpisode(Episode episode) {
    	Integer latestCount = episodeRepository.findTopByAIdOrderByEpisodeCountDesc(episode.getaId()).map(ep -> ep.getEpisodeCount()).orElse(0);
		episode.setEpisodeCount(latestCount + 1);
		episode.setCreatedAt(LocalDateTime.now());
		episode.setUpdatedAt(LocalDateTime.now());
		
		// 이미지 이름 정렬
		if (episode.getEpisodeUrl() != null) {
			// 이미지 이름을 ','로 나누고 정렬
			List<String> sortedImageNames = Arrays.stream(episode.getEpisodeUrl().split(","))
			.map(String::trim)
			.sorted((a, b) -> {
			// 숫자 기준으로 정렬
			Integer numA = Integer.parseInt(a.replaceAll("\\D", ""));
			Integer numB = Integer.parseInt(b.replaceAll("\\D", ""));
			return numA.compareTo(numB);
		})
		.collect(Collectors.toList());
		
		// 정렬된 이미지 이름을 다시 ','로 연결
		episode.setEpisodeUrl(String.join(", ", sortedImageNames));
		}
        return episodeRepository.save(episode);
    }
    
    // aId에 따른 에피소드 수 조회
    public Integer getEpisodeCountByArtworkId(Integer aId) {
        return episodeRepository.countByAId(aId);
    }
    
    // ReviewStatus가 'Review'인 에피소드 리스트를 가져오는 메서드
    public List<Episode> getEpisodesUnderReview() {
        return episodeRepository.findByReviewStatus(ReviewStatus.Review);
    }
    
    public void updateEpisodeReviewStatus(Integer episodeId, ReviewStatus reviewStatus) {
        Episode episode = episodeRepository.findById(episodeId)
                                           .orElseThrow(() -> new RuntimeException("Episode not found"));
        episode.setReviewStatus(reviewStatus);
        episodeRepository.save(episode);
    }

    public void deleteEpisode(Integer episodeId) {
        Episode episode = episodeRepository.findById(episodeId)
                                           .orElseThrow(() -> new RuntimeException("Episode not found"));
        episodeRepository.delete(episode);
    }
}
