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
    String buildEmailTemplate(String emailTitle, String recipientUsername, List<EmailArticleDTO> dailyArticles){

        Context thymeLeafContext = new Context();

        thymeLeafContext.setVariable("dailyTitle", emailTitle);
        thymeLeafContext.setVariable("articles", dailyArticles);
        thymeLeafContext.setVariable("link", link1 + recipientUsername + link2);

        String emailContent = templateEngine.process("DailyArticleDigestEmailTemplate", thymeLeafContext);

        return emailContent;
    }
}
