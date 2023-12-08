package kamathadarsh.Conduit.Response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Data
public class ArticleResponse extends CustomResponse{

    private String slug;

    private String title;

    private String description;

    private String body;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer favouriteCount;

    private ProfileResponse authorProfile;
}
