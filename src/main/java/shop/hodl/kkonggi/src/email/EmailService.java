package shop.hodl.kkonggi.src.email;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.context.Context;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.user.UserDao;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.Random;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class EmailService {

    @Autowired
    private final EmailDao emailDao;

    @Autowired
    private final EmailProvider emailProvider;

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @Transactional
    public void sendEmailMessage(String email) throws BaseException {

        // 회원가입 된 이메일인지 확인
        if(emailProvider.checkDuplicateEmail(email) == 1) throw new BaseException(BaseResponseStatus.POST_AUTH_EXISTS_EMAIL);

        String ePw = createKey();
        MimeMessage message = emailSender.createMimeMessage();

        try{
            message.addRecipients(MimeMessage.RecipientType.TO, email); // 보낼 이메일 설정
            message.setSubject("[꽁기] " + "인증번호를 안내해드립니다."); // 이메일 제목
            message.setText(setContext(ePw), "utf-8", "html"); // 내용 설정(Template Process)

            // 보낼 때 이름 설정하고 싶은 경우
            // message.setFrom(new InternetAddress([이메일 계정], [설정할 이름]));

            emailSender.send(message); // 이메일 전송

            // 이메일 이미 있으면,
            if(emailProvider.checkEmail(email) == 1) emailDao.updateAuthCode(email, ePw);
            emailDao.createAuth(email, ePw);
        }
        catch (MessagingException e){
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.SEND_MAIL_ERROR);
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    private String setContext(String code) { // 타임리프 설정하는 코드
        Context context = new Context();
        context.setVariable("code", code); // Template에 전달할 데이터 설정
        return templateEngine.process("mail", context); // mail.html
    }

    // 인증코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 4; i++) { // 인증코드 4자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

}
