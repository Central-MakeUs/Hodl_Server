package shop.hodl.kkonggi.src.record.exercise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.exercise.model.PostExerciseReq;
import shop.hodl.kkonggi.src.record.sun.model.GetSunRes;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import static shop.hodl.kkonggi.utils.Chat.makeSaveFailChat;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexTime;

@RestController
@RequestMapping("/app/v1/users/record/exercise")
public class ExerciseController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final ExerciseProvider exerciseProvider;
    @Autowired
    private final ExerciseService exerciseService;
    @Autowired
    private final JwtService jwtService;
    private static final int scenarioIdx = 7;

    public ExerciseController(ExerciseProvider exerciseProvider, ExerciseService exerciseService, JwtService jwtService){
        this.exerciseProvider = exerciseProvider;
        this.exerciseService = exerciseService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/input")
    public BaseResponse<GetChatRes> getExerciseInput(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "EXE_REC_INPUT";
            GetChatRes getChatRes = exerciseProvider.getExerciseInput(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetSunRes> getExercise(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            GetSunRes getSunRes = exerciseProvider.getExercise(userIdx, date);
            return new BaseResponse<>(getSunRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("")
    public BaseResponse<GetChatRes> createExerciseRecord(@RequestBody PostExerciseReq postReq){
        if(postReq.getIsExercise() != 1 && postReq.getIsExercise() != 0)
            return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_STATUS);

        if(!isRegexTime(postReq.getStartTime()) || !isRegexTime(postReq.getTotalTime()))
            return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);    // 시간 형식 확인
        try{
            int userIdx = jwtService.getUserIdx();
            GetChatRes getChatRes = exerciseService.createExerciseRecord(userIdx, postReq);
            if(getChatRes.getAction() != null && getChatRes.getAction().getActionType().equals("USER_INPUT_CHIP_GROUP")){
                getChatRes = makeSaveFailChat(getChatRes,"EXERCISE_CHIP_GROUP", "SAVE_FAIL_RETRY_EXERCISE", "SAVE_FAIL_DISCARD_EXERCISE");
                return new BaseResponse<>(getChatRes, BaseResponseStatus.CHAT_ERROR);
            }
            return new BaseResponse<>(getChatRes);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("")
    public BaseResponse<GetChatRes> updateExerciseRecord(@RequestBody PostExerciseReq postReq){
        if(postReq.getIsExercise() != 1 && postReq.getIsExercise() != 0)
            return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_STATUS);

        if(!isRegexTime(postReq.getStartTime()) || !isRegexTime(postReq.getTotalTime()))
            return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);    // 시간 형식 확인
        try{
            int userIdx = jwtService.getUserIdx();
            GetChatRes getChatRes = exerciseService.updateExerciseRecord(userIdx, postReq);
            if(getChatRes.getAction() != null && getChatRes.getAction().getActionType().equals("USER_INPUT_CHIP_GROUP")){
                getChatRes = makeSaveFailChat(getChatRes,"EXERCISE_CHIP_GROUP", "SAVE_FAIL_RETRY_EXERCISE", "SAVE_FAIL_DISCARD_EXERCISE");
                return new BaseResponse<>(getChatRes, BaseResponseStatus.CHAT_ERROR);
            }
            return new BaseResponse<>(getChatRes);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/good")
    public BaseResponse<GetChatRes> getExerciseGood(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "EXE_REC_GOOD";
            GetChatRes getChatRes = exerciseProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/hard")
    public BaseResponse<GetChatRes> getExerciseHard(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "EXE_REC_HARD";
            GetChatRes getChatRes = exerciseProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/time")
    public BaseResponse<GetChatRes> getExerciseTime(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "EXE_REC_TIME";
            GetChatRes getChatRes = exerciseProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/load")
    public BaseResponse<GetChatRes> getExerciseLoad(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "EXE_REC_LOAD";
            GetChatRes getChatRes = exerciseProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/bad")
    public BaseResponse<GetChatRes> getExerciseBad(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "EXE_REC_BAD";
            GetChatRes getChatRes = exerciseProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/feel")
    public BaseResponse<GetChatRes> getExerciseFeel(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "EXE_REC_FEEL_BAD";
            GetChatRes getChatRes = exerciseProvider.getChats(userIdx, scenarioIdx, groupId);
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
            String groupId = "EXE_REC_MOD_NO";
            GetChatRes getChatRes = exerciseProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
