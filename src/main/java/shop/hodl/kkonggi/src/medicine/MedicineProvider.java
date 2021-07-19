package shop.hodl.kkonggi.src.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.medicine.model.GetMedChatRes;
import shop.hodl.kkonggi.utils.JwtService;
import sun.text.resources.CollationData;

import java.util.Collections;

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



}
