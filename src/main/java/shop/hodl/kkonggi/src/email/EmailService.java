package shop.hodl.kkonggi.src.email;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import static shop.hodl.kkonggi.utils.Email.*;

@RequiredArgsConstructor
@Service
public class EmailService {

    @Autowired
    private final EmailDao emailDao;
    @Autowired
    private final EmailProvider emailProvider;
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    public void sendEmailMessage(String email) throws BaseException {

        // 회원가입 된 이메일인지 확인
        if(emailProvider.checkDuplicateEmail(email) == 1) throw new BaseException(BaseResponseStatus.POST_AUTH_EXISTS_EMAIL);

        String ePw = createKey();
        MimeMessage message = emailSender.createMimeMessage();

        try{
            message.addRecipients(MimeMessage.RecipientType.TO, email); // 보낼 이메일 설정
            message.setSubject("[꽁기] " + "인증번호를 안내해드립니다."); // 이메일 제목
            message.setText(setContext(ePw, templateEngine), "utf-8", "html"); // 내용 설정(Template Process)

            // 보낼 때 이름 설정하고 싶은 경우
            // message.setFrom(new InternetAddress([이메일 계정], [설정할 이름]));

            emailSender.send(message); // 이메일 전송

            // 이메일 이미 있으면,
            if(emailProvider.checkEmail(email) == 1) emailDao.updateAuthCode(email, ePw);
            else emailDao.createAuth(email, ePw);
        }
        catch (MessagingException e){
            logger.error("Fail email send = " + email);
            throw new BaseException(BaseResponseStatus.SEND_MAIL_ERROR);
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}
