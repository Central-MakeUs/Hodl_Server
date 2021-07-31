package shop.hodl.kkonggi.src.email;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.email.model.PostAuthReq;
import shop.hodl.kkonggi.src.user.UserDao;

import static shop.hodl.kkonggi.config.Constant.LogDateFormat;

@RequiredArgsConstructor
@Service
public class EmailProvider {
    @Autowired
    private final EmailDao emailDao;
    @Autowired
    private final UserDao userDao;
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public int checkEmail(String email) throws BaseException {
        try{
            return emailDao.checkAuthEmail(email);
        } catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void checkAuth(PostAuthReq postAuthReq) throws BaseException {
        // 이메일 자체가 없음
        if(emailDao.checkAuthEmail(postAuthReq.getEmail()) == 0) {
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + "No Authentic email = " + postAuthReq.getEmail());
            throw new BaseException(BaseResponseStatus.POST_AUTH_EMPTY_EMAIL);
        }
        // 인증 코드가 다름
        if(emailDao.checkAuthCode(postAuthReq) == 0) {
            logger.warn(LogDateFormat.format(System.currentTimeMillis()) + "Invalid Authentic email = " + postAuthReq.getEmail());
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_EMAIL_CODE);
        }
    }

    public int checkDuplicateEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
