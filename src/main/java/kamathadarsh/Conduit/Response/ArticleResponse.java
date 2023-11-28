package kamathadarsh.Conduit.Response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class ArticleResponse extends CustomResponse{

    private String slug;

    private String title;

    private String description;

    private String body;

    private Instant createdAt;

    private Instant updatedAt;

    private Integer favouriteCount;

    private ProfileResponse authorProfile;
}
