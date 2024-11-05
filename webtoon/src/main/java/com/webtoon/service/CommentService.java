package com.webtoon.service;

import com.webtoon.model.Comment;
import com.webtoon.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentsByEpisode(Integer aId, Integer episodeCount) {
        return commentRepository.findByAIdAndEpisodeCount(aId, episodeCount);
    }

    public Comment saveComment(Comment comment) {
        try {
            // 댓글 저장
            return commentRepository.save(comment);
        } catch (Exception e) {
            e.printStackTrace(); // 오류 로그
            throw new RuntimeException("Failed to save comment"); // 예외 던지기
        }
    }
    
    public void deleteComment(Integer commentId) {
        commentRepository.deleteById(commentId);
    }
    
    // 모든 공개 댓글 조회
    public List<Comment> getAllPublicComments() {
        return commentRepository.findByIsPublicTrue(); // 비공개가 아닌 댓글만 조회
    }
    
    // 댓글 비공개 처리
    public void hideComment(Integer id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setIsPublic(false);
        commentRepository.save(comment);
    }
}
