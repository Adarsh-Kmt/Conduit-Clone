package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.Entity.Article;
import kamathadarsh.Conduit.Entity.Comment;
import kamathadarsh.Conduit.Entity.User;
import kamathadarsh.Conduit.Exception.ArticleNotFoundException;
import kamathadarsh.Conduit.Exception.CommentNotFoundException;
import kamathadarsh.Conduit.Repository.ArticleRepository;
import kamathadarsh.Conduit.Repository.CommentRepository;
import kamathadarsh.Conduit.Repository.UserRepository;
import kamathadarsh.Conduit.Request.CommentRequest;
import kamathadarsh.Conduit.Response.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommentService {

    final UserService userService;

    final ArticleRepository articleRepository;

    final CommentRepository commentRepository;

    final UserRepository userRepository;

    public CommentResponse commentToCommentResponse(String currUserUsername, Comment comment){

        User author = comment.getUser();
        ProfileResponse authorProfile = (ProfileResponse) userService.getProfile(author.getUsername(), currUserUsername);
        return CommentResponse.builder()
                .body(comment.getBody())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .authorProfile(authorProfile)
                .build();
    }

    public CustomResponse getAllCommentsUnderAnArticle(String currUserUsername, String articleSlug){

        try{
            Optional<Article> articleExists = articleRepository.findArticleBySlug(articleSlug);

            if(!articleExists.isPresent()) throw new ArticleNotFoundException("article with slug " + articleSlug + " does not exist.");

            List<Comment> allComments = commentRepository.findAllCommentsUnderAnArticle(articleSlug);

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

            Optional<Article> articleExists = articleRepository.findArticleBySlug(articleSlug);

            Optional<User> userExists = userRepository.findByUsername(currUserUsername);

            if(!articleExists.isPresent()) throw new ArticleNotFoundException("article with slug " + articleSlug + " does not exist.");
            Comment comment = Comment.builder()
                    .article(articleExists.get())
                    .user(userExists.get())
                    .body(commentRequest.getBody())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

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
            Optional<Comment> commentExists = commentRepository.getCommentUnderAnArticleById(articleSlug, commentId);

            if(!commentExists.isPresent())
                throw new CommentNotFoundException("comment with id " + commentId + "under article with slug: " + articleSlug + " was not found.");

            commentRepository.delete(commentExists.get());

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
