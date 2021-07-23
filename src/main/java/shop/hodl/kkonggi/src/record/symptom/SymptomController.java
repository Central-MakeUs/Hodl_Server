package shop.hodl.kkonggi.src.record.symptom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.src.record.symptom.model.GetSymptomRes;
import shop.hodl.kkonggi.src.record.symptom.model.PostSymptomReq;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;


@RestController
@RequestMapping("/app/v1/users/record/symptom")
public class SymptomController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final SymptomProvider symptomProvider;
    @Autowired
    private final SymptomService symptomService;
    @Autowired
    private final JwtService jwtService;
    private static final int scenarioIdx = 4;

    public SymptomController(SymptomProvider symptomProvider, SymptomService symptomService, JwtService jwtService){
        this.symptomProvider = symptomProvider;
        this.symptomService = symptomService;
        this.jwtService = jwtService;
    }

    /**
     * 증상 기록할래, 첫 시작
     * @return
     */
    @ResponseBody
    @GetMapping("/input")
    public BaseResponse<GetChatRes> getRecordSymptomInput(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SYM_REC_IS";
            GetChatRes getChatRes = symptomProvider.getRecordSymptom(userIdx, date, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [증상 기록할래]  -> [응 아파서 지금 좀 힘드네..]
     * @return
     */
    @ResponseBody
    @GetMapping("/sick")
    public BaseResponse<GetChatRes> getRecordSymptomSick(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SYM_REC_SICK";
            GetChatRes getChatRes = symptomProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [증상 기록할래]  -> [응 아파서 지금 좀 힘드네..] -> [괜찮아, 전화 드렸어]
     * @return
     */
    @ResponseBody
    @GetMapping("/sick/ok")
    public BaseResponse<GetChatRes> getRecordSymptomSickOk(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SYM_REC_SICK_OK";
            GetChatRes getChatRes = symptomProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * 증상 기록 전, 할래? 말래?
     * @return
     */
    @ResponseBody
    @GetMapping("/ask")
    public BaseResponse<GetChatRes> getRecordSymptomAsk(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SYM_REC_ASK";
            GetChatRes getChatRes = symptomProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [증상 기록할래]  -> [응 부작용이 있었어] -> [좋아, 알려줄게]
     *
     */
    @ResponseBody
    @GetMapping("/ask/check")
    public BaseResponse<GetSymptomRes> getSymptomList(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            GetSymptomRes getChatRes = symptomProvider.getSymptomList(userIdx, date);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 증상 기록
     * @param postReq
     * @return
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<GetChatRes> createSymptomRecord(@RequestBody PostSymptomReq postReq){
        try{
            int userIdx = jwtService.getUserIdx();
            GetChatRes getChatRes = symptomService.createSymptomRecord(userIdx, postReq);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 증상 기록의 [확인] 버튼 클릭
     * @return
     */
    @ResponseBody
    @GetMapping("/ask/ok")
    public BaseResponse<GetChatRes> getRecordSymptomAskOk(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SYM_REC_ASK_OK";
            GetChatRes getChatRes = symptomProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [증상 기록할래] 수정 -> [아니, 그냥 눌러봤어]
     * @return
     */
    @ResponseBody
    @GetMapping("/ask/no")
    public BaseResponse<GetChatRes> getRecordSymptomAskNo(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SYM_REC_ASK_NO";
            GetChatRes getChatRes = symptomProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [증상 기록할래]  -> [응 부작용이 있었어] -> [싫어]
     * @return
     */
    @ResponseBody
    @GetMapping("/mod/no")
    public BaseResponse<GetChatRes> getRecordSymptomModNo(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "SYM_REC_MOD_NO";
            GetChatRes getChatRes = symptomProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 증상 수정
     * @param postReq
     * @return
     */
    @ResponseBody
    @PatchMapping("")
    public BaseResponse<GetChatRes> updateSymptomRecord(@RequestBody PostSymptomReq postReq){
        try{
            int userIdx = jwtService.getUserIdx();
            GetChatRes getChatRes = symptomService.updateSymptomRecord(userIdx, postReq);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
