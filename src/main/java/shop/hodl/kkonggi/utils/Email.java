package shop.hodl.kkonggi.utils;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Random;

public class Email {

    public static String setContext(String code, TemplateEngine templateEngine) { // 타임리프 설정하는 코드
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
