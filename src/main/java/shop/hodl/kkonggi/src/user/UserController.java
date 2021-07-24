package shop.hodl.kkonggi.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.user.model.*;
import shop.hodl.kkonggi.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static shop.hodl.kkonggi.config.BaseResponseStatus.*;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/v1/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원 조회 API
     * [GET] /users
     * 회원 번호 및 이메일 검색 조회 API
     * [GET] /users? Email=
     * @return BaseResponse<List<GetUserRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
        try{
            if(Email == null){
                List<GetUserRes> getUsersRes = userProvider.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            // Get Users
            List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(Email);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 1명 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUserInfo> getUser(@PathVariable("userIdx") int userIdx) {
        // Get Users
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdxByJwt != userIdx) throw new BaseException(BaseResponseStatus.INVALID_USER_JWT);

            GetUserInfo getUserRes = userProvider.getUser(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if(postUserReq.getPassword() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(postUserReq.getPassword().length() < 8 || postUserReq.getPassword().length() > 20){
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        if(postUserReq.getCheckedUserInfo() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_USERINFO);
        }
        try{
            logger.info(getClass().getSimpleName() + " " + postUserReq.getEmail());
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 닉네임 초기 설정 API
     * @param name
     * @return
     */
    @ResponseBody
    @GetMapping("/nickname")
    public BaseResponse<GetChatRes> getUserNickName(@RequestParam("name") String name){
        if(name.isEmpty()){
            return new BaseResponse<>(BaseResponseStatus.PATCH_USERS_EMPTY_NICKNAME);   // 닉네임을 입력해주세요.
        }
        try{
            int userIdx = jwtService.getUserIdx();
            logger.info("userIdx : " + userIdx, ", name : " + name);
            GetChatRes getChatRes = userProvider.getNickNameInput(name);
            return new BaseResponse<>(getChatRes);
        }
        catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 닉네임 정보 변경 API [응!맞아] 클릭시
     * [PATCH] app/v1/users/nickname/yes
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/nickname/yes")
    public BaseResponse<GetChatRes> modifyUserName(@RequestBody  PatchNickNameReq patchNickNameReq){
        if(patchNickNameReq.getNickname() == null){
            return new BaseResponse<>(PATCH_USERS_EMPTY_NICKNAME);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            //같다면 유저네임 변경
            PatchUserReq patchUserReq = new PatchUserReq(userIdxByJwt, patchNickNameReq.getNickname());
            String groupId = "NICKNAME_SUCCESS";
            return new BaseResponse<>(userService.modifyUserName(patchUserReq, groupId));

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 앗 잘못 말했어 클릭
     * [GET] app/v1/users/nickname/no
     * @return
     */
    @ResponseBody
    @GetMapping("/nickname/no")
    public BaseResponse<GetChatRes> getFailModifyNickName(){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인

            return new BaseResponse<>(userProvider.getFailModifyNickName());

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 닉네임 정보 변경 재시도 API
     * [PATCH] app/v1/users/nickname
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/nickname/error")
    public BaseResponse<GetChatRes> remodifyUserName(@RequestBody  PatchNickNameReq patchNickNameReq){
        if(patchNickNameReq.getNickname() == null){
            return new BaseResponse<>(PATCH_USERS_EMPTY_NICKNAME);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //같다면 유저네임 변경
            String groupId = "NICKNAME_RE_SUCCESS";
            PatchUserReq patchUserReq = new PatchUserReq(userIdxByJwt, patchNickNameReq.getNickname());
            return new BaseResponse<>(userService.modifyUserName(patchUserReq, groupId));

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 마이페이지에서 닉네임 수정
     * @param patchNickNameReq
     * @return
     */
    @ResponseBody
    @PatchMapping("/nickname")
    public BaseResponse<PatchNickNameRes> modifyUserNickName(@RequestBody  PatchNickNameReq patchNickNameReq){
        if(patchNickNameReq.getNickname() == null){
            return new BaseResponse<>(PATCH_USERS_EMPTY_NICKNAME);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            PatchUserReq patchUserReq = new PatchUserReq(userIdxByJwt, patchNickNameReq.getNickname());
            PatchNickNameRes patchNickNameRes = userService.modifyUserName(patchUserReq);
            return new BaseResponse<>(patchNickNameRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 마이페이지 화면
     */
    @ResponseBody
    @GetMapping("/my")
    public BaseResponse<GetMyRes> getMyPage(){
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            GetMyRes getMyRes = userProvider.getMyPage(userIdxByJwt);



            return new BaseResponse<>(getMyRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
