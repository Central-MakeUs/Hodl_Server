package shop.hodl.kkonggi.src.record.sun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.sleep.model.PostSleepReq;
import shop.hodl.kkonggi.src.record.sun.model.PostSunReq;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

import static shop.hodl.kkonggi.utils.Time.getCurrentDateStr;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;

@Service
public class SunService {
    private final SunDao sunDao;
    private final SunProvider sunProvider;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SunService(SunDao sunDao, SunProvider sunProvider,JwtService jwtService) {
        this.sunDao = sunDao;
        this.sunProvider = sunProvider;
        this.jwtService = jwtService;
    }

    @Transactional
    public GetChatRes createSunRecord(int userIdx, PostSunReq postReq) throws BaseException {
        if(postReq.getDate() == null || getCurrentDateStr().equals(postReq.getDate()) || postReq.getDate().isEmpty()) postReq.setDate(getCurrentDateStr());
        else if(!isRegexDate(postReq.getDate()) || postReq.getDate().length() != 8)
            throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        if(sunProvider.checkSunRecord(userIdx, postReq.getDate()) == 1){
            throw new BaseException(BaseResponseStatus.POST_SUN_RECORD_ALREADY);
        }

        try{
            String groupId = "";
            int scenarioIdx = 6;
            GetChatRes getChatRes;
            if (postReq.getIsSun() == 0) postReq.setTotalTime("00:00");
            int result = sunDao.createSunRecord(userIdx, postReq);
            if(result > 0){
                if(postReq.getIsSun() == 0) groupId = "SUN_REC_NOT";
                else{
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    Date ten = format.parse("00:10");
                    Date thirty = format.parse("00:30");
                    Date totalTime = format.parse(postReq.getTotalTime());

                    if(totalTime.before(ten)) {
                        logger.info("[Before 10]" + ten.getTime() + " " + totalTime.getTime());
                        groupId = "SUN_REC_LESS_TEN";
                    }
                    else if(totalTime.after(thirty)) {
                        logger.info("[After 30]" + ten.getTime() + " " + totalTime.getTime());
                        groupId = "SUN_REC_MORE_THIRTY";
                    }
                    else {
                        logger.info("[10 ~ 30]" + ten.getTime() + " " + totalTime.getTime());
                        groupId = "SUN_REC_MORE_TEN";
                    }
                }
            } else{
                groupId = "SAVE_FAIL";
                scenarioIdx = 0;
            }
            getChatRes = sunProvider.getChats(userIdx, scenarioIdx, groupId);
            return getChatRes;
        } catch (Exception exception){
            logger.error("userIdx = " + userIdx + "post sun fail");
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public GetChatRes updateSunRecord(int userIdx, PostSunReq postReq) throws BaseException {
        if(postReq.getDate() == null || getCurrentDateStr().equals(postReq.getDate()) || postReq.getDate().isEmpty()) postReq.setDate(getCurrentDateStr());
        else if(!isRegexDate(postReq.getDate()) || postReq.getDate().length() != 8)
            throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        if(sunProvider.checkSunRecord(userIdx, postReq.getDate()) == 0){
            throw new BaseException(BaseResponseStatus.PATCH_SUN_RECORD_EMPTY);
        }
        try{
            int recordIdx = sunProvider.getSunRecord(userIdx, postReq.getDate());
            String groupId = "";
            int scenarioIdx = 6;
            GetChatRes getChatRes;
            if (postReq.getIsSun() == 0) postReq.setTotalTime("00:00");
            int result = sunDao.updateSunRecord(postReq, recordIdx);
            if(result > 0){
                if(postReq.getIsSun() == 0) groupId = "SUN_REC_NOT";
                else{
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    Date ten = format.parse("00:10");
                    Date thirty = format.parse("00:30");
                    Date totalTime = format.parse(postReq.getTotalTime());

                    if(totalTime.before(ten)) {
                        logger.info("[Before 10]" + ten.getTime() + " " + totalTime.getTime());
                        groupId = "SUN_REC_LESS_TEN";
                    }
                    else if(totalTime.after(thirty)) {
                        logger.info("[After 30]" + ten.getTime() + " " + totalTime.getTime());
                        groupId = "SUN_REC_MORE_THIRTY";
                    }
                    else {
                        logger.info("[10 ~ 30]" + ten.getTime() + " " + totalTime.getTime());
                        groupId = "SUN_REC_MORE_TEN";
                    }
                }
            } else{
                groupId = "SAVE_FAIL";
                scenarioIdx = 0;
            }
            getChatRes = sunProvider.getChats(userIdx, scenarioIdx, groupId);
            return getChatRes;
        } catch (Exception exception){
            logger.error("userIdx = " + userIdx + "patch sun fail");
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
