package kamathadarsh.Conduit.Service;

import kamathadarsh.Conduit.DTO.EmailDTO.EmailUserDTO;
import kamathadarsh.Conduit.Exception.MailNotSentException;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailContentBuilderService emailContentBuilderService;

    public void sendCongratulatoryEmail(EmailUserDTO recipientEmailUserDTO, String articleTitle, Integer numberOfLikes){

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("conduit@email.com");
            messageHelper.setTo(recipientEmailUserDTO.getEmailId());
            messageHelper.setSubject("Congratulations! Your Article is Popular!");
            messageHelper.setText(emailContentBuilderService
                    .buildCongratulatoryEmailTemplate(recipientEmailUserDTO.getUsername(), articleTitle, numberOfLikes));
        };
        try{
            mailSender.send(messagePreparator);

        }
        catch(MailException e){

            throw new MailNotSentException("failed to send email.");
        }
    }


}
