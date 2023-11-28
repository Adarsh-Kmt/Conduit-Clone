package kamathadarsh.Conduit.Response;

import kamathadarsh.Conduit.Entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class TagResponse extends CustomResponse{

    private List<Tag> tags;
}
