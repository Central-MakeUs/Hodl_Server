package shop.hodl.kkonggi.src.user;



import org.jboss.jandex.Index;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.secret.Secret;
import shop.hodl.kkonggi.src.user.model.PatchNickNameRes;
import shop.hodl.kkonggi.src.user.model.PatchUserReq;
import shop.hodl.kkonggi.src.user.model.PostUserReq;
import shop.hodl.kkonggi.utils.AES128;
import shop.hodl.kkonggi.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.user.model.PostUserRes;

import javax.transaction.Transactional;
import java.util.List;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    @Transactional
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(userProvider.checkEmail(postUserReq.getEmail()) ==1){
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
        }
        String userInfoToString = "N";
        if(postUserReq.getCheckedUserInfo()) userInfoToString = "Y";

        String pwd;
        try{
            //암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq, userInfoToString);
            logger.info(getClass().getSimpleName() + userIdx);
            return new PostUserRes(userIdx);
        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<String> modifyUserName(PatchUserReq patchUserReq, int groupIdx) throws BaseException {
        if(patchUserReq.getUserNickName().equals(userProvider.getUserNickName(patchUserReq.getUserIdx())))
            throw new BaseException(BaseResponseStatus.PATCH_USERS_ALREADY_NICKNAME);

        try{
            int result = userDao.modifyUserName(patchUserReq);
            if(result == 0){    // 닉네임 저장 실패
                return userDao.getFalseModifyUserNickName();
            }
            else{   // 닉네임 저장 성공
                List<String> getSuccess = userDao.getSuccessModifyUserNickName(groupIdx);
                String modifiedNickName = userProvider.getUserNickName(patchUserReq.getUserIdx());

                if(groupIdx == 4){
                    getSuccess.addAll(userDao.getSuccessModifyUserNickName(1));
                }

                String toReplace = "%User_Nickname%";

                for(int i = 0; i < getSuccess.size(); i++){
                    if(getSuccess.get(i).contains(toReplace))
                        getSuccess.set(i,getSuccess.get(i).replace(toReplace, modifiedNickName));
                    }
                return getSuccess;
            }

        } catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}
