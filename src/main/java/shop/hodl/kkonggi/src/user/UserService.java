package shop.hodl.kkonggi.src.user;



import org.jboss.jandex.Index;
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

    @Transactional
    public GetChatRes modifyUserName(PatchUserReq patchUserReq, String groupId) throws BaseException {
        if(patchUserReq.getUserNickName().equals(userProvider.getUserNickName(patchUserReq.getUserIdx())))
            throw new BaseException(BaseResponseStatus.PATCH_USERS_ALREADY_NICKNAME);

        try{
            int scenarioIdx = 1;    // 닉네임 시나리오 idx = 1
            int result = userDao.modifyUserName(patchUserReq);
            if(result == 0){    // 닉네임 저장 실패
                String falseGroupId = "NICKNAME_FALSE";
                GetChatRes getChatFalse = userDao.getChats(falseGroupId, scenarioIdx);
                return getChatFalse;
            }
            else{   // 닉네임 저장 성공

                GetChatRes getChatRes = null;
                String modifiedNickName = userProvider.getUserNickName(patchUserReq.getUserIdx());

                if(groupId.equals("NICKNAME_RE_SUCCESS")){    // 재시도 할 때,
                    getChatRes = userDao.getChatsNoAction(groupId, scenarioIdx);
                    GetChatRes getSuccessRes = userDao.getChats("NICKNAME_SUCCESS", scenarioIdx);

                    getChatRes.getChat().addAll(getSuccessRes.getChat());    // Chat 다 복붙
                    getChatRes.setAction(getSuccessRes.getAction());   // Action 설정
                }
                else if(groupId.equals("NICKNAME_SUCCESS")){
                    getChatRes = userDao.getChats(groupId, scenarioIdx);
                }

                String toReplace = "%User_Nickname%";

                for(int i = 0; i < getChatRes.getChat().size(); i++){
                    if(getChatRes.getChat().get(i).getContent().contains(toReplace))
                        getChatRes.getChat().get(i).setContent(getChatRes.getChat().get(i).getContent().replace(toReplace, modifiedNickName));
                    }
                return getChatRes;
            }

        } catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public PatchNickNameRes modifyUserName(PatchUserReq patchUserReq) throws BaseException{
        int result = 0;
        try{
            result = userDao.modifyUserName(patchUserReq);
        }catch(Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
        if(result == 0) throw new BaseException(BaseResponseStatus.MODIFY_FAIL_USERNAME);
        PatchNickNameRes patchNickNameRes = new PatchNickNameRes(patchUserReq.getUserIdx(), patchUserReq.getUserNickName());
        return patchNickNameRes;
    }

}
