package shop.hodl.kkonggi.record.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.record.medicine.model.GetMedicineListRes;
import shop.hodl.kkonggi.src.medicine.model.GetMedChatRes;
import shop.hodl.kkonggi.utils.JwtService;

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
    public BaseResponse<GetMedChatRes> getRecordMedicineInput(){
        try{
            int userIdx = jwtService.getUserIdx();
            GetMedChatRes getMedChatRes = recordMedicineProvider.getRecordMedicineInput(userIdx, scenarioIdx);
            return new BaseResponse<>(getMedChatRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("")
    // 오늘 먹을 약이 있는 경우
    public BaseResponse<List<GetMedicineListRes>> getTodayMedicineList(){
        try{
            int userIdx = jwtService.getUserIdx();
            List<GetMedicineListRes> getMedicineListRes = recordMedicineProvider.getTodayMedicineList(userIdx);
            return new BaseResponse<>(getMedicineListRes);

        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    // 오늘 먹을 약이 없는 경우
}
