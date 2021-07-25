package shop.hodl.kkonggi.src.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.medicine.model.GetMedChatRes;
import shop.hodl.kkonggi.src.medicine.model.MedicineDTO;
import shop.hodl.kkonggi.src.medicine.model.PatchDeleteReq;
import shop.hodl.kkonggi.src.medicine.model.PostMedicineReq;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;

import static shop.hodl.kkonggi.utils.Cycle.*;

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

        int days = intArrayToInt(medicineDTO.getDays());
        ArrayList<String> timeSlot = toTimeSlot(medicineDTO.getTimes());

        if(days == 0) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        if(timeSlot.isEmpty()) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_TIME);

        int scenarioIdx;
        String groupId;

        if(medicineProvider.checkMedicine(userIdx, medicineDTO.getName()) == 1){
            logger.info(medicineDTO.getName());
            scenarioIdx = 2;
            groupId = "MED_ADD_EXITS";
            String medicineReplace = "%medicine_name%";
            GetMedChatRes getMedChatRes = medicineProvider.getMedChatExist(groupId, scenarioIdx);
            for(int i = 0; i < getMedChatRes.getChat().size(); i++){
                if(getMedChatRes.getChat().get(i).getContent().contains(medicineReplace))
                    getMedChatRes.getChat().get(i).setContent(getMedChatRes.getChat().get(i).getContent().replace(medicineReplace, medicineDTO.getName()));
            }
            return getMedChatRes;
        }

        try{
            PostMedicineReq postMedicineReq = new PostMedicineReq(userIdx, medicineDTO.getName(), days, medicineDTO.getStart(), medicineDTO.getEnd());

            int medicineIdx = medicineDao.createMedicine(postMedicineReq);
            int medicineTime = 0;
            for(String time : timeSlot){
                medicineTime = medicineDao.createMedicineTime(medicineIdx, time);
                if(medicineTime == 0) break;
            }

            if(medicineIdx > 0 && medicineTime > 0){
                scenarioIdx = 2;
                groupId = "MED_ADD_SUCCESS";
            } else{
                logger.error( "약물 저장 실패 시나리오,  " + "userIdx = " + userIdx);
                scenarioIdx = 0;
                groupId = "SAVE_FAIL";
            }
            GetMedChatRes getMedChatRes = medicineProvider.getMedChats(postMedicineReq.getUserIdx(), groupId, scenarioIdx);
            int toBeFirst = getMedChatRes.getAction().getChoiceList()
                    .indexOf(getMedChatRes.getAction().getChoiceList().stream().filter(e -> e.getContent().equals("약 복용 기록할래")).findFirst().get());
            int lastIndex = getMedChatRes.getAction().getChoiceList().size() - 1;
            // Action 순서 변경
            Collections.swap(getMedChatRes.getAction().getChoiceList(), 0, toBeFirst);
            int toBeLast = getMedChatRes.getAction().getChoiceList()
                    .indexOf(getMedChatRes.getAction().getChoiceList().stream().filter(e -> e.getContent().equals("아니, 괜찮아")).findFirst().get());
            // Action 순서 변경
            Collections.swap(getMedChatRes.getAction().getChoiceList(), toBeLast, lastIndex);
            return getMedChatRes;
        } catch (Exception exception) {
            logger.error( "약물 저장 실패 DB, " + "userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 특정 약물 삭제
    @Transactional
    public Integer deleteMedicine(int userIdx, int medicineIdx ,PatchDeleteReq patchDeleteReq) throws BaseException{
        // 해당 약물이 있는 지 or 삭제된 것인지
        if(medicineProvider.checkMedicine(userIdx, medicineIdx) == 0)
            throw new BaseException(BaseResponseStatus.PATCH_MEDICINE_EXISTS);
        try{
            // 해당 약물이
            int result = medicineDao.deleteMedicine(patchDeleteReq);
            deleteMedicineTime(userIdx, medicineIdx,patchDeleteReq);
            return patchDeleteReq.getMedicineIdx();
        } catch (Exception exception) {
            logger.error( "약물 삭제 실패 DB, " + "userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 특정 약물 삭제
    @Transactional
    public Integer deleteMedicineTime(int userIdx, int medicineIdx ,PatchDeleteReq patchDeleteReq) throws BaseException{
        // 해당 약물이 있는 지 or 삭제된 것인지
        if(medicineProvider.checkMedicineTime(medicineIdx) == 0)
            throw new BaseException(BaseResponseStatus.PATCH_MEDICINE_EXISTS);
        try{
            // 해당 약물이
            int result = medicineDao.deleteMedicineTime(patchDeleteReq);
            return patchDeleteReq.getMedicineIdx();
        } catch (Exception exception) {
            logger.error( "MedicineTime 삭제 실패 DB, " + "userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
