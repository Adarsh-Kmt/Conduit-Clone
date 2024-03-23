package kamathadarsh.Conduit.Service;


import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Comment;

import kamathadarsh.Conduit.Exception.ArticleNotFoundException;
import kamathadarsh.Conduit.Exception.CommentNotFoundException;


import kamathadarsh.Conduit.Request.CommentRequest;
import kamathadarsh.Conduit.Response.*;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.CommentRecord;
import kamathadarsh.Conduit.jooqRepository.JOOQArticleRepository;
import kamathadarsh.Conduit.jooqRepository.JOOQCommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


@Service
@AllArgsConstructor
public class CommentService {

    final UserService userService;

    final JOOQArticleRepository jooqArticleRepository;

    final JOOQCommentRepository jooqCommentRepository;


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
            boolean articleExists = jooqArticleRepository.checkIfArticleExistsByArticleSlug(articleSlug);

            if(!articleExists) throw new ArticleNotFoundException("article with slug " + articleSlug + " does not exist.");

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

            boolean articleExists = jooqArticleRepository.checkIfArticleExistsByArticleSlug(articleSlug);

            if(!articleExists) throw new ArticleNotFoundException("article with slug " + articleSlug + " does not exist.");


            Comment comment = new Comment();
            comment.setBody(commentRequest.getBody());
            comment.setArticleSlug(articleSlug);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());
            comment.setUserUsername(currUserUsername);

            // create comment returns the auto-generated comment id of the comment from the comment table,
            // to be included in comment response object.
            Long commentId = jooqCommentRepository.createComment(comment);

            comment.setId(commentId);
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

    // delete all comments and nested comments.
    public void BreadthFirstSearch(String articleSlug,  Long parentCommentId){

        Queue<Long> q = new LinkedList<>();

        q.add(parentCommentId);

        while(q.isEmpty() == false){

            int currSize = q.size();

            for(int i = 0; i < currSize; i++){

                Long replyId = q.remove();
                List<Long> childReplyIdList = jooqCommentRepository.getIdOfRepliesToComment(articleSlug, replyId);
                deleteComment(articleSlug, replyId);

                q.addAll(childReplyIdList);
            }
        }
    }
    public CustomResponse deleteComment(String articleSlug, Long commentId){

        try{

            if(!jooqCommentRepository.checkIfCommentExistsByIdUnderAnArticle(commentId, articleSlug))
                throw new CommentNotFoundException("comment with id " + commentId +  " was not found.");

            BreadthFirstSearch(articleSlug, commentId);

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


    public CustomResponse replyToComment(String currUserUsername, String articleSlug, Long parentCommentId, CommentRequest commentRequest){

        try{


            boolean commentExists = jooqCommentRepository.checkIfCommentExistsByIdUnderAnArticle(parentCommentId, articleSlug);
            if(!commentExists) throw new CommentNotFoundException("comment with id " + parentCommentId + " was not found");


            jooqCommentRepository.replyToComment(currUserUsername, articleSlug, parentCommentId, commentRequest);

            return SuccessResponse.builder()
                    .successMessage("successfully replied to comment with id " + parentCommentId)
                    .build();

        }
        catch(CommentNotFoundException e){

            return FailureResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .build();
        }


    }


    public CustomResponse getRepliesToComment(String currUserUsername, String articleSlug, Long parentCommentId){

        try{

            boolean commentExists = jooqCommentRepository.checkIfCommentExistsByIdUnderAnArticle(parentCommentId, articleSlug);
            if(!commentExists) throw new CommentNotFoundException("comment with id " + parentCommentId + " was not found");


            List<Comment> replyList = jooqCommentRepository.getRepliesToComment(articleSlug, parentCommentId);

            List<CommentResponse> replyResponseList = new ArrayList<>();

            for(Comment reply : replyList){
                replyResponseList.add(commentToCommentResponse(currUserUsername, reply));
            }

            return new MultipleCommentResponse(replyResponseList);

        }
        catch(CommentNotFoundException e){

            return FailureResponse.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .build();
        }


    }
}
