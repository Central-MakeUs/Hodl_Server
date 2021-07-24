package shop.hodl.kkonggi.src.record.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.medicine.model.*;
import shop.hodl.kkonggi.src.medicine.model.GetMedChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;
import static shop.hodl.kkonggi.utils.Time.getDays;
import shop.hodl.kkonggi.config.Constant;

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
            exception.printStackTrace();
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


    public List<GetMedicineListRes> getTodayMedicineList(int userIdx, String date) throws BaseException{
        List<GetMedicineListRes> getMedicineListRes = new ArrayList<>();

        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        Date current = new Date();
        String currentTimeStr = dtFormat.format(current);

        if(date == null || currentTimeStr.equals(date) || date.isEmpty()) date = currentTimeStr;
        else if(!isRegexDate(date) || date.length() != 8) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);

        logger.info("date after = " + date);

        try{
            for(int i = 0; i < Constant.TIMES.get(0).size() ; i++) {
                getMedicineListRes.add(recordMedicineDao.getTodayMedicineList(userIdx, Constant.TIMES.get(0).get(i), Constant.TIMES.get(1).get(i), date));
            }
            return getMedicineListRes;
        }
        catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedicineRecordRes getSpecificMedicineRecord(int medicineIdx, String timeSlot, String date) throws BaseException{
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        Date current = new Date();
        String currentTimeStr = dtFormat.format(current);

        if(date == null || currentTimeStr.equals(date) || date.isEmpty()) date = currentTimeStr;
        else if(!isRegexDate(date) || date.length() != 8) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);

        try{
            GetMedicineRecordRes toReturn = new GetMedicineRecordRes();
            // MedicineRecord에 있는 지 확인!
            int isRecorded = checkSpecificMedicineRecord(medicineIdx, timeSlot, date);
            GetMedicine getMedicine = null;
            // 수정 화면
            if(isRecorded == 1) {
                toReturn.setStatus("modify");
                getMedicine = recordMedicineDao.getSpecificMedicineRecordModify(medicineIdx, timeSlot, date);
            }
            // 입력 화면
            else{
                toReturn.setStatus("record");
                String defulatTime = "";
                for(int i = 0; i < Constant.TIMES.get(0).size(); i++){
                    if(Constant.TIMES.get(0).get(i).equals(timeSlot)) defulatTime = Constant.TIMES.get(1).get(i);
                }
                getMedicine = recordMedicineDao.getSpecificMedicineRecord(date,defulatTime, medicineIdx, timeSlot);
            }
            getMedicine.setSlotCnt(getMedicine.getSlot().size());
            toReturn.setMedicineIdx(getMedicine.getMedicineIdx());
            toReturn.setMedicineName(getMedicine.getMedicineName());
            toReturn.setDate(getMedicine.getDate());
            toReturn.setTime(getMedicine.getTime());
            toReturn.setAmount(getMedicine.getAmount());
            toReturn.setMemo(getMedicine.getMemo());
            toReturn.setDays(getDays(getMedicine.getDays()));
            toReturn.setSlot(getMedicine.getSlot());
            toReturn.setSlotCnt(getMedicine.getSlotCnt());

            return toReturn;

        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkRecordIdx(PatchMedicineRecordReq patchReq, int medicineIdx, String timeSlot) throws BaseException{
        try{
            return recordMedicineDao.checkRecordIdx(patchReq, medicineIdx, timeSlot);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkRecordIdx(int medicineIdx, String timeSlot, String date) throws BaseException{
        try{
            return recordMedicineDao.checkRecordIdx(medicineIdx, timeSlot, date);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getRecordIdx(int medicineIdx, String slot, String date) throws BaseException{
        try{
            return recordMedicineDao.getRecordIdx(medicineIdx, slot, date);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getRecordIdx(PatchMedicineRecordReq patchReq, int medicineIdx, String timeSlot) throws BaseException{
        try{
            return recordMedicineDao.getRecordIdx(patchReq, medicineIdx, timeSlot);
        }catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    public int checkSpecificMedicineRecord(int medicineIdx, String timeSlot, String date) throws BaseException{
        try{
            return recordMedicineDao.checkSpecificMedicineRecord(medicineIdx, timeSlot, date);
        }catch (Exception exception){
            exception.printStackTrace();
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

    public int checMedicineRecordAmount(int medicineIdx, String timeSlot) throws BaseException {
        try{
            return recordMedicineDao.checMedicineRecordAmount(medicineIdx, timeSlot);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 가장 최근 복용량
    public double getLatestMedicineAmount(int medicineIdx, String timeSlot) throws BaseException{
        try{
            int checkMedicineRecord = checMedicineRecordAmount(medicineIdx, timeSlot);
            if (checkMedicineRecord > 0) return recordMedicineDao.getLatestMedicineAmount(medicineIdx, timeSlot);
            return 1;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedChatRes getTodayMedicineStatus(int userIdx, String date, int scenarioIdx) throws BaseException{
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        Date current = new Date();
        String currentTimeStr = dtFormat.format(current);

        if(date == null || currentTimeStr.equals(date) || date.isEmpty()) date = currentTimeStr;
        else if(!isRegexDate(date) || date.length() != 8) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);

        try{
            // 오늘 기록되어야하는 record 수
            int totalCnt = getTodayRecordCnt(userIdx, date);
            int yesCnt = getTodayYesCnt(userIdx, date);
            int noCnt = getTodayNoCnt(userIdx, date);

            String gorupId;
            // 오늘 약 다 먹음
            if(totalCnt == yesCnt){
                gorupId = "MED_REC_ALL_TAKE";
            }
            // 오늘 약 다 안먹음
            else if (totalCnt == noCnt) {
                gorupId = "MED_REC_NO_TAKE";
            }
            else{
                gorupId = "MED_REC_OK";
            }

            GetMedChatRes getMedChatRes = recordMedicineDao.getChats(gorupId, scenarioIdx);
            String nickReplace = "%user_nickname%";
            for(int i = 0; i < getMedChatRes.getChat().size(); i++){
                if(getMedChatRes.getChat().get(i).getContent().contains(nickReplace))
                    getMedChatRes.getChat().get(i).setContent(getMedChatRes.getChat().get(i).getContent().replace(nickReplace, getUserNickName(userIdx)));
            }

            return getMedChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getTodayRecordCnt(int userIdx, String date) throws  BaseException{
        try{
            return recordMedicineDao.getTodayRecordCnt(userIdx, date);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getTodayYesCnt(int userIdx, String date) throws  BaseException{
        try{
            return recordMedicineDao.getTodayYesCnt(userIdx, date);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getTodayNoCnt(int userIdx, String date) throws  BaseException{
        try{
            return recordMedicineDao.getTodayNoCnt(userIdx, date);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedChatRes getChats(int userIdx, int scenarioIdx, String groupId) throws  BaseException{
        try{
            GetMedChatRes getMedChatRes = recordMedicineDao.getChats(groupId, scenarioIdx);
            String nickReplace = "%user_nickname%";
            for(int i = 0; i < getMedChatRes.getChat().size(); i++){
                if(getMedChatRes.getChat().get(i).getContent().contains(nickReplace))
                    getMedChatRes.getChat().get(i).setContent(getMedChatRes.getChat().get(i).getContent().replace(nickReplace, getUserNickName(userIdx)));
            }
            return getMedChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedChatRes getChatsNoAction(int userIdx, int scenarioIdx, String groupId) throws  BaseException{
        try{
            GetMedChatRes getMedChatRes = recordMedicineDao.getChatsNoAction(groupId, scenarioIdx);
            String nickReplace = "%user_nickname%";
            for(int i = 0; i < getMedChatRes.getChat().size(); i++){
                if(getMedChatRes.getChat().get(i).getContent().contains(nickReplace))
                    getMedChatRes.getChat().get(i).setContent(getMedChatRes.getChat().get(i).getContent().replace(nickReplace, getUserNickName(userIdx)));
            }
            return getMedChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
