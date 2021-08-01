package shop.hodl.kkonggi.src.record.symptom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.symptom.model.GetSymptomRes;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;
import static shop.hodl.kkonggi.utils.Time.getCurrentDateStr;
import static shop.hodl.kkonggi.utils.Chat.replaceNickName;
import static shop.hodl.kkonggi.utils.Chat.getSymptoms;

@Service
public class SymptomProvider {
    private final SymptomDao symptomDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SymptomProvider(SymptomDao symptomDao, JwtService jwtService) {
        this.symptomDao = symptomDao;
        this.jwtService = jwtService;
    }

    public GetChatRes getRecordSymptom(int userIdx, String date, int scenarioIdx, String groupId) throws BaseException {

        String currentTimeStr =  getCurrentDateStr();
        if(date == null || currentTimeStr.equals(date) || date.isEmpty()) date = currentTimeStr;
        else if(!isRegexDate(date) || date.length() != 8) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        try{
            // 이미 증상을 기록한 경우
            if(groupId.equals("SYM_REC_IS") && checkSymptomOfDay(userIdx, date) == 1) groupId = "SYM_REC_MOD";

            GetChatRes getChatRes = symptomDao.getChats(groupId, scenarioIdx);
            if(groupId.equals("SYM_REC_IS")) getChatRes.getChat().add(symptomDao.getImage("LAGOM_SAD"));
            replaceNickName(getChatRes, getUserNickName(userIdx));
            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetSymptomRes getSymptomList(int userIdx, String date) throws BaseException{
        String currentTimeStr =  getCurrentDateStr();
        if(date == null || currentTimeStr.equals(date) || date.isEmpty()) date = currentTimeStr;
        else if(!isRegexDate(date) || date.length() != 8) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        int getChecked = 0;
        try{
            GetSymptomRes getEmptySymptom =  getEmptySymptom(checkSymptomOfDay(userIdx, date));
            // 이미 기록됨
            if(checkSymptomOfDay(userIdx, date) == 1) {
                getChecked = getChecked(userIdx, date);
                getEmptySymptom = getSymptoms(getChecked, getEmptySymptom);
            }
            return getEmptySymptom;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getChats(int userIdx, int scenarioIdx, String groupId) throws BaseException{
        try{
            GetChatRes getChatRes = symptomDao.getChats(groupId, scenarioIdx);
            getChatRes = replaceNickName(getChatRes, getUserNickName(userIdx));
            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getChecked(int userIdx, String date) throws BaseException{
        try {
            return symptomDao.getChecked(userIdx, date);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetSymptomRes getEmptySymptom(int status) throws BaseException{
        try {
            return symptomDao.getEmptySymptom(status);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    public GetChatRes getChatsNoAction(int userIdx, int scenarioIdx, String groupId) throws BaseException{
        try{
            GetChatRes getChatRes = symptomDao.getChatsNoAction(groupId, scenarioIdx);
            replaceNickName(getChatRes, getUserNickName(userIdx));
            if(groupId.equals("SYM_REC_ASK_NO")) {
                getChatRes.getChat().add(1, symptomDao.getImage("LAGOM_SAD"));
            }
            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkSymptomOfDay(int userIdx, String date) throws BaseException{
        try{
            return symptomDao.checkSymptomOfDay(userIdx, date);
        } catch (Exception exception){
            throw  new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getSymptomOfDay(int userIdx, String date) throws BaseException{
        try{
            return symptomDao.getSymptomOfDay(userIdx, date);
        } catch (Exception exception){
            throw  new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public String getUserNickName(int userIdx) throws BaseException {
        try {
            return symptomDao.getUserNickName(userIdx);
        } catch (Exception exception){
            throw  new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
