package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.DTO.EmailDTO.EmailArticleDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
@AllArgsConstructor
public class EmailContentBuilderService {

    private final TemplateEngine templateEngine;

    private static final String link1 = "http://localhost:8080/api/user/";
    private static final String link2 = "/articles/";
    String buildDailyDigestEmailTemplate(String recipientUsername, List<EmailArticleDTO> dailyArticles){

        Context thymeLeafContext = new Context();

        thymeLeafContext.setVariable("dailyTitle", "Conduit Daily Digest");
        thymeLeafContext.setVariable("articles", dailyArticles);
        thymeLeafContext.setVariable("link", link1 + recipientUsername + link2);

        return templateEngine.process("DailyArticleDigestEmailTemplate", thymeLeafContext);


    }

    String buildCongratulatoryEmailTemplate(String recipientUsername, String articleTitle, Integer numberOfLikes){

        Context thymeLeafContext = new Context();

        String congratulatoryMessage = "congratulations "+ recipientUsername+", your article \"" + articleTitle + "\" has " + numberOfLikes + " likes.";
        thymeLeafContext.setVariable("title", "Your Article is Popular");
        thymeLeafContext.setVariable("body", congratulatoryMessage);

        return templateEngine.process("CongratulatoryEmailTemplate", thymeLeafContext);


    }
}
