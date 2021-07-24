package shop.hodl.kkonggi.src.record.sleep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.sleep.model.PostSleepReq;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;
import static shop.hodl.kkonggi.utils.Time.getCurrentDateStr;
import static shop.hodl.kkonggi.utils.SleepTime.subTime;
import static shop.hodl.kkonggi.utils.Chat.makeSaveFailChat;

@Service
public class SleepService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SleepDao sleepDao;
    private final SleepProvider sleepProvider;
    private final JwtService jwtService;


    @Autowired
    public SleepService(SleepDao sleepDao, SleepProvider sleepProvider, JwtService jwtService) {
        this.sleepDao = sleepDao;
        this.sleepProvider = sleepProvider;
        this.jwtService = jwtService;
    }

    @Transactional
    public GetChatRes createSleepRecord(int userIdx, PostSleepReq postReq) throws BaseException{
        if(postReq.getDate() == null || getCurrentDateStr().equals(postReq.getDate()) || postReq.getDate().isEmpty()) postReq.setDate(getCurrentDateStr());
        else if(!isRegexDate(postReq.getDate()) || postReq.getDate().length() != 8)
            throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        try{
            String groupId = "";
            int scenarioIdx = 5;
            GetChatRes getChatRes;
            int result = sleepDao.createSleepRecord(userIdx, postReq);
            if(result > 0){
                if(postReq.getIsSleep() == 0) groupId = "SLEEP_REC_LESS";
                else{
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    Date mid = format.parse("24:00");
                    Date sleepTime = format.parse(postReq.getSleepTime());
                    Date wakeUpTime = format.parse(postReq.getWakeUpTime());
                    logger.info("sleepTime = " + sleepTime);
                    logger.info("wakeUpTime = " + wakeUpTime);
                    long diff = subTime(sleepTime, wakeUpTime, mid);

                    logger.info("timeDiff = " + diff);
                    if(diff < 5) groupId = "SLEEP_REC_LESS";
                    else if(12 < diff) groupId = "SLEEP_REC_MORE";
                    else {
                        groupId = "SLEEP_REC_MID";
                        getChatRes = sleepProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
                        return getChatRes;
                    }
                }
            }else{
                groupId = "SAVE_FAIL";
                scenarioIdx = 0;
            }
            getChatRes = sleepProvider.getChats(userIdx, scenarioIdx, groupId);
            return getChatRes;
        } catch (Exception exception){
            logger.error("userIdx = " + userIdx + "post sleep fail");
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}