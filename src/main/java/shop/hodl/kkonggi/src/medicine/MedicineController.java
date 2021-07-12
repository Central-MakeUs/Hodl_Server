package shop.hodl.kkonggi.src.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.medicine.model.GetMedAddTime;
import shop.hodl.kkonggi.src.medicine.model.GetStepperChatRes;
import shop.hodl.kkonggi.src.medicine.model.MedicineDTO;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

@RestController
@RequestMapping("/app/v1/users/medicine")
public class MedicineController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 약물 추가 시나리오 Idx
    private static int scenarioIdx = 2;

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
    @GetMapping("")
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
    public BaseResponse<GetStepperChatRes> getMedAddName(){
        try {
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_ADD_NAME";
            int stepNumber = 1;

            GetStepperChatRes getStepperChatRes = medicineProvider.getMedAdd(groupId, scenarioIdx, stepNumber);
            return new BaseResponse<>(getStepperChatRes);

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
    public BaseResponse<GetStepperChatRes> getMedAddCycle(@RequestParam("name") String name){
        if(name.isEmpty()){
            return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_EMPTY_NAME);
        }
        try {
            int userIdx = jwtService.getUserIdx();
            GetStepperChatRes getStepperChatRes = medicineProvider.getMedAddCycle(name);
            return new BaseResponse<>(getStepperChatRes);

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
    public BaseResponse<GetStepperChatRes> getMedAddStart(){
        try {
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_ADD_START";
            int stepNumber = 3;
            GetStepperChatRes getStepperChatRes = medicineProvider.getMedAdd(groupId, scenarioIdx, stepNumber);
            return new BaseResponse<>(getStepperChatRes);

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
    public BaseResponse<GetStepperChatRes> getMedAddEnd(){
        try {
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_ADD_END";
            int stepNumber = 4;
            GetStepperChatRes getStepperChatRes = medicineProvider.getMedAdd(groupId, scenarioIdx, stepNumber);
            return new BaseResponse<>(getStepperChatRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/time")
    public BaseResponse<GetMedAddTime> getMedAddTime(){
        try{
            int userIdx = jwtService.getUserIdx();
            GetMedAddTime getMedAddTime = medicineProvider.getMedAddTime();
            return new BaseResponse<>(getMedAddTime);
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

            if(medicineDTO.getDays().length != 7){
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
            }

            if(medicineDTO.getTimes().length != 5){
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_INVALID_TIME);
            }

            int userIdx = jwtService.getUserIdx();
             GetChatRes getChatRes = medicineService.createMedicine(userIdx, medicineDTO);
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
}
