package kamathadarsh.Conduit.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentResponse {

    private Long id;
    private String body;
    private Instant createdAt;
    private Instant updatedAt;
    private ProfileResponse authorProfile;



}
