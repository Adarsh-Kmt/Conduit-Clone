package kamathadarsh.Conduit.Request;

import jakarta.validation.constraints.NotBlank;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostArticleRequest {

    @NotBlank(message = "title is mandatory.")
    private String title;
    private String description;
    private String body;
    private List<String> tagList;
}
