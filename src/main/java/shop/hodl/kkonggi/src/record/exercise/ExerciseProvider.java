package shop.hodl.kkonggi.src.record.exercise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.sun.model.GetSunRes;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import static shop.hodl.kkonggi.utils.Chat.replaceNickName;
import static shop.hodl.kkonggi.utils.Time.getCurrentDateStr;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;

@Service
public class ExerciseProvider {
    private final ExerciseDao exerciseDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ExerciseProvider(ExerciseDao exerciseDao, JwtService jwtService) {
        this.exerciseDao = exerciseDao;
        this.jwtService = jwtService;
    }

    public GetSunRes getExercise(int userIdx, String date) throws BaseException{
        String currentTimeStr =  getCurrentDateStr();
        if(date == null || currentTimeStr.equals(date) || date.isEmpty()) date = currentTimeStr;
        else if(!isRegexDate(date) || date.length() != 8) throw new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_DATE);
        int status = 0;
        try{
            if(checkExerciseRecord(userIdx,date) > 0) status = 1;
            GetSunRes getSunRes = exerciseDao.getExercise(userIdx, date, status);
            return getSunRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getExerciseInput(int userIdx, int scenarioIdx, String groupId) throws BaseException {
        try{
            String currentTimeStr =  getCurrentDateStr();
            if(checkExerciseRecord(userIdx, currentTimeStr) == 1) groupId = "EXE_REC_MOD";
            GetChatRes getChatRes = getChats(userIdx, scenarioIdx, groupId);
            if(groupId.equals("EXE_REC_INPUT")) getChatRes.getChat().add(exerciseDao.getImage("LAGOM_TWINKLE"));


            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkExerciseRecord(int userIdx, String date) throws BaseException{
        try{
            return exerciseDao.checkExerciseRecord(userIdx, date);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getExerciseRecord(int userIdx, String date) throws BaseException{
        try{
            return exerciseDao.getExerciseRecord(userIdx, date);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getChats(int userIdx, int scenarioIdx, String groupId) throws BaseException {
        try{
            GetChatRes getChatRes = exerciseDao.getChats(groupId, scenarioIdx);
            if(groupId.equals("EXE_REC_GOOD")) getChatRes.getChat().add(1, exerciseDao.getImage("LAGOM_TWINKLE"));
            if(groupId.equals("EXE_REC_HARD") || groupId.equals("EXE_REC_BAD")) getChatRes.getChat().add(1, exerciseDao.getImage("LAGOM_SAD"));
            if(groupId.equals("EXE_REC_FEEL_BAD")) getChatRes.getChat().add(2, exerciseDao.getImage("LAGOM_TWINKLE"));

            replaceNickName(getChatRes, getUserNickName(userIdx));
            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getChatsNoAction(int userIdx, int scenarioIdx, String groupId) throws BaseException{
        try{
            GetChatRes getChatRes = exerciseDao.getChatsNoAction(groupId, scenarioIdx);
            replaceNickName(getChatRes, getUserNickName(userIdx));
            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public String getUserNickName(int userIdx) throws BaseException {
        try {
            return exerciseDao.getUserNickName(userIdx);
        } catch (Exception exception){
            throw  new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
