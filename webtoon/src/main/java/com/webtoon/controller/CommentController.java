package com.webtoon.controller;

import com.webtoon.model.Comment;
import com.webtoon.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000") // 클라이언트 URL
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 특정 에피소드의 댓글 조회
    @GetMapping("/artwork/{aId}/episode/{episodeCount}")
    public List<Comment> getComments(@PathVariable Integer aId, @PathVariable Integer episodeCount) {
        return commentService.getCommentsByEpisode(aId, episodeCount);
    }

    // 댓글 저장
    @PostMapping("/save")
    public Comment saveComment(@RequestBody Comment comment) {
    	try {
    		return commentService.saveComment(comment);
    	} catch (Exception e) {
            e.printStackTrace(); // 오류 로그
            throw new RuntimeException("Failed to save comment"); // 예외 던지기
        }
    }
    
    // 댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Integer commentId) {
        try {
            commentService.deleteComment(commentId);
            return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting comment", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 모든 공개 댓글 조회
    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        List<Comment> comments = commentService.getAllPublicComments();
        return ResponseEntity.ok(comments);
    }

    // 댓글 비공개 처리
    @PatchMapping("/{id}/hide")
    public ResponseEntity<Void> hideComment(@PathVariable Integer id) {
        commentService.hideComment(id);
        return ResponseEntity.noContent().build();
    }
}
