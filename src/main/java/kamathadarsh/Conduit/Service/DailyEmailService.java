package kamathadarsh.Conduit.Service;


import kamathadarsh.Conduit.DTO.EmailDTO.EmailArticleDTO;
import kamathadarsh.Conduit.DTO.EmailDTO.EmailUserDTO;
import kamathadarsh.Conduit.Exception.MailNotSentException;
import kamathadarsh.Conduit.Request.GetArticleRequest;
import kamathadarsh.Conduit.jooqRepository.JOOQArticleRepository;
import kamathadarsh.Conduit.jooqRepository.JOOQUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DailyEmailService {

    private final EmailContentBuilderService emailContentBuilderService;
    private final JavaMailSender mailSender;

    private final JOOQUserRepository jooqUserRepository;

    private final JOOQArticleRepository jooqArticleRepository;

    @Scheduled(initialDelay = 5000, fixedRate = 1000*60*60*24)
    @Async
    public void sendDailyDigestOfArticlesEmail(){

        List<EmailUserDTO> listOfEmailUserInfo = jooqUserRepository.getEmailUserInfo();
        List<EmailArticleDTO> dailyDigestOfArticles = jooqArticleRepository.emailFeed(GetArticleRequest.builder().limit(10).build());

        for(EmailUserDTO emailUserDTO : listOfEmailUserInfo){

            MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
                messageHelper.setFrom("conduit@email.com");
                messageHelper.setTo(emailUserDTO.getEmailId());
                messageHelper.setSubject("Today's Daily Dose of Articles!");
                messageHelper.setText(emailContentBuilderService
                        .buildDailyDigestEmailTemplate(emailUserDTO.getUsername(), dailyDigestOfArticles));
            };
            try{
                mailSender.send(messagePreparator);
                System.out.println("email sent to user " + emailUserDTO.getUsername());

            }
            catch(MailException e){

                throw new MailNotSentException("failed to send email.");
            }

        }

    }


}
