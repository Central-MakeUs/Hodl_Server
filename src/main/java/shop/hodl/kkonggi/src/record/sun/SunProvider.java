package shop.hodl.kkonggi.src.record.sun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.sleep.model.GetSleepRes;
import shop.hodl.kkonggi.src.record.sun.model.GetSunRes;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import static shop.hodl.kkonggi.utils.Chat.replaceNickName;
import static shop.hodl.kkonggi.utils.Time.getCurrentDateStr;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;

@Service
public class SunProvider {
    private final SunDao sunDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SunProvider(SunDao sunDao, JwtService jwtService) {
        this.sunDao = sunDao;
        this.jwtService = jwtService;
    }

    public GetSunRes getSun(int userIdx, String date) throws BaseException{
        String currentTimeStr =  getCurrentDateStr();
        if(date == null || currentTimeStr.equals(date) || date.isEmpty()) date = currentTimeStr;
        else if(!isRegexDate(date) || date.length() != 8) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        int status = 0;
        try{
            if(checkSunRecord(userIdx,date) > 0) status = 1;
            GetSunRes getSunRes = sunDao.getSun(userIdx, date, status);
            return getSunRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getSunInput(int userIdx, int scenarioIdx, String groupId) throws BaseException{
        try{
            String currentTimeStr =  getCurrentDateStr();
            if(checkSunRecord(userIdx, currentTimeStr) == 1) groupId = "SUN_REC_MOD";
            return getChats(userIdx, scenarioIdx, groupId);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkSunRecord(int userIdx, String date) throws BaseException{
        try{
            return sunDao.checkSunRecord(userIdx, date);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int getSunRecord(int userIdx, String date)throws BaseException {
        try{
            return sunDao.getSunRecord(userIdx, date);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getChats(int userIdx, int scenarioIdx, String groupId) throws BaseException {
        try{
            GetChatRes getChatRes = sunDao.getChats(groupId, scenarioIdx);
            getChatRes = replaceNickName(getChatRes, getUserNickName(userIdx));
            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetChatRes getChatsNoAction(int userIdx, int scenarioIdx, String groupId) throws BaseException{
        try{
            GetChatRes getChatRes = sunDao.getChatsNoAction(groupId, scenarioIdx);
            getChatRes = replaceNickName(getChatRes, getUserNickName(userIdx));
            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public String getUserNickName(int userIdx) throws BaseException {
        try {
            return sunDao.getUserNickName(userIdx);
        } catch (Exception exception){
            throw  new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
