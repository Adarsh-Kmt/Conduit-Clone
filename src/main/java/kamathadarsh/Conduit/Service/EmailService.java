package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.Exception.MailNotSentException;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.CongratulatoryEmailView;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailContentBuilderService emailContentBuilderService;

    @Async
    public void sendCongratulatoryEmail(CongratulatoryEmailView congratulatoryEmailInfo){

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("conduit@email.com");
            messageHelper.setTo(congratulatoryEmailInfo.getEmailId());
            messageHelper.setSubject("Congratulations! Your Article is Popular!");

            messageHelper.setText(emailContentBuilderService.buildCongratulatoryEmailTemplate
                    (congratulatoryEmailInfo.getUsername(),
                            congratulatoryEmailInfo.getTitle(),
                            congratulatoryEmailInfo.getNextMilestone()));
        };
        try{
            mailSender.send(messagePreparator);

        }
        catch(MailException e){

            throw new MailNotSentException("failed to send email.");
        }
    }


}
