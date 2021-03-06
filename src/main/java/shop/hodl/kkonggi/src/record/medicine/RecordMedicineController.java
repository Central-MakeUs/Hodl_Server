package shop.hodl.kkonggi.src.record.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.medicine.model.*;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexTime;

import java.util.List;

@RestController
@RequestMapping("/app/v1/users/record/medicine")
public class RecordMedicineController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final RecordMedicineProvider recordMedicineProvider;
    @Autowired
    private final RecordMedicineService recordMedicineService;
    @Autowired
    private final JwtService jwtService;

    // 약물 복용 시나리오 Idx
    private static final int scenarioIdx = 3;

    public RecordMedicineController(RecordMedicineProvider recordMedicineProvider, RecordMedicineService recordMedicineService, JwtService jwtService) {
        this.recordMedicineProvider = recordMedicineProvider;
        this.recordMedicineService = recordMedicineService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/scenario")
    public BaseResponse<GetChatRes> getRecordMedicineInput(){
        try{
            int userIdx = jwtService.getUserIdx();
            GetChatRes getMedChatRes = recordMedicineProvider.getRecordMedicineInput(userIdx, scenarioIdx);
            return new BaseResponse<>(getMedChatRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetMedicineListRes>> getTodayMedicineList(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            List<GetMedicineListRes> getMedicineListRes = recordMedicineProvider.getTodayMedicineList(userIdx, date);
            return new BaseResponse<>(getMedicineListRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 넘어 오는 약물값들은 전
    @ResponseBody
    @PostMapping("/all")
    public BaseResponse<PostAllMedicineRecordRes> createAllMedicineRecord(@RequestBody PostAllMedicineRecordReq postReq){
        try{
            if(postReq.getTimeSlot().isEmpty() || postReq.getTimeSlot() == null) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_EMPTY_SLOT);
            if(!(postReq.getTimeSlot().equals("D") || postReq.getTimeSlot().equals("M") || postReq.getTimeSlot().equals("L") || postReq.getTimeSlot().equals("E")
            || postReq.getTimeSlot().equals("N"))) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_SLOT);
            if(postReq.getMedicineIdx().length == 0 || postReq.getMedicineIdx() == null)
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_EMPTY_MEDICINES);
            if(postReq.getTime().isEmpty() || postReq.getTime() == null)
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_EMPTY_TIME);
            if(!isRegexTime(postReq.getTime()))
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);

            int userIdx = jwtService.getUserIdx();
            PostAllMedicineRecordRes postAllMedicineRecordRes = recordMedicineService.createAllMedicineRecord(userIdx, postReq);
            return new BaseResponse<>(postAllMedicineRecordRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/all")
    public BaseResponse<PostAllMedicineRecordRes> updateAllMedicineRecord(@RequestBody PostAllMedicineRecordReq postReq){
        try{
            if(postReq.getTimeSlot().isEmpty() || postReq.getTimeSlot() == null) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_EMPTY_SLOT);
            if(!(postReq.getTimeSlot().equals("D") || postReq.getTimeSlot().equals("M") || postReq.getTimeSlot().equals("L") || postReq.getTimeSlot().equals("E")
                    || postReq.getTimeSlot().equals("N"))) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_SLOT);
            if(postReq.getMedicineIdx().length == 0 || postReq.getMedicineIdx() == null)
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_EMPTY_MEDICINES);
            if(postReq.getTime().isEmpty() || postReq.getTime() == null)
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_EMPTY_TIME);
            if(!isRegexTime(postReq.getTime()))
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);

            int userIdx = jwtService.getUserIdx();
            PostAllMedicineRecordRes postAllMedicineRecordRes = recordMedicineService.updateAllMedicineRecord(userIdx, postReq);
            return new BaseResponse<>(postAllMedicineRecordRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @ResponseBody
    @GetMapping("/{medicineIdx}/{timeslot}")
    public BaseResponse<GetMedicineRecordRes> getSpecificMedicineRecord(@PathVariable("medicineIdx") int medicineIdx, @PathVariable("timeslot") String timeSlot,
                                                                        @RequestParam(required = false) String date){
        try{
            if(!(timeSlot.equals("D") || timeSlot.equals("M") || timeSlot.equals("L") || timeSlot.equals("E")
                    || timeSlot.equals("N"))) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_SLOT);
            int userIdx = jwtService.getUserIdx();
            GetMedicineRecordRes getMedicineRecordRes = recordMedicineProvider.getSpecificMedicineRecord(medicineIdx, timeSlot, date);
            return new BaseResponse<>(getMedicineRecordRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/{medicineIdx}/{timeslot}")
    public BaseResponse<PostMedicineRecordRes> createMedicineRecord(@RequestBody PostMedicineRecordReq postReq,
                                                     @PathVariable("medicineIdx") int medicineIdx, @PathVariable("timeslot") String timeSlot){
        try{
            if(!(timeSlot.equals("D") || timeSlot.equals("M") || timeSlot.equals("L") || timeSlot.equals("E")
                    || timeSlot.equals("N"))) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_SLOT);

            if(!isRegexTime(postReq.getTime())) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);

            if( !postReq.getStatus().equals("N") &&  !postReq.getStatus().equals("Y") )
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_STATUS);

            int userIdx = jwtService.getUserIdx();

            PostMedicineRecordRes postMedicineRecordRes = recordMedicineService.createMedicineRecord(userIdx ,postReq, medicineIdx, timeSlot);

            return new BaseResponse<>(postMedicineRecordRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    @ResponseBody
    @PatchMapping("/{medicineIdx}/{timeslot}")
    public BaseResponse<Integer> updateMedicineRecord(@RequestBody PatchMedicineRecordReq patchReq,
                                               @PathVariable("medicineIdx") int medicineIdx, @PathVariable("timeslot") String timeSlot){
        try{
            if(!(timeSlot.equals("D") || timeSlot.equals("M") || timeSlot.equals("L") || timeSlot.equals("E")
                    || timeSlot.equals("N"))) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_SLOT);

            if(!isRegexTime(patchReq.getTime())) return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);
            if( !patchReq.getStatus().equals("N") &&  !patchReq.getStatus().equals("Y") )
                return new BaseResponse<>(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_STATUS);

            int userIdx = jwtService.getUserIdx();
            recordMedicineService.updateMedicineRecord(userIdx ,patchReq, medicineIdx, timeSlot);

            return new BaseResponse<>(medicineIdx);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("list/ok")
    public BaseResponse<GetChatRes> getTodayMedicineStatus(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            GetChatRes getMedChatRes = recordMedicineProvider.getTodayMedicineStatus(userIdx, date, scenarioIdx);
            return new BaseResponse<>(getMedChatRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/scenario/notadd")
    public BaseResponse<GetChatRes> getNotAddChats(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_REC_NO";
            GetChatRes getMedChatRes = recordMedicineProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getMedChatRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/scenario/forget")
    public BaseResponse<GetChatRes> getForgetChats(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_REC_FORGET";
            GetChatRes getMedChatRes = recordMedicineProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getMedChatRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/scenario/just")
    public BaseResponse<GetChatRes> getJustChats(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_REC_DONT_WANT";
            GetChatRes getMedChatRes = recordMedicineProvider.getChats(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getMedChatRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/scenario/thank")
    public BaseResponse<GetChatRes> getThankChats(@RequestParam(required = false) String date){
        try{
            int userIdx = jwtService.getUserIdx();
            String groupId = "MED_REC_DONT_WANT_OK";
            GetChatRes getMedChatRes = recordMedicineProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            return new BaseResponse<>(getMedChatRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
