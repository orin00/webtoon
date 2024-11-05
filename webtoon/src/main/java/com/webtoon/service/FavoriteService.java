package com.webtoon.service;

import com.webtoon.model.Favorite;
import com.webtoon.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public void saveFavorite(String email, int artworkId) {
        Favorite favorite = new Favorite();
        favorite.setEmail(email);
        favorite.setaId(artworkId);
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);
    }
    
    public List<Favorite> getFavoritesByEmail(String email) {
        return favoriteRepository.findByEmail(email);
    }
}
