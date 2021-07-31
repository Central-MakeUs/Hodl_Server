package shop.hodl.kkonggi.src.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.medicine.model.*;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;


@RestController
@RequestMapping("/app/v1/users/medicine")
public class MedicineController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 약물 추가 시나리오 Idx
    private static final int scenarioIdx = 2;

    @Autowired
    private final MedicineProvider medicineProvider;
    @Autowired
    private final MedicineService medicineService;
    @Autowired
    private final JwtService jwtService;

    public MedicineController(MedicineProvider medicineProvider, MedicineService medicineService, JwtService jwtService){
        this.medicineProvider = medicineProvider;
        this.medicineService = medicineService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/input")
    public BaseResponse<GetChatRes> getMedAddInput(){
        try {
            int userIdx = jwtService.getUserIdx();
            GetChatRes getChatRes = medicineProvider.getMedAddInput();
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * app/users/medicine/name
     * @return
     */
    @ResponseBody
    @GetMapping("/name")
    public BaseResponse<GetChatRes> getMedAddName(){
        try {
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_ADD_NAME";
            int stepNumber = 1;

            GetChatRes getMedChatRes = medicineProvider.getMedAdd(groupId, scenarioIdx, stepNumber);
            return new BaseResponse<>(getMedChatRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * app/v1/users/medicine/cycle?name
     * @return
     */
    @ResponseBody
    @GetMapping("/cycle")
    public BaseResponse<GetChatRes> getMedAddCycle(@RequestParam("name") String name){
        if(name.isEmpty()){
            return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_EMPTY_NAME);
        }
        try {
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_ADD_CYCLE";
            int scenarioIdx = 2;
            int stepNumber = 2;
            GetChatRes getMedChatRes = medicineProvider.getMedAddChats(userIdx,name, groupId, scenarioIdx, stepNumber);
            return new BaseResponse<>(getMedChatRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * app/users/medicine/start
     * @return
     */
    @ResponseBody
    @GetMapping("/start")
    public BaseResponse<GetChatRes> getMedAddStart(){
        try {
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_ADD_START";
            int stepNumber = 3;
            GetChatRes getMedChatRes = medicineProvider.getMedAddChats(userIdx, "", groupId, scenarioIdx, stepNumber);
            return new BaseResponse<>(getMedChatRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * app/users/medicine/end
     * @return
     */
    @ResponseBody
    @GetMapping("/end")
    public BaseResponse<GetChatRes> getMedAddEnd(){
        try {
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_ADD_END";
            int stepNumber = 4;
            GetChatRes getMedChatRes = medicineProvider.getMedAddChats(userIdx, "", groupId, scenarioIdx, stepNumber);
            return new BaseResponse<>(getMedChatRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/time")
    public BaseResponse<GetChatRes> getMedAddTime(){
        try{
            int userIdx = jwtService.getUserIdx();
            GetChatRes getMedChatRes = medicineProvider.getMedAddTime();
            return new BaseResponse<>(getMedChatRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("check")
    public BaseResponse<GetChatRes> getMedAddCheck(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_ADD_IS_OK";
            GetChatRes getChatRes = medicineProvider.getMedChats(userIdx, groupId, scenarioIdx);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @ResponseBody
    @PostMapping("")
    public BaseResponse<GetChatRes> createMedicine(@RequestBody MedicineDTO medicineDTO){
        try {
            if(medicineDTO.getName().isEmpty()) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_EMPTY_NAME);
            if(medicineDTO.getStart().isEmpty()) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_EMPTY_START);
            if(medicineDTO.getDays() == null) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_EMPTY_DAYS);
            if(medicineDTO.getTimes() == null) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_EMPTY_TIME);

            if(medicineDTO.getDays().length != 7)
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);

            if(medicineDTO.getTimes().length != 5)
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_INVALID_TIME);

            int userIdx = jwtService.getUserIdx();
            logger.info("userIdx = " + userIdx);
            GetChatRes getChatRes = medicineService.createMedicine(userIdx, medicineDTO);
            // 채팅 실패
            GetChatRes getSaveFailedChats = medicineProvider.getSaveFailedChats(userIdx);

            if(getChatRes.equals(getSaveFailedChats)) {
                String actionType = "MEDICINE_ADD_FAIL";
                String actionIdRe = "MED_ADD_FAIL_RETRY";
                String actionIdFail = "MED_ADD_FAIL_DISCARD";
                getChatRes.getAction().setActionType(actionType);
                for(int i= 0; i < getChatRes.getAction().getChoiceList().size(); i++){
                    if(getChatRes.getAction().getChoiceList().get(i).getContent().contains("취소")){
                        getChatRes.getAction().getChoiceList().get(i).setActionId(actionIdFail);
                    }
                    if(getChatRes.getAction().getChoiceList().get(i).getContent().contains("재전송")){
                        getChatRes.getAction().getChoiceList().get(i).setActionId(actionIdRe);
                    }
                }
                return new BaseResponse<>(getChatRes, BaseResponseStatus.CHAT_ERROR);
            }


            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @ResponseBody
    @GetMapping("modify")
    public BaseResponse<GetChatRes> getMedAddModify(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_ADD_MODIFY";
            GetChatRes getChatRes = medicineProvider.getMedChats(userIdx, groupId, scenarioIdx);
            return new BaseResponse<>(getChatRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("no")
    public BaseResponse<GetChatRes> getMedAddNo(){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "COM_OK";
            GetChatRes getChatRes = medicineProvider.getMedChats(userIdx, groupId, 0);
            return new BaseResponse<>(getChatRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 내 약통
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetMedicineRes> getMyMedicines(@RequestParam(required = false) String cycle,
                                                       @RequestParam(required = false) String time,
                                                       @RequestParam(required = false) Integer endDay){
        try {
            int userIdx = jwtService.getUserIdx();
            GetMedicineRes getMedicineRes = medicineProvider.getMyMedicines(userIdx, cycle, time, endDay);
            return new BaseResponse<>(getMedicineRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 특정 약 상세
     */
    @ResponseBody
    @GetMapping("/{medicineIdx}")
    public BaseResponse<GetMedicineDetailRes> getMedicineDetail(@PathVariable("medicineIdx") int medicineIdx){
        try {
            int userIdx = jwtService.getUserIdx();
            GetMedicineDetailRes getMedicineDetailRes = medicineProvider.getMedicineDetail(userIdx, medicineIdx);
            return new BaseResponse<>(getMedicineDetailRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 특정 약 변경
     */
    @ResponseBody
    @PutMapping("/{medicineIdx}")
    public BaseResponse<Integer> updateMedicineDetail(@PathVariable("medicineIdx") int medicineIdx, @RequestBody MedicineDTO medicineDTO){
        try {
            if(medicineDTO.getName().isEmpty()) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_EMPTY_NAME);
            if(medicineDTO.getStart().isEmpty()) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_EMPTY_START);
            if(medicineDTO.getDays() == null) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_EMPTY_DAYS);
            if(medicineDTO.getTimes() == null) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_EMPTY_TIME);

            if(medicineDTO.getDays().length != 7)
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
            if(medicineDTO.getTimes().length != 5)
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_INVALID_TIME);

            int userIdx = jwtService.getUserIdx();
            Integer result = medicineService.updateMedicineDetail(userIdx, medicineIdx, medicineDTO);
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 특정 e약 삭제
     *      * @param patchDleteReq
     * @return
     */
    @ResponseBody
    @PatchMapping("/{medicineIdx}")
    public BaseResponse<Integer> deleteMedicine(@RequestBody PatchDeleteReq patchDeleteReq, @PathVariable("medicineIdx") int medicineIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            if(patchDeleteReq.getMedicineIdx() < 0 || medicineIdx != patchDeleteReq.getMedicineIdx())
                throw new BaseException(BaseResponseStatus.PATCH_MEDICINE_EXISTS);
            Integer result = medicineService.deleteMedicine(userIdx, medicineIdx, patchDeleteReq);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
