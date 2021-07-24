package shop.hodl.kkonggi.src.record.sleep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.sleep.model.GetSleepRes;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import static shop.hodl.kkonggi.utils.Chat.replaceNickName;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;
import static shop.hodl.kkonggi.utils.Time.getCurrentDateStr;

@Service
public class SleepProvider {
    private final SleepDao sleepDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SleepProvider(SleepDao sleepDao, JwtService jwtService) {
        this.sleepDao = sleepDao;
        this.jwtService = jwtService;
    }

    // 디폴트 일어나는 시간 : 오전 6시
    // 디폴트 잠 든 시간 : 오후 9시
    public GetSleepRes getSleep(int userIdx, String date) throws BaseException{
        String currentTimeStr =  getCurrentDateStr();
        if(date == null || currentTimeStr.equals(date) || date.isEmpty()) date = currentTimeStr;
        else if(!isRegexDate(date) || date.length() != 8) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        int status = 0;
        try{
            if(checkSleepRecord(userIdx,date) > 0) status = 1;
            GetSleepRes getSleepRes = getSleep(userIdx, date, status);
            return getSleepRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetSleepRes getSleep(int userIdx, String date, int status) throws BaseException{
        try {
            return sleepDao.getSleep(userIdx, date, status);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getSleepInput(int userIdx, int scenarioIdx, String groupId) throws BaseException{
        try{
            // 오늘 잠 기록 했는 지,
            String currentTimeStr =  getCurrentDateStr();
            if(checkSleepRecord(userIdx,currentTimeStr) == 1) groupId = "SLEEP_REC_MOD";
            return getChats(userIdx, scenarioIdx, groupId);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkSleepRecord(int userIdx, String date) throws BaseException{
        try{
            return sleepDao.checkSleepRecord(userIdx, date);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getSleepRecord(int userIdx, String date) throws BaseException{
        try{
            return sleepDao.getSleepRecord(userIdx, date);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getChats(int userIdx, int scenarioIdx, String groupId) throws BaseException {
        try{
            GetChatRes getChatRes = sleepDao.getChats(groupId, scenarioIdx);
            getChatRes = replaceNickName(getChatRes, getUserNickName(userIdx));
            return getChatRes;
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getChatsNoAction(int userIdx, int scenarioIdx, String groupId) throws BaseException{
        try{
            GetChatRes getChatRes = sleepDao.getChatsNoAction(groupId, scenarioIdx);
            getChatRes = replaceNickName(getChatRes, getUserNickName(userIdx));
            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public String getUserNickName(int userIdx) throws BaseException {
        try {
            return sleepDao.getUserNickName(userIdx);
        } catch (Exception exception){
            throw  new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
