package kamathadarsh.Conduit.Request;


import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UpdateArticleRequest {

    private String title;
    private String body;
    private String description;
}
