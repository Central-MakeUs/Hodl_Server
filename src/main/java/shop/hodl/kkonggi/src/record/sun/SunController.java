package shop.hodl.kkonggi.src.record.sun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.sleep.model.PostSleepReq;
import shop.hodl.kkonggi.src.record.sun.model.GetSunRes;
import shop.hodl.kkonggi.src.record.sun.model.PostSunReq;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import static shop.hodl.kkonggi.utils.Chat.makeSaveFailChat;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexTime;


@RestController
@RequestMapping("/app/v1/users/record/sun")
public class SunController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final SunProvider sunProvider;
    @Autowired
    private final SunService sunService;
    @Autowired
    private final JwtService jwtService;
    private static final int scenarioIdx = 6;

    public SunController(SunProvider sunProvider, SunService sunService, JwtService jwtService){
        this.sunProvider = sunProvider;
        this.sunService = sunService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/input")
    public BaseResponse<GetChatRes> getSunInput(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SUN_REC_INPUT";
            GetChatRes getChatRes = sunProvider.getSunInput(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetSunRes> getSun(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            GetSunRes getSunRes = sunProvider.getSun(userIdx, date);
            return new BaseResponse<>(getSunRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("")
    public BaseResponse<GetChatRes> createSunRecord(@RequestBody PostSunReq postSleepReq){
        if(postSleepReq.getIsSun() != 1 && postSleepReq.getIsSun() != 0)
            return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_STATUS);

        if(!isRegexTime(postSleepReq.getStartTime()) || !isRegexTime(postSleepReq.getTotalTime()))
            return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);    // 시간 형식 확인
        try{
            int userIdx = jwtService.getUserIdx();
            GetChatRes getChatRes = sunService.createSunRecord(userIdx, postSleepReq);
            if(getChatRes.getAction() != null && getChatRes.getAction().getActionType().equals("USER_INPUT_CHIP_GROUP")){
                getChatRes = makeSaveFailChat(getChatRes,"SUN_CHIP_GROUP", "SAVE_FAIL_RETRY_SUN", "SAVE_FAIL_DISCARD_SUN");
                return new BaseResponse<>(getChatRes, BaseResponseStatus.CHAT_ERROR);
            }
            return new BaseResponse<>(getChatRes);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("")
    public BaseResponse<GetChatRes> updateSunRecord(@RequestBody PostSunReq postSleepReq){
        if(postSleepReq.getIsSun() != 1 && postSleepReq.getIsSun() != 0)
            return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_STATUS);

        if(!isRegexTime(postSleepReq.getStartTime()) || !isRegexTime(postSleepReq.getTotalTime()))
            return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);    // 시간 형식 확인
        try{
            int userIdx = jwtService.getUserIdx();
            GetChatRes getChatRes = sunService.updateSunRecord(userIdx, postSleepReq);
            if(getChatRes.getAction() != null && getChatRes.getAction().getActionType().equals("USER_INPUT_CHIP_GROUP")){
                getChatRes = makeSaveFailChat(getChatRes,"SUN_CHIP_GROUP", "SAVE_FAIL_RETRY_SUN", "SAVE_FAIL_DISCARD_SUN");
                return new BaseResponse<>(getChatRes, BaseResponseStatus.CHAT_ERROR);
            }
            return new BaseResponse<>(getChatRes);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/know/yes")
    public BaseResponse<GetChatRes> getSunKnowYes(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SUN_REC_MORE_KNOW";
            GetChatRes getChatRes = sunProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/know/no")
    public BaseResponse<GetChatRes> getSunKnowNo(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SUN_REC_MORE_N_KNOW";
            GetChatRes getChatRes = sunProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/how")
    public BaseResponse<GetChatRes> getSunHow(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SUN_REC_HOW";
            GetChatRes getChatRes = sunProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/mod/no")
    public BaseResponse<GetChatRes> getSunModNo(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SUN_MOD_NO";
            GetChatRes getChatRes = sunProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
