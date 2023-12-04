package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.Entity.Article;
import kamathadarsh.Conduit.Entity.Tag;
import kamathadarsh.Conduit.Repository.TagRepository;
import kamathadarsh.Conduit.Response.TagResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class TagService {

    private TagRepository tagRepository;

    public TagResponse getAllTags(){

        List<Tag> tagList = tagRepository.getAllTags();

        return new TagResponse(tagList);
    }

    public Optional<Tag> findTagByTagName(String tagName){

        return tagRepository.findTagByTagName(tagName);
    }

    public Tag createTag(String tagName){

        Tag newTag = new Tag(tagName, new HashSet<>());

        tagRepository.save(newTag);
        return newTag;

    }

    public Tag addArticleToList(Tag tag, Article newArticle){

        Set<Article> articlesWithTagName = tag.getArticles();

        articlesWithTagName.add(newArticle);

        tagRepository.save(tag);

        return tag;

    }

    public void deleteTag(Tag tag){

        tagRepository.delete(tag);
    }
}
