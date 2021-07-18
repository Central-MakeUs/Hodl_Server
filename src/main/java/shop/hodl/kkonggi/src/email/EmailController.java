package shop.hodl.kkonggi.src.email;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.src.email.model.PostAuthReq;
import shop.hodl.kkonggi.src.email.model.PostEmailReq;

import static shop.hodl.kkonggi.config.BaseResponseStatus.*;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexEmail;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/v1/email")
public class EmailController {

    @Autowired
    private final EmailService emailService;
    @Autowired
    private final EmailProvider emailProvider;
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/code") // 이메일 인증 코드 보내기
    public BaseResponse<String> emailAuth(@RequestBody PostEmailReq postEmailReq) {
        if(postEmailReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현
        if(!isRegexEmail(postEmailReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            logger.info(logger.getName()+ postEmailReq.getEmail());
            emailService.sendEmailMessage(postEmailReq.getEmail());

            logger.info("[Email] send success : " + postEmailReq.getEmail());
            return new BaseResponse<>("");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @PostMapping("/verifyCode") // 이메일 인증 코드 검증
    public BaseResponse<String> verifyCode(@RequestBody PostAuthReq postAuthReq) {
        if(postAuthReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현
        if(!isRegexEmail(postAuthReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try{
            if(postAuthReq.getCode() == null){
                logger.warn("Resend email = " + postAuthReq.getEmail());
                emailService.sendEmailMessage(postAuthReq.getEmail());
                logger.warn("Complete resend email = " + postAuthReq.getEmail());
                return new BaseResponse<>(POST_AUTH_EMPTY_CODE);
            }
            emailProvider.checkAuth(postAuthReq);
            return new BaseResponse<>("");
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
