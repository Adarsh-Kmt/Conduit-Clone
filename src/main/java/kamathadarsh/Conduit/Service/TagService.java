package kamathadarsh.Conduit.Service;


import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Tag;
import kamathadarsh.Conduit.Response.TagResponse;
import kamathadarsh.Conduit.jooqRepository.JOOQTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class TagService {

    private final JOOQTagRepository jooqTagRepository;

    public TagResponse getAllTags(){

        List<Tag> tagList = jooqTagRepository.getAllTags();

        return new TagResponse(tagList);
    }

    public boolean findTagByTagName(String tagName){

        return jooqTagRepository.findTagByTagName(tagName);
    }

    public void createTag(String tagName){

        jooqTagRepository.createTag(tagName);

    }

    public void addArticleToList(String tagName, String newArticleSlug){

        jooqTagRepository.addArticleToList(tagName, newArticleSlug);

    }

    public void deleteTag(String tagName){

        jooqTagRepository.deleteTag(tagName);
    }
}
