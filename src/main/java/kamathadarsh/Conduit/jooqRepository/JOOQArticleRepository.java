package kamathadarsh.Conduit.jooqRepository;

import kamathadarsh.Conduit.CustomRecordMapper.EmailArticleDTOMapper;
import kamathadarsh.Conduit.CustomRecordMapper.EmailUserDTOMapper;
import kamathadarsh.Conduit.DTO.EmailDTO.EmailArticleDTO;
import kamathadarsh.Conduit.DTO.EmailDTO.EmailUserDTO;
import kamathadarsh.Conduit.Request.GetArticleRequest;
import kamathadarsh.Conduit.Request.UpdateArticleRequest;
import kamathadarsh.Conduit.Service.EmailService;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Article;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.CongratulatoryEmailView;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Tag;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.ArticleRecord;
import lombok.AllArgsConstructor;


import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kamathadarsh.Conduit.jooq.jooqGenerated.Tables.*;



@AllArgsConstructor
@Repository
public class JOOQArticleRepository {

    private final DSLContext dslContext;

    private final EmailService emailService;

    private final Integer milestoneInterval = 5;

    public String slugify(String articleTitle){

        String slug = articleTitle.trim().toLowerCase().replace(" ", "-");
        return slug + "-" + UUID.randomUUID();
    }

    @Scheduled(fixedRate = 1000*60)
    @Async
    public void periodicMilestoneCheck(){

        /*
        add a column to article table, let it be nextMilestone.
        periodically, this method checks which articles have exceeded their nextMilestone,
        and sends emails to all the authors of these articles.
         */


        List<CongratulatoryEmailView> congratsEmailList = dslContext.select()
                .from(CONGRATULATORY_EMAIL_VIEW)
                .fetchInto(CongratulatoryEmailView.class);


        for(CongratulatoryEmailView congratsEmail : congratsEmailList){
            System.out.println("sending congratulatory message to user " + congratsEmail.getUsername());
            emailService.sendCongratulatoryEmail(congratsEmail);
        }

        if(!congratsEmailList.isEmpty()) periodicMilestoneUpdate();

    }

    /*
    this method is called if articles cross their current milestone,
    so that their nextMilestone field can be updated to the next milestone.
     */
    public void periodicMilestoneUpdate(){

        dslContext.update(ARTICLE)
                .set(ARTICLE.NEXT_MILESTONE, (ARTICLE.FAVOURITE_COUNT.minus((ARTICLE.FAVOURITE_COUNT.mod(DSL.inline(milestoneInterval))))).plus(DSL.inline(milestoneInterval)))
                .where(ARTICLE.FAVOURITE_COUNT.ge(ARTICLE.NEXT_MILESTONE))
                .execute();
        System.out.println("periodic milestone update done.");
    }

    public boolean checkIfArticleExistsByArticleSlug(String articleSlug){

        return dslContext.fetchExists(dslContext.selectFrom(ARTICLE).where(ARTICLE.SLUG.eq(articleSlug)));
    }
    public Optional<Article> findArticleBySlug(String articleSlug){

        return Optional.ofNullable(dslContext.select()
                .from(ARTICLE)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .fetchOneInto(Article.class));
    }

    public List<Tag> getAllTagsAssociatedWithArticle(String articleSlug){

        return dslContext.select()
                .from(TAG)
                .join(ARTICLE_TAG_TABLE).on(ARTICLE_TAG_TABLE.ARTICLE_SLUG.eq(ARTICLE.SLUG))
                .where(ARTICLE_TAG_TABLE.ARTICLE_SLUG.eq(articleSlug))
                .fetchInto(Tag.class);

    }

    public void createArticle(Article article){

        dslContext.insertInto(ARTICLE)
                .set(ARTICLE.SLUG, article.getSlug())
                .set(ARTICLE.DESCRIPTION, article.getDescription())
                .set(ARTICLE.TITLE, article.getTitle())
                .set(ARTICLE.BODY, article.getBody())
                .set(ARTICLE.CREATED_AT, article.getCreatedAt())
                .set(ARTICLE.UPDATED_AT, article.getUpdatedAt())
                .set(ARTICLE.AUTHOR_USERNAME, article.getAuthorUsername())
                .set(ARTICLE.FAVOURITE_COUNT, article.getFavouriteCount())
                .set(ARTICLE.NEXT_MILESTONE, article.getNextMilestone())
                .execute();
    }

    public void favouriteArticle(String articleSlug, String currUserUsername){

        dslContext.insertInto(USER_FAVOURITE_ARTICLE_TABLE)
                .set(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG, articleSlug)
                .set(USER_FAVOURITE_ARTICLE_TABLE.USERNAME, currUserUsername).execute();

        Integer favouriteCount = dslContext.select(ARTICLE.FAVOURITE_COUNT)
                .from(ARTICLE)
                .where(ARTICLE.SLUG.eq(articleSlug)).fetchOneInto(Integer.class);

        favouriteCount++;

        dslContext.update(ARTICLE)
                .set(ARTICLE.FAVOURITE_COUNT, favouriteCount)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .execute();

//        if(favouriteCount == 5 || favouriteCount % 50 == 0){
//
//            String authorUsername = dslContext.select(ARTICLE.AUTHOR_USERNAME)
//                    .from(ARTICLE)
//                    .where(ARTICLE.SLUG.eq(articleSlug))
//                    .fetchOneInto(String.class);
//
//            UserTableRecord userRecord = dslContext.select(USER_TABLE.EMAIL_ID, USER_TABLE.USERNAME)
//                    .from(USER_TABLE)
//                    .where(USER_TABLE.USERNAME.eq(authorUsername))
//                    .fetchOneInto(UserTableRecord.class);
//
//            EmailUserDTOMapper mapper = new EmailUserDTOMapper();
//            EmailUserDTO emailAuthorUserDTO = mapper.map(userRecord);
//
//            String articleTitle = dslContext.select(ARTICLE.TITLE)
//                    .from(ARTICLE)
//                    .where(ARTICLE.SLUG.eq(articleSlug))
//                    .fetchOneInto(String.class);
//
//            emailService.sendCongratulatoryEmail(emailAuthorUserDTO, articleTitle, favouriteCount);
//        }


    }

    public void unfavouriteArticle(String articleSlug, String currUserUsername){

        dslContext.deleteFrom(USER_FAVOURITE_ARTICLE_TABLE)
                .where(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.eq(articleSlug))
                .and(USER_FAVOURITE_ARTICLE_TABLE.USERNAME.eq(currUserUsername)).execute();

        Integer favouriteCount = dslContext.select(ARTICLE.FAVOURITE_COUNT)
                .from(ARTICLE)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .fetchOneInto(Integer.class);

        favouriteCount--;

        dslContext.update(ARTICLE)
                .set(ARTICLE.FAVOURITE_COUNT, favouriteCount)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .execute();
    }

    public boolean articleIsFavouritedByUser(String currUserUsername, String articleSlug){

        return dslContext.fetchExists(
                dslContext.select().from(USER_FAVOURITE_ARTICLE_TABLE)
                        .where(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.eq(articleSlug),
                                USER_FAVOURITE_ARTICLE_TABLE.USERNAME.eq(currUserUsername))
        );

        
    }
    public List<Article> globalFeed(String currUserUsername, GetArticleRequest getArticleRequest){

//        SelectQuery query = dslContext.selectFrom(ARTICLE).getQuery();
//
//        if(getArticleRequest.getIsFavourited() != null && getArticleRequest.getIsFavourited()){
//
//            query.addJoin(USER_FAVOURITE_ARTICLE_TABLE, USER_FAVOURITE_ARTICLE_TABLE.USERNAME.eq(ARTICLE.SLUG));
//            query.addConditions(USER_FAVOURITE_ARTICLE_TABLE.USERNAME.eq(currUserUsername));
//        }
//
//        return query.fetchInto(Article.class);

//        return dslContext.select().from(ARTICLE)
//                .where(
//                        (getArticleRequest.getIsFavourited() != null && getArticleRequest.getIsFavourited())?
//                                USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.in(
//                                        dslContext.select(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG)
//                                                .from(USER_FAVOURITE_ARTICLE_TABLE)
//                                                .where(USER_FAVOURITE_ARTICLE_TABLE.USERNAME.eq(currUserUsername))
//                                ):DSL.trueCondition()
//                ).fetchInto(Article.class);

        boolean isFavourited = (getArticleRequest.getIsFavourited() != null && getArticleRequest.getIsFavourited());
        boolean tagFilter = (getArticleRequest.getTags() != null && !getArticleRequest.getTags().isEmpty());
        boolean authorFilter = (getArticleRequest.getAuthorUsername() != null && !getArticleRequest.getAuthorUsername().isBlank());

        String authorUsername = getArticleRequest.getAuthorUsername();
        List<String> listOfTags = getArticleRequest.getTags();
        return dslContext.select(ARTICLE.SLUG,
                        ARTICLE.BODY,
                        ARTICLE.AUTHOR_USERNAME,
                        ARTICLE.UPDATED_AT,
                ARTICLE.CREATED_AT,
                ARTICLE.DESCRIPTION,
                ARTICLE.FAVOURITE_COUNT,
                ARTICLE.TITLE
                ).distinctOn(ARTICLE.SLUG).from(ARTICLE)
                .join(USER_FAVOURITE_ARTICLE_TABLE)
                .on(isFavourited ? USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.eq(ARTICLE.SLUG):DSL.noCondition())
                .join(ARTICLE_TAG_TABLE)
                .on(tagFilter ? ARTICLE_TAG_TABLE.ARTICLE_SLUG.eq(ARTICLE.SLUG):DSL.noCondition())
                .where(tagFilter ? ARTICLE_TAG_TABLE.TAG_NAME.in(listOfTags):DSL.trueCondition())
                .and(isFavourited?USER_FAVOURITE_ARTICLE_TABLE.USERNAME.eq(currUserUsername):DSL.trueCondition())
                .and(authorFilter?ARTICLE.AUTHOR_USERNAME.eq(authorUsername):DSL.trueCondition())
                .orderBy(ARTICLE.UPDATED_AT.desc())
                .limit(getArticleRequest.getLimit())
                .fetchInto(Article.class);





    }


    public void updateArticle(String articleSlug, UpdateArticleRequest updateArticleRequest){


        ArticleRecord updatedRecord = new ArticleRecord();

        if(updateArticleRequest.getBody() != null && !updateArticleRequest.getBody().isBlank()){

            updatedRecord.set(ARTICLE.BODY, updateArticleRequest.getBody());
        }

        if(updateArticleRequest.getDescription() != null && !updateArticleRequest.getDescription().isBlank()){

            updatedRecord.set(ARTICLE.DESCRIPTION, updateArticleRequest.getDescription());
        }

        // change the updated-at time.
        updatedRecord.set(ARTICLE.UPDATED_AT, LocalDateTime.now());

        dslContext.update(ARTICLE)
                .set(updatedRecord)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .execute();




    }

    public String getAuthorUsernameOfArticle(String articleSlug){

        return dslContext.select(ARTICLE.AUTHOR_USERNAME)
                .from(ARTICLE)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .fetchOneInto(String.class);
    }

    public void deleteArticle(String articleSlug){

        // delete article record form the article table.
        dslContext.deleteFrom(ARTICLE)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .execute();

        // deleting the article also means deleting all the comments associated with it.
        dslContext.deleteFrom(COMMENT)
                .where(COMMENT.ARTICLE_SLUG.eq(articleSlug))
                .execute();

        // must delete the article slug from user favourite article table.
        dslContext.deleteFrom(USER_FAVOURITE_ARTICLE_TABLE)
                .where(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.eq(articleSlug))
                .execute();

        // must delete corresponding records from tag article table.
        dslContext.deleteFrom(ARTICLE_TAG_TABLE)
                .where(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.eq(articleSlug))
                .execute();



    }

    public List<EmailArticleDTO> emailFeed(GetArticleRequest getArticleRequest){

        List<ArticleRecord> records =  dslContext.select(
                        ARTICLE.TITLE,
                        ARTICLE.DESCRIPTION,
                        ARTICLE.SLUG)
                .from(ARTICLE)
                .orderBy(ARTICLE.UPDATED_AT.desc())
                .limit(getArticleRequest.getLimit())
                .fetchInto(ArticleRecord.class);

        EmailArticleDTOMapper mapper = new EmailArticleDTOMapper();
        return records.stream().map(record -> mapper.map(record)).toList();


    }


}
