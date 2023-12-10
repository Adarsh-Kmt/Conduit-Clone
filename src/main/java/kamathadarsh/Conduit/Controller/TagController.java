package kamathadarsh.Conduit.Controller;


import kamathadarsh.Conduit.Response.TagResponse;
import kamathadarsh.Conduit.Service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@AllArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/api/tags")
    public ResponseEntity<TagResponse> getAllTags(){

        TagResponse tagResponse = tagService.getAllTags();

        return ResponseEntity.status(HttpStatus.OK).body(tagResponse);
    }
}
