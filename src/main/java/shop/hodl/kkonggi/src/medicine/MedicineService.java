package shop.hodl.kkonggi.src.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.medicine.model.*;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static shop.hodl.kkonggi.config.Constant.LogDateFormat;
import static shop.hodl.kkonggi.utils.Cycle.*;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;

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
    public GetChatRes createMedicine(int userIdx, MedicineDTO medicineDTO) throws BaseException {

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
            GetChatRes getMedChatRes = medicineProvider.getMedChatExist(groupId, scenarioIdx);
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
                logger.error( "?????? ?????? ?????? ????????????,  " + "userIdx = " + userIdx);
                scenarioIdx = 0;
                groupId = "SAVE_FAIL";
            }
            GetChatRes getMedChatRes = medicineProvider.getMedChats(postMedicineReq.getUserIdx(), groupId, scenarioIdx);
            int toBeFirst = getMedChatRes.getAction().getChoiceList()
                    .indexOf(getMedChatRes.getAction().getChoiceList().stream().filter(e -> e.getContent().equals("??? ?????? ????????????")).findFirst().get());
            int lastIndex = getMedChatRes.getAction().getChoiceList().size() - 1;
            // Action ?????? ??????
            Collections.swap(getMedChatRes.getAction().getChoiceList(), 0, toBeFirst);
            int toBeLast = getMedChatRes.getAction().getChoiceList()
                    .indexOf(getMedChatRes.getAction().getChoiceList().stream().filter(e -> e.getContent().equals("??????, ?????????")).findFirst().get());
            // Action ?????? ??????
            Collections.swap(getMedChatRes.getAction().getChoiceList(), toBeLast, lastIndex);
            return getMedChatRes;
        } catch (Exception exception) {
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + "Fail to STORE Medicine in DB, " + "userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // ?????? ?????? ??????
    @Transactional
    public Integer updateMedicineDetail(int userIdx, int medicineIdx, MedicineDTO medicineDTO) throws BaseException{
        if(!isRegexDate(medicineDTO.getStart()))
            throw  new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);

        int days = intArrayToInt(medicineDTO.getDays());
        ArrayList<String> toBeModifiedTimeSlot = toTimeSlot(medicineDTO.getTimes());

        if(days == 0) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);

        if(toBeModifiedTimeSlot.isEmpty()) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_TIME);

        if(medicineProvider.checkMedicine(userIdx, medicineIdx) == 0)
            throw new BaseException(BaseResponseStatus.PUT_MEDICINE_NO);
        try{
            PutMedicineReq putMedicineReq = new PutMedicineReq(userIdx, medicineIdx, medicineDTO.getName(), medicineDTO.getMedicineDetail(), medicineDTO.getStart(), medicineDTO.getEnd(), days);

            // Medicine ????????????
            int updateResult = medicineDao.updateMedicineDetail(putMedicineReq);
            if(updateResult == 0) throw new BaseException(BaseResponseStatus.DATABASE_ERROR);

            List<String> getTimeSlotInDB = medicineProvider.getTimeSlot(medicineIdx);   // ?????? TimeSlot
            List<String> toBeletedTimeSlot = getTimeSlotInDB.stream().filter(x -> !toBeModifiedTimeSlot.contains(x)).collect(Collectors.toList());    // status -> N
            List<String> toBeAddTimeSlot = toBeModifiedTimeSlot.stream().filter(x -> !getTimeSlotInDB.contains(x)).collect(Collectors.toList());    // create of status -> Y

            int medicineTime = 0;
            // ????????? ??????
            for(String beDeletedTimeSlot : toBeletedTimeSlot){
                if(medicineProvider.checkMedicineTime(medicineIdx, beDeletedTimeSlot, "Y") == 0)
                    throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
                medicineTime = medicineDao.updateMedicineTime(medicineIdx, beDeletedTimeSlot, "N");
                if(medicineTime == 0) throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }
            // ????????? ??????
            for(String beAddedTimeSlot : toBeAddTimeSlot){
                if(medicineProvider.checkMedicineTime(medicineIdx, beAddedTimeSlot, "N") == 0) medicineTime = medicineDao.createMedicineTime(medicineIdx, beAddedTimeSlot);
                else medicineTime = medicineDao.updateMedicineTime(medicineIdx, beAddedTimeSlot, "Y");
                if(medicineTime == 0) throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }
            return medicineIdx;
        } catch (Exception exception) {
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + "Fail to MODIFY Medicine, " + "userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // ?????? ?????? ??????
    @Transactional
    public Integer deleteMedicine(int userIdx, int medicineIdx ,PatchDeleteReq patchDeleteReq) throws BaseException{
        // ?????? ????????? ?????? ??? or ????????? ?????????
        if(medicineProvider.checkMedicine(userIdx, medicineIdx) == 0)
            throw new BaseException(BaseResponseStatus.PATCH_MEDICINE_EXISTS);
        try{
            // ?????? ?????????
            int result = medicineDao.deleteMedicine(patchDeleteReq);
            deleteMedicineTime(userIdx, medicineIdx,patchDeleteReq);
            patchDeleteReq.getMedicineIdx();
            return medicineProvider.getMedicineCnt(userIdx);
        } catch (Exception exception) {
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + "Fail to DELETE Medicine, " + "userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // ?????? ?????? ??????
    @Transactional
    public Integer deleteMedicineTime(int userIdx, int medicineIdx ,PatchDeleteReq patchDeleteReq) throws BaseException{
        // ?????? ????????? ?????? ??? or ????????? ?????????
        if(medicineProvider.checkMedicineTime(medicineIdx) == 0)
            throw new BaseException(BaseResponseStatus.PATCH_MEDICINE_EXISTS);
        try{
            // ?????? ?????????
            int result = medicineDao.deleteMedicineTime(patchDeleteReq);
            return patchDeleteReq.getMedicineIdx();
        } catch (Exception exception) {
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + "Fail to DELETE MedicineTIME, " + "userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
