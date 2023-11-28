package kamathadarsh.Conduit.Repository;

import kamathadarsh.Conduit.Entity.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Test
    @DisplayName("get all tags.")
    public void test1(){

        Tag newTag1 = Tag.builder()
                .tagName("test tag 1")
                .articles(new HashSet<>())
                .build();

        Tag newTag2 = Tag.builder()
                .tagName("test tag 2")
                .articles(new HashSet<>())
                .build();

        Tag newTag3 = Tag.builder()
                .tagName("test tag 3")
                .articles(new HashSet<>())
                .build();

        tagRepository.save(newTag1);
        tagRepository.save(newTag2);
        tagRepository.save(newTag3);

        List<Tag> tagList = tagRepository.getAllTags();

        for(int i = 1; i <= 3; i++){

            Assertions.assertEquals("test tag " + i, tagList.get(i-1).getTagName());
        }
    }

    @Test
    @DisplayName("get tag by tag name")
    public void test2(){

        Tag newTag4 = Tag.builder()
                .tagName("test tag 4")
                .build();

        tagRepository.save(newTag4);
        Tag tag = tagRepository.findTagByTagName("test tag 4").get();

        Assertions.assertEquals("test tag 4", tag.getTagName());
    }

}