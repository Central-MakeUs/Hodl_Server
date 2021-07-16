package shop.hodl.kkonggi.record.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.record.medicine.model.GetMedicineListRes;
import shop.hodl.kkonggi.src.medicine.model.GetMedChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class RecordMedicineProvider {
    private final JwtService jwtService;
    private final RecordMedicineDao recordMedicineDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RecordMedicineProvider(JwtService jwtService, RecordMedicineDao recordMedicineDao) {
        this.jwtService = jwtService;
        this.recordMedicineDao = recordMedicineDao;
    }

    public GetMedChatRes getRecordMedicineInput(int userIdx, int scscenarioIdx) throws BaseException {
        try{
            String gorupId = "";

            // 유저가 약을 등록했는지??
            int isMedicine = checkUserMedicine(userIdx);
            if(isMedicine == 0) {
                gorupId = "MED_REC_INPUT_ADD";
                return recordMedicineDao.getChats(gorupId, scscenarioIdx);
            }

            int isTodayMedicine = checkTodayMedicine(userIdx);

            // 유저가 오늘 먹을 약물이 있는 경우
            if(isTodayMedicine > 0) gorupId = "MED_REC_INPUT_EXIST";
            // 유저가 오늘 먹을 약물이 없는 경우
            else gorupId = "MED_REC_INPUT_NONE";

            GetMedChatRes getMedChatRes = recordMedicineDao.getChats(gorupId, scscenarioIdx);

            String nickNameReplace = "%user_nickname%";
            String timeSlotReplace = "%time_slot%";
            String timeSlotCntReplace = "%time_slot_cnt%";
            String medicineTypeCntReplace = "%medicine_type_cnt%";
            for(int i = 0; i < getMedChatRes.getChat().size(); i++){
                if(getMedChatRes.getChat().get(i).getContent().contains(nickNameReplace)){
                    getMedChatRes.getChat().get(i).setContent(getMedChatRes.getChat().get(i).getContent().replace(nickNameReplace, getUserNickName(userIdx)));
                }
                if(getMedChatRes.getChat().get(i).getContent().contains(timeSlotReplace)){
                    getMedChatRes.getChat().get(i).setContent(getMedChatRes.getChat().get(i).getContent().replace(timeSlotReplace, getTimeSlot(userIdx)));
                }
                if(getMedChatRes.getChat().get(i).getContent().contains(timeSlotCntReplace)){
                    getMedChatRes.getChat().get(i).setContent(getMedChatRes.getChat().get(i).getContent().replace(timeSlotCntReplace, Integer.toString(getTimeSlotCnt(userIdx))));
                }
                if(getMedChatRes.getChat().get(i).getContent().contains(medicineTypeCntReplace)){
                    getMedChatRes.getChat().get(i).setContent(getMedChatRes.getChat().get(i).getContent().replace(medicineTypeCntReplace,Integer.toString(getMedicineType(userIdx))));
                }
            }

            return getMedChatRes;
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public String getTimeSlot(int userIdx) throws BaseException{
        try{
            List<String> timeSlot = recordMedicineDao.getTimeSlot(userIdx);
            String resultSlot = "";

            for(int i = 0; i < timeSlot.size(); i++){
                if(i == 0) resultSlot += timeSlot.get(i);
                else resultSlot += "," + timeSlot.get(i);
            }

            return resultSlot;
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getTimeSlotCnt(int userIdx) throws BaseException{
        try{
            return recordMedicineDao.getTimeSlotCnt(userIdx);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getMedicineType(int userIdx) throws BaseException{
        try{
            return recordMedicineDao.getMedicineType(userIdx);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    public String getUserNickName(int userIdx) throws BaseException{
        try{
            return recordMedicineDao.getUserNickName(userIdx);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    public List<GetMedicineListRes> getTodayMedicineList(int userIdx) throws BaseException{
        try{
            // 시간대 list에
            List<String> timeSlot = Arrays.asList("D", "M", "L", "E", "N");
            List<String> defaultSlot = Arrays.asList("06:00", "09:00", "12:00", "18:00", "21:00");
            List<GetMedicineListRes> getMedicineListRes = new ArrayList<>();
            for(int i = 0; i < timeSlot.size(); i++){
                //if(i == 0) getMedicineListRes.set(recordMedicineDao.getTodayMedicineList(userIdx, timeSlot.get(i), defaultSlot.get(i)));
                getMedicineListRes.add(recordMedicineDao.getTodayMedicineList(userIdx, timeSlot.get(i), defaultSlot.get(i)));
            }
            return getMedicineListRes;

        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 유저의 Medicine 존재하는 지 체크
    public int checkUserMedicine(int userIdx) throws BaseException{
        try{
            return recordMedicineDao.checkUserMedicine(userIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkTodayMedicine(int userIdx) throws BaseException{
        try{
            return recordMedicineDao.checkTodayMedicine(userIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
