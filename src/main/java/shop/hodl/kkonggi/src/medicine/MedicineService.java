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
        int times = intArrayToInt(medicineDTO.getTimes());

        if(days == 0) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        if(times == 0) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_TIME);

        if(medicineProvider.checkMedicine(userIdx, medicineDTO.getName()) == 1)
            throw new BaseException(BaseResponseStatus.POST_MEDICINE_EXISTS);

        try{
            // 2일에 한 번씩
            if(medicineDTO.getDays()[0] == 2) {
                cycle = "2";
                days = 0;
            }
            else cycle = "S";
            PostMedicineReq postMedicineReq = new PostMedicineReq(userIdx, medicineDTO.getName(), cycle, days, times, medicineDTO.getStart(), medicineDTO.getEnd());

            int medicineIdx = medicineDao.createMedicine(postMedicineReq);
            int scenarioIdx;
            String groupId;

            if(medicineIdx > 1000){
                scenarioIdx = 2;
                groupId = "MED_ADD_SUCCESS";
            } else{
                scenarioIdx = 0;
                groupId = "SAVE_FAIL";
            }
            return medicineProvider.getMedChats(postMedicineReq.getUserIdx(), groupId, scenarioIdx);

        } catch (Exception exception) {
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
}
