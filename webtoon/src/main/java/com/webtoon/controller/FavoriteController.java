package com.webtoon.controller;

import com.webtoon.model.Artwork;
import com.webtoon.model.Favorite;
import com.webtoon.service.ArtworkService;
import com.webtoon.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private ArtworkService artworkService;

    @PostMapping("/save")
    public ResponseEntity<Void> saveFavorite(@RequestParam String email, @RequestParam int artworkId) {
        favoriteService.saveFavorite(email, artworkId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/by-email")
    public ResponseEntity<List<Favorite>> getFavoritesByEmail(@RequestParam String email) {
        List<Favorite> favorites = favoriteService.getFavoritesByEmail(email);
        return ResponseEntity.ok(favorites);
    }
    
    @GetMapping("/artworks/by-artist-ids")
    public ResponseEntity<List<Artwork>> getArtworksByArtistIds(@RequestParam List<Integer> artistIds) {
        // ids 리스트를 사용하여 artwork 목록을 가져옵니다.
        List<Artwork> artworks = artworkService.getArtworksByArtistIds(artistIds);

        // artwork 목록을 응답으로 반환합니다.
        return ResponseEntity.ok(artworks);
    }

}
