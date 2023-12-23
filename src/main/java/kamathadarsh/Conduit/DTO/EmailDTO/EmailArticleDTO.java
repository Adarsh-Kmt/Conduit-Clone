package kamathadarsh.Conduit.DTO.EmailDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailArticleDTO {

    private String title;
    private String description;
    private String slug;
}
