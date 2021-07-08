package shop.hodl.kkonggi.src.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.email.model.GetEmailReq;
import shop.hodl.kkonggi.src.email.model.GetEmailRes;

@RequiredArgsConstructor
@Service
public class EmailProvider {
    @Autowired
    private final EmailDao emailDao;

    public int checkEmail(String email) throws BaseException {
        try{
            return emailDao.checkAuthEmail(email);
        } catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public void checkAuth(GetEmailReq getEmailReq) throws BaseException {
        try{
            // 이메일 자체가 없음
            if(emailDao.checkAuthEmail(getEmailReq.getEmail()) == 0) throw new BaseException(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);

            // 인증 코드가 다름
            if (emailDao.checkAuthCode(getEmailReq) == 0) throw new BaseException(BaseResponseStatus.INVALID_AUTH_EMAIL_CODE);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

}
