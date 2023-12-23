package kamathadarsh.Conduit.CustomRecordMapper;

import kamathadarsh.Conduit.DTO.EmailDTO.EmailArticleDTO;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.ArticleRecord;
import org.jooq.RecordMapper;

public class EmailArticleDTOMapper implements RecordMapper<ArticleRecord, EmailArticleDTO> {


    @Override
    public @org.jetbrains.annotations.Nullable EmailArticleDTO map(ArticleRecord articleRecord) {

        return EmailArticleDTO.builder()
                .description(articleRecord.getDescription())
                .slug(articleRecord.getSlug())
                .title(articleRecord.getTitle())
                .build();
    }

    @Override
    public EmailArticleDTO apply(ArticleRecord record) {
        return RecordMapper.super.apply(record);
    }
}
