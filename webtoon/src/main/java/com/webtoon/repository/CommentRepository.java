package com.webtoon.repository;

import com.webtoon.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByAIdAndEpisodeCount(Integer aId, Integer episodeCount);
    
    List<Comment> findByIsPublicTrue(); // 공개 댓글 조회
}