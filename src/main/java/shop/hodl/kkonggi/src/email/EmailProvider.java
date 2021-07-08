package shop.hodl.kkonggi.src.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.email.model.PostAuthReq;

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

    public void checkAuth(PostAuthReq postAuthReq) throws BaseException {
        // 이메일 자체가 없음
        if(emailDao.checkAuthEmail(postAuthReq.getEmail()) == 0) throw new BaseException(BaseResponseStatus.POST_AUTH_EMPTY_EMAIL);

        // 인증 코드가 다름
        if(emailDao.checkAuthCode(postAuthReq) == 0) throw new BaseException(BaseResponseStatus.INVALID_AUTH_EMAIL_CODE);
    }

}
