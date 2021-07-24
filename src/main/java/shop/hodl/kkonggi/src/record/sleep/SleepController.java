package shop.hodl.kkonggi.src.record.sleep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.sleep.model.GetSleepRes;
import shop.hodl.kkonggi.src.record.sleep.model.PostSleepReq;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import static shop.hodl.kkonggi.utils.Chat.makeSaveFailChat;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexTime;

@RestController
@RequestMapping("/app/v1/users/record/sleep")
public class SleepController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final SleepProvider sleepProvider;
    @Autowired
    private final SleepService sleepService;
    @Autowired
    private final JwtService jwtService;
    private static final int scenarioIdx = 5;

    public SleepController(SleepProvider sleepProvider, SleepService sleepService, JwtService jwtService){
        this.sleepProvider = sleepProvider;
        this.sleepService = sleepService;
        this.jwtService = jwtService;
    }

    /**
     * 수면 기록할래 클릭
     * @return
     */
    @ResponseBody
    @GetMapping("/input")
    public BaseResponse<GetChatRes> getSleepInput(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_INPUT";
            GetChatRes getChatRes = sleepProvider.getSleepInput(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 수면 기록 페이지 get
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetSleepRes> getSleep(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            GetSleepRes getSleepRes = sleepProvider.getSleep(userIdx, date);
            return new BaseResponse<>(getSleepRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 수면 기록 등록
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<GetChatRes> createSleepRecord(@RequestBody PostSleepReq postSleepReq){
        try{
            if(postSleepReq.getIsSleep() != 1 && postSleepReq.getIsSleep() != 0)
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_STATUS);

            if(!isRegexTime(postSleepReq.getSleepTime()) || !isRegexTime(postSleepReq.getWakeUpTime()))
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);    // 시간 형식 확인

            int userIdx = jwtService.getUserIdx();
            GetChatRes getChatRes = sleepService.createSleepRecord(userIdx, postSleepReq);
            if(getChatRes.getAction() != null && getChatRes.getAction().getActionType().equals("USER_INPUT_CHIP_GROUP")){
                getChatRes = makeSaveFailChat(getChatRes,"SLEEP_CHIP_GROUP", "SAVE_FAIL_RETRY_SLEEP", "SAVE_FAIL_DISCARD_SLEEP");
                return new BaseResponse<>(getChatRes, BaseResponseStatus.CHAT_ERROR);
            }
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 수면 기록 수정
     */
    @ResponseBody
    @PatchMapping("")
    public BaseResponse<GetChatRes> updateSleepRecord(@RequestBody PostSleepReq postSleepReq){
        try{
            if(postSleepReq.getIsSleep() != 1 && postSleepReq.getIsSleep() != 0)
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_STATUS);

            if(!isRegexTime(postSleepReq.getSleepTime()) || !isRegexTime(postSleepReq.getWakeUpTime()))
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);    // 시간 형식 확인

            int userIdx = jwtService.getUserIdx();
            GetChatRes getChatRes = sleepService.updateSleepRecord(userIdx, postSleepReq);
            if(getChatRes.getAction() != null && getChatRes.getAction().getActionType().equals("USER_INPUT_CHIP_GROUP")){
                getChatRes = makeSaveFailChat(getChatRes,"SLEEP_CHIP_GROUP", "SAVE_FAIL_RETRY_SLEEP", "SAVE_FAIL_DISCARD_SLEEP");
                return new BaseResponse<>(getChatRes, BaseResponseStatus.CHAT_ERROR);
            }
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/less/just")
    public BaseResponse<GetChatRes> getSleepLessJust(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_LESS_JUST";
            GetChatRes getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/breath")
    public BaseResponse<GetChatRes> getSleepBreath(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_LESS_BREATH";
            GetChatRes getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/insomnia")
    public BaseResponse<GetChatRes> getSleepInsomnia(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_LESS_INSOMNIA";
            GetChatRes getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/cry")
    public BaseResponse<GetChatRes> getSleepCry(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_LESS_CRY";
            GetChatRes getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/sad")
    public BaseResponse<GetChatRes> getSleepSad(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_LESS_SAD";
            GetChatRes getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/what")
    public BaseResponse<GetChatRes> getSleepWhat(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_LESS_WHAT";
            GetChatRes getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/how")
    public BaseResponse<GetChatRes> getSleepHow(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_LESS_HOW";
            GetChatRes getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/work")
    public BaseResponse<GetChatRes> getSleepWork(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_WORK";
            GetChatRes getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/stress")
    public BaseResponse<GetChatRes> getSleepStress(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_STRESS";
            GetChatRes getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/anything")
    public BaseResponse<GetChatRes> getSleepAny(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_ANY";
            GetChatRes getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // NoAction
    @ResponseBody
    @GetMapping("/breath/no")
    public BaseResponse<GetChatRes> getSleepBreathNo(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_LESS_BREATH";
            GetChatRes getChatRes = sleepProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/next")
    public BaseResponse<GetChatRes> getSleepNext(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_LESS_NEXT";
            GetChatRes getChatRes = sleepProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/sad/no")
    public BaseResponse<GetChatRes> getSleepSadNo(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_LESS_SAD_NO";
            GetChatRes getChatRes = sleepProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/exercise")
    public BaseResponse<GetChatRes> getSleepExcersieNo(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_EXE_NO";
            GetChatRes getChatRes = sleepProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/exercise/ok")
    public BaseResponse<GetChatRes> getSleepExerciseOk(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_EXE_YES";
            GetChatRes getChatRes = sleepProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/stress/ok")
    public BaseResponse<GetChatRes> getSleepStressOk(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_STRESS_OK";
            GetChatRes getChatRes = sleepProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/anything/ok")
    public BaseResponse<GetChatRes> getSleepAnythingOk(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SLEEP_REC_ANY_OK";
            GetChatRes getChatRes = sleepProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
