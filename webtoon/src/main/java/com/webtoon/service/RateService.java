package com.webtoon.service;

import com.webtoon.model.Rate;
import com.webtoon.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RateService {
    
    @Autowired
    private RateRepository rateRepository;
    
    public boolean checkIfRated(String email, int artworkId, int episodeCount) {
        return rateRepository.existsByEmailAndArtworkIdAndEpisodeCount(email, artworkId, episodeCount);
    }

    public void saveRating(Rate rate) {
        rateRepository.save(rate);
    }
    
    public List<Rate> findByArtworkIdAndEpisodeCount(int artworkId, int episodeCount) {
        return rateRepository.findByArtworkIdAndEpisodeCount(artworkId, episodeCount);
    }
    
    public Double getAverageRating(int artworkId, int episodeCount) {
        List<Rate> rates = rateRepository.findByArtworkIdAndEpisodeCount(artworkId, episodeCount);
        if (rates.isEmpty()) {
            return null;
        }

        double sum = 0;
        for (Rate rate : rates) {
            sum += rate.getStar();
        }
        return sum / rates.size();
    }

    public Integer getUserRating(String email, int artworkId, int episodeCount) {
        List<Rate> rates = rateRepository.findByArtworkIdAndEpisodeCountAndEmail(artworkId, episodeCount, email);
        if (!rates.isEmpty()) {
            return rates.get(0).getStar(); // 사용자 별점 반환
        }
        return null; // 별점이 없는 경우
    }
    
    public int getRatingCount(int artworkId, int episodeCount) {
        return rateRepository.countByArtworkIdAndEpisodeCount(artworkId, episodeCount);
    }
}
