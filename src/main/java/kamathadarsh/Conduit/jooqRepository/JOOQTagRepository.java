package kamathadarsh.Conduit.jooqRepository;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Article;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Tag;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kamathadarsh.Conduit.jooq.jooqGenerated.Tables.ARTICLE_TAG_TABLE;
import static kamathadarsh.Conduit.jooq.jooqGenerated.Tables.TAG;
import static kamathadarsh.Conduit.jooq.jooqGenerated.tables.Article.ARTICLE;

@AllArgsConstructor
@Repository
public class JOOQTagRepository{


    private final DSLContext dslContext;

    public List<Tag> getAllTags(){

        return dslContext.select()
                .from(TAG)
                .fetchInto(Tag.class);


    }

    public boolean findTagByTagName(String tagName){

        return dslContext.fetchExists(dslContext.select()
                .from(TAG)
                .where(TAG.TAG_NAME.eq(tagName)));


    }

    public void createTag(String tagName){

        dslContext.insertInto(TAG)
                .set(TAG.TAG_NAME, tagName)
                .execute();
    }

    public List<Article> getAllArticlesWithTag(String tagName){

        return dslContext.select()
                .from(ARTICLE)
                .join(ARTICLE_TAG_TABLE).on(ARTICLE_TAG_TABLE.ARTICLE_SLUG.eq(ARTICLE.SLUG))
                .join(TAG).on(ARTICLE_TAG_TABLE.TAG_NAME.eq(TAG.TAG_NAME))
                .where(TAG.TAG_NAME.eq(tagName))
                .fetchInto(Article.class);


    }


    public void addArticleToList(String tagName, String articleSlug){

        dslContext.insertInto(ARTICLE_TAG_TABLE)
                .set(ARTICLE_TAG_TABLE.ARTICLE_SLUG, articleSlug)
                .set(ARTICLE_TAG_TABLE.TAG_NAME, tagName)
                .execute();
    }

    public void deleteTag(String tagName){

        dslContext.deleteFrom(TAG)
                .where(TAG.TAG_NAME.eq(tagName))
                .execute();
    }



}
