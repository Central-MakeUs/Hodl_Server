package shop.hodl.kkonggi.src.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.medicine.model.GetMedAddTime;
import shop.hodl.kkonggi.src.medicine.model.GetStepperChatRes;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

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

    public GetChatRes getMedAddInput() throws BaseException{
        try{
            String gorupId = "";
            int scenarioIdx = 2;

            GetChatRes getChatRes = medicineDao.getChats(gorupId, scenarioIdx);

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

    public GetStepperChatRes getMedAdd(String groupId, int scenarioIdx, int stepNumber) throws BaseException{
        try{

            GetStepperChatRes getStepperChatRes = medicineDao.getStepperChats(groupId, scenarioIdx, stepNumber);

            return getStepperChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetStepperChatRes getMedAddCycle(String name) throws BaseException{
        try{
            String gorupId = "MED_ADD_CYCLE";
            int scenarioIdx = 2;
            int stepNumber = 2;

            GetStepperChatRes getStepperChatRes = medicineDao.getStepperChats(gorupId, scenarioIdx, stepNumber);

            // 약 이름 바꿈
            String toReplace = "%MED_ADD_002_01_답변%";
            getStepperChatRes.getStepperChat().get(0).setContent(getStepperChatRes.getStepperChat().get(0).getContent().replace(toReplace, name));

            return getStepperChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetMedAddTime getMedAddTime() throws BaseException{
        try{
            String gorupId = "MED_ADD_TIME";
            int scenarioIdx = 2;
            int stepNumber = 5;
            GetMedAddTime getMedAddTime = medicineDao.getMedAddTime(gorupId, scenarioIdx, stepNumber);
            return getMedAddTime;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getMedChats(int userIdx, String groupId, int scenarioIdx) throws BaseException {
        try{
            GetChatRes getChatRes = medicineDao.getChats(groupId, scenarioIdx);

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
