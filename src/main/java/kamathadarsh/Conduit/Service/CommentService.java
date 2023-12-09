package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Article;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Comment;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.UserTable;

import kamathadarsh.Conduit.Exception.ArticleNotFoundException;
import kamathadarsh.Conduit.Exception.CommentNotFoundException;


import kamathadarsh.Conduit.Request.CommentRequest;
import kamathadarsh.Conduit.Response.*;
import kamathadarsh.Conduit.jooqRepository.JOOQArticleRepository;
import kamathadarsh.Conduit.jooqRepository.JOOQCommentRepository;
import kamathadarsh.Conduit.jooqRepository.JOOQUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {

    final UserService userService;

    final JOOQArticleRepository jooqArticleRepository;

    final JOOQCommentRepository jooqCommentRepository;

    final JOOQUserRepository jooqUserRepository;

    public CommentResponse commentToCommentResponse(String currUserUsername, Comment comment){

        String authorUsername = comment.getUserUsername();
        ProfileResponse authorProfile = (ProfileResponse) userService.getProfile(authorUsername, currUserUsername);
        return CommentResponse.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .authorProfile(authorProfile)
                .build();
    }

    public CustomResponse getAllCommentsUnderAnArticle(String currUserUsername, String articleSlug){

        try{
            Optional<Article> articleExists = jooqArticleRepository.findArticleBySlug(articleSlug);

            if(!articleExists.isPresent()) throw new ArticleNotFoundException("article with slug " + articleSlug + " does not exist.");

            List<Comment> allComments = jooqCommentRepository.findAllCommentsUnderAnArticle(articleSlug);

            List<CommentResponse> allCommentResponses = new ArrayList<>();

            for(Comment comment : allComments){

                allCommentResponses.add(commentToCommentResponse(currUserUsername, comment));
            }

            return new MultipleCommentResponse(allCommentResponses);

        }
        catch(ArticleNotFoundException e){

            return FailureResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

    }


    public CustomResponse postComment(String currUserUsername, String articleSlug, CommentRequest commentRequest){

        try{

            Optional<Article> articleExists = jooqArticleRepository.findArticleBySlug(articleSlug);

            Optional<UserTable> userExists = jooqUserRepository.findByUsername(currUserUsername);

            if(!articleExists.isPresent()) throw new ArticleNotFoundException("article with slug " + articleSlug + " does not exist.");


            Comment comment = new Comment();
            comment.setBody(commentRequest.getBody());
            comment.setArticleSlug(articleSlug);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());
            comment.setUserUsername(currUserUsername);

            jooqCommentRepository.createComment(comment);

            CommentResponse commentResponse = commentToCommentResponse(currUserUsername, comment);

            return new SingleCommentResponse(commentResponse);

        }
        catch(ArticleNotFoundException e){

            return FailureResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

    }

    public CustomResponse deleteComment(String articleSlug, Long commentId){

        try{


            if(!jooqCommentRepository.checkIfCommentUnderAnArticleById(articleSlug, commentId))
                throw new CommentNotFoundException("comment with id " + commentId + "under article with slug: " + articleSlug + " was not found.");

            jooqCommentRepository.deleteCommentUnderAnArticleById(articleSlug, commentId);

            return SuccessResponse.builder()
                    .successMessage("comment has been deleted successfully.")
                    .build();
        }
        catch(CommentNotFoundException e){

            return FailureResponse.builder()
                    .message(e.getMessage())
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

    }
}
