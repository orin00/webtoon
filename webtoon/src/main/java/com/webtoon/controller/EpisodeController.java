package com.webtoon.controller;

import com.webtoon.model.Episode;
import com.webtoon.model.Episode.ReviewStatus;
import com.webtoon.service.EpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/episodes")
public class EpisodeController {

    private final EpisodeService episodeService;

    @Autowired
    public EpisodeController(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }

    // 특정 artworkId에 해당하는 에피소드 리스트 반환
    @GetMapping("/artwork/{artworkId}")
    public List<Episode> getEpisodesByArtworkId(@PathVariable Integer artworkId) {
        return episodeService.getEpisodesByArtworkId(artworkId);
    }
    
    @GetMapping("/artwork/{artworkId}/count/{episodeCount}")
    public ResponseEntity<List<Episode>> getEpisodesByArtworkIdAndEpisodeCount(
            @PathVariable("artworkId") Integer artworkId,
            @PathVariable("episodeCount") Integer episodeCount) {
        List<Episode> episodes = episodeService.getEpisodesByArtworkIdAndEpisodeCount(artworkId, episodeCount);
        return ResponseEntity.ok(episodes);
    }
    
    // 에피소드 테이블에 데이터 삽입(review_status의 초기값 Review)
    @PostMapping("/submitEpisode")
    @ResponseStatus(HttpStatus.CREATED)
    public Episode createEpisode(@RequestBody Episode episode) {
        return episodeService.createEpisode(episode);
    }
    
    // 에피소드 수 조회
    @GetMapping("/count")
    public ResponseEntity<?> getEpisodeCount(@RequestParam Integer aId) {
        try {
            Integer count = episodeService.getEpisodeCountByArtworkId(aId);
            return ResponseEntity.ok("{\"count\": " + count + "}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류가 발생했습니다.\"}");
        }
    }

    // ReviewStatus가 'Review'인 에피소드 데이터를 반환하는 API
    @GetMapping("/review")
    public List<Episode> getEpisodesUnderReview() {
        return episodeService.getEpisodesUnderReview();
    }
    
    @PutMapping("/{id}/review-status")
    public ResponseEntity<Void> updateReviewStatus(@PathVariable Integer id, @RequestBody ReviewStatus reviewStatus) {
        episodeService.updateEpisodeReviewStatus(id, reviewStatus);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEpisode(@PathVariable Integer id) {
        episodeService.deleteEpisode(id);
        return ResponseEntity.noContent().build();
    }
}
