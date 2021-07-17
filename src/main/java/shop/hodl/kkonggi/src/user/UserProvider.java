package shop.hodl.kkonggi.src.user;


import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.secret.Secret;
import shop.hodl.kkonggi.src.user.model.*;
import shop.hodl.kkonggi.utils.AES128;
import shop.hodl.kkonggi.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseResponseStatus;

import java.util.List;
import static shop.hodl.kkonggi.utils.ValidationRegex.getMaskedEmail;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public List<GetUserRes> getUsers() throws BaseException {
        try{
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        }
        catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<GetUserRes> getUsersByEmail(String email) throws BaseException{
        try{
            List<GetUserRes> getUsersRes = userDao.getUsersByEmail(email);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
                    }


    public GetUserInfo getUser(int userIdx) throws BaseException {
        try {
            GetUserInfo getUserRes = userDao.getUser(userIdx);
            // 이메일 마스킹
            getUserRes.setEmail(getMaskedEmail(getUserRes.getEmail()));
            logger.info("Email masking = " + getUserRes.getEmail());
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        // 이메일 여부
        if(checkEmail(postLoginReq.getEmail()) == 0) throw new BaseException(BaseResponseStatus.POST_USERS_NO_EMAIL);

        User user = userDao.getPwd(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(BaseResponseStatus.PASSWORD_DECRYPTION_ERROR);
        }

        if(postLoginReq.getPassword().equals(password)){
            int userIdx = userDao.getPwd(postLoginReq).getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt, getUserNickName(userIdx));
        }
        else{
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGIN);
        }

    }

    public String getUserNickName(int userIdx){
        return userDao.getUserNickName(userIdx);
    }

    public GetChatRes getFailModifyNickName() throws BaseException{
        try{
            String groupId = "NICKNAME_RE_ENTER";
            int scenarioIdx = 1;    // 닉네임 시나리오 idx = 1

            return userDao.getChats(groupId, scenarioIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getNickNameInput(String name) throws BaseException{
        try{
            String groupId = "NICKNAME_INPUT";  // 처음 닉네임 설정
            int scenarioIdx = 1;    // 닉네임 시나리오 idx = 1

            GetChatRes getChatRes = userDao.getChats(groupId, scenarioIdx);
            String toReplcae = "%User_Nickname%";
            for(int i = 0; i < getChatRes.getChat().size(); i++){
                if(getChatRes.getChat().get(i).getContent().contains(toReplcae)){
                    getChatRes.getChat().get(i).setContent(getChatRes.getChat().get(i).getContent().replace(toReplcae, name));
                }
            }
            return getChatRes;
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
