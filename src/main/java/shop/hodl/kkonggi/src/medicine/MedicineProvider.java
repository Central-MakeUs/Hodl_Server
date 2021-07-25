package shop.hodl.kkonggi.src.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.medicine.model.GetMedChatRes;
import shop.hodl.kkonggi.src.medicine.model.GetMedicine;
import shop.hodl.kkonggi.src.medicine.model.GetMedicineRes;
import shop.hodl.kkonggi.utils.JwtService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicineProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MedicineDao medicineDao;
    private final JwtService jwtService;

    @Autowired
    public MedicineProvider(MedicineDao medicineDao, JwtService jwtService) {
        this.medicineDao = medicineDao;
        this.jwtService = jwtService;
    }

    public GetMedicineRes getMedicineRes(GetMedicine getMedicine){
        GetMedicineRes getMedicineRes = new GetMedicineRes();
        getMedicineRes.setMedicineList(new ArrayList<>());

        getMedicineRes.setTotalCnt(getMedicine.getMedicineList().size());
        for(int i = 0; i < getMedicine.getMedicineList().size(); i++){
            getMedicineRes.getMedicineList().add(
                    new GetMedicineRes.Medicine(
                            getMedicine.getMedicineList().get(i).getMedicineIdx(),
                            getMedicine.getMedicineList().get(i).getMedicineName(),
                            getMedicine.getMedicineList().get(i).getAmount(),
                            getMedicine.getMedicineList().get(i).getCycle()
                    ));
        }

        return getMedicineRes;
    }

    public GetMedicineRes getMyMedicines(int userIdx, String cycle, String time, Integer endDay) throws BaseException{
        GetMedicine getMedicineRes;
        try{
            if (cycle != null) {
                // D : 새벽, M : 아침, L : 점심, E : 저녁, N : 자기전
                String[] arr = new String[5];
                String[] cycleArr = cycle.split(",");
                for(int i = 0; i < arr.length; i++){
                    if(cycleArr.length > i) arr[i] = cycleArr[i];
                    else arr[i] = "";
                }
                getMedicineRes = medicineDao.getMyMedicinesHasSlot(userIdx, arr, cycleArr.length);
            } else{
                getMedicineRes = medicineDao.getMyMedicines(userIdx);
            }
            if(time != null){
                if(time.equals("매일")){
                    getMedicineRes.setMedicineList(getMedicineRes.getMedicineList().stream().filter(t -> t.getCycle().equals("매일")).collect(Collectors.toList()));
                } else if (time.equals("요일")){
                    getMedicineRes.setMedicineList(getMedicineRes.getMedicineList().stream().filter(t -> !(t.getCycle().equals("매일"))).collect(Collectors.toList()));
                }
            }
            if(endDay != null){
                if(endDay == 1){
                    getMedicineRes.setMedicineList(getMedicineRes.getMedicineList().stream().filter(t -> (t.getEndDay() != null)).collect(Collectors.toList()));
                } else if(endDay == 0){
                    getMedicineRes.setMedicineList(getMedicineRes.getMedicineList().stream().filter(t -> (t.getEndDay() == null)).collect(Collectors.toList()));
                }
            }
            return getMedicineRes(getMedicineRes);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedChatRes getMedAddInput() throws BaseException{
        try{
            String gorupId = "";
            int scenarioIdx = 2;

            GetMedChatRes getChatRes = medicineDao.getChats(gorupId, scenarioIdx);

            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getTotalStepNumber() throws BaseException{
        try{
            int scenarioIdx = 2;
            return medicineDao.getTotalStepNumber(scenarioIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedChatRes getMedAdd(String groupId, int scenarioIdx, int stepNumber) throws BaseException{
        try{

            GetMedChatRes getMedChatRes = medicineDao.getMedChatRes(groupId, scenarioIdx, stepNumber);

            return getMedChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedChatRes getMedAddChats(int userIdx,String medName, String groupId, int scenarioIdx, int stepNumber) throws BaseException{
        try{

            GetMedChatRes getMedChatRes = medicineDao.getMedChatRes(groupId, scenarioIdx, stepNumber);
            // 약 이름 바꿈
            String replaceMedicine = "%MED_ADD_002_01_답변%";
            for(int i = 0; i < getMedChatRes.getChat().size(); i++){
                if(getMedChatRes.getChat().get(i).getContent().contains(replaceMedicine)){
                    getMedChatRes.getChat().get(0).setContent(getMedChatRes.getChat().get(0).getContent().replace(replaceMedicine, medName));
                }
            }
            int index = getMedChatRes.getAction().getChoiceList()
                    .indexOf(getMedChatRes.getAction().getChoiceList().stream().filter(e -> e.getContent().equals("이전 단계로")).findFirst().get());
            int lastIndex = getMedChatRes.getAction().getChoiceList().size() - 1;
            // Action 순서 변경
            Collections.swap(getMedChatRes.getAction().getChoiceList(), index, lastIndex);
            return getMedChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedChatRes getMedAddTime() throws BaseException{
        try{
            String gorupId = "MED_ADD_TIME";
            int scenarioIdx = 2;
            int stepNumber = 5;
            GetMedChatRes getMedChatRes = new GetMedChatRes();

            for(int i = 0; i < 2; i++){
                getMedChatRes = medicineDao.getMedAddTime(gorupId, scenarioIdx, stepNumber, getMedChatRes, i);
            }

            Collections.swap(getMedChatRes.getAction().getChoiceList(), 1, 3);
            return getMedChatRes;
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedChatRes getMedChats(int userIdx, String groupId, int scenarioIdx) throws BaseException {
        try{
            GetMedChatRes getChatRes;
            if(groupId.equals("COM_OK")) getChatRes = medicineDao.getChatsNoAction(groupId, scenarioIdx);
            else getChatRes = medicineDao.getChats(groupId, scenarioIdx);

            // 닉네임 변경
            String replace = "%user_nickname%";
            for(int i = 0; i < getChatRes.getChat().size(); i++){
                if(getChatRes.getChat().get(i).getContent().contains(replace)){
                    getChatRes.getChat().get(i).setContent(getChatRes.getChat().get(i).getContent().replace(replace, getUserNickName(userIdx)));
                }
            }

            return getChatRes;
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedChatRes getMedChatExist(String groupId, int scenarioIdx) throws BaseException{
        try{
            // todo : 2차 출시 -> Action있도록 바꿔야함
            return medicineDao.getChatsNoAction(groupId, scenarioIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    public GetMedChatRes getSaveFailedChats(int userIdx) throws BaseException{
        int scenarioIdx = 0;
        String groupId = "SAVE_FAIL";

        try{
            GetMedChatRes getMedChatRes = medicineDao.getChats(groupId, scenarioIdx);
            return getMedChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public String getUserNickName(int userIdx) throws BaseException{
        try{
            return medicineDao.getUserNickName(userIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkMedicine(int userIdx, String medicineRealName) throws BaseException{
        try{
            return medicineDao.checkMedicine(userIdx, medicineRealName);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkMedicine(int userIdx, int medicineIdx) throws BaseException{
        try{
            return medicineDao.checkMedicine(userIdx, medicineIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkMedicineTime(int medicineIdx) throws BaseException{
        try{
            return medicineDao.checkMedicineTime(medicineIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



}
