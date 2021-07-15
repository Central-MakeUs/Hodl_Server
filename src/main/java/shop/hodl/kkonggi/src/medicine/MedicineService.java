package shop.hodl.kkonggi.src.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.medicine.model.GetMedChatRes;
import shop.hodl.kkonggi.src.medicine.model.MedicineDTO;
import shop.hodl.kkonggi.src.medicine.model.PostMedicineReq;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
public class MedicineService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MedicineProvider medicineProvider;
    private final MedicineDao medicineDao;
    private final JwtService jwtService;

    @Autowired
    public MedicineService(MedicineProvider medicineProvider, MedicineDao medicineDao, JwtService jwtService){
        this.medicineProvider = medicineProvider;
        this.medicineDao = medicineDao;
        this.jwtService = jwtService;
    }


    @Transactional
    public GetMedChatRes createMedicine(int userIdx, MedicineDTO medicineDTO) throws BaseException {

        String cycle = "";
        int days = intArrayToInt(medicineDTO.getDays());
        ArrayList<String> timeSlot = toTimeSlot(medicineDTO.getTimes());

        if(days == 0) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        if(timeSlot.isEmpty()) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_TIME);

        if(medicineProvider.checkMedicine(userIdx, medicineDTO.getName()) == 1)
            throw new BaseException(BaseResponseStatus.POST_MEDICINE_EXISTS);

        try{
            // 2일에 한 번씩
            if(medicineDTO.getDays()[0] == 2) {
                cycle = "2";
                days = 0;
            }
            else cycle = "S";
            PostMedicineReq postMedicineReq = new PostMedicineReq(userIdx, medicineDTO.getName(), cycle, days, medicineDTO.getStart(), medicineDTO.getEnd());

            int medicineIdx = medicineDao.createMedicine(postMedicineReq);
            int medicineTime = 0;
            for(String time : timeSlot){
                medicineTime = medicineDao.createMedicineTime(medicineIdx, time);
                if(medicineTime == 0) break;
            }

            int scenarioIdx;
            String groupId;

            if(medicineIdx > 0 && medicineTime > 0){
                scenarioIdx = 2;
                groupId = "MED_ADD_SUCCESS";
            } else{
                scenarioIdx = 0;
                groupId = "SAVE_FAIL";
            }
            return medicineProvider.getMedChats(postMedicineReq.getUserIdx(), groupId, scenarioIdx);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int intArrayToInt(int[] arr){
        int sum = 0;
        for(int i = 0; i < arr.length; i++){
            if(arr[i] == 1){
                sum += Math.pow(2, i);
            }
        }
        return sum;
    }

    public ArrayList<String> toTimeSlot(int[] arr){
        ArrayList<String> timeSlot = new ArrayList<>();
        for(int i = 0; i < arr.length; i++){
            if(arr[i] == 1){
                if(i == 0) timeSlot.add("D");   // Dawn
                if(i == 1) timeSlot.add("M");   // Morning
                if(i == 2) timeSlot.add("L");   // Launch
                if(i == 3) timeSlot.add("E");   // Evening
                if(i == 4) timeSlot.add("N");   // Night
            }
        }
        return timeSlot;
    }
}
