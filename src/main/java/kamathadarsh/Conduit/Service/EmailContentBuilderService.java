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

        String congratulatoryMessage =
                "Dear " + recipientUsername+ ",\n" +
                "\n" +
                "We're thrilled to share some exciting news with you — your article, \""+articleTitle+"\", has reached a remarkable milestone! \uD83C\uDF1F\n" +
                "\n" +
                "\uD83D\uDC4F Milestone Achievement: "+ numberOfLikes + " Likes! \uD83D\uDC4D\n" +
                "\n" +
                "Your content has resonated with our community, and the positive response is a testament to the quality of your work. We're genuinely impressed by the impact your words have had on our readers.\n" +
                "\n" +
                "Your dedication to creating valuable and engaging content is truly commendable, and it's clear that your insights are making a difference. Thank you for contributing to the vibrant community we're building together.\n" +
                "\n" +
                "Keep up the fantastic work! We're looking forward to celebrating many more milestones with you.\n" +
                "\n" +
                "Cheers to your success!\n" +
                "\n" +
                "Best regards,\n" +
                "Conduit Team";
        thymeLeafContext.setVariable("title", "Your Article is Popular");
        thymeLeafContext.setVariable("body", congratulatoryMessage);

        return templateEngine.process("CongratulatoryEmailTemplate", thymeLeafContext);


    }
}
