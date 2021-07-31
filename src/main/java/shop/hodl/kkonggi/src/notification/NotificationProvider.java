package shop.hodl.kkonggi.src.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.notification.model.GetMedicineNotificationRes;
import shop.hodl.kkonggi.src.notification.model.GetNotificationRes;
import shop.hodl.kkonggi.src.notification.model.PostMedicineNotificationReq;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static shop.hodl.kkonggi.config.Constant.TIMES;
import static shop.hodl.kkonggi.config.Constant.LogDateFormat;

@Service
public class NotificationProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final NotificationDao notificationDao;
    private final JwtService jwtService;

    @Autowired
    public NotificationProvider(NotificationDao notificationDao, JwtService jwtService) {
        this.notificationDao = notificationDao;
        this.jwtService = jwtService;
    }

    // NotificationTB create 존재
    @Transactional
    public GetNotificationRes getNotification(int userIdx) throws BaseException {
        try{
            if(checkNotification(userIdx) == 0) notificationDao.createNotification(userIdx);
            return notificationDao.getNotification(userIdx);
        } catch (Exception exception){
            exception.printStackTrace();
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + " Fail to CREATE or GET Notification, userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // MedicineNotificationTB create 존재함
    @Transactional
    public GetMedicineNotificationRes getMedicineNotification(int userIdx) throws BaseException {
        try{
            if(checkMedicineNotification(userIdx) == 0){
                List<PostMedicineNotificationReq> postMedicineNotificationReqList = new ArrayList<>();
                for(int i = 0; i < TIMES.get(0).size(); i++){
                    postMedicineNotificationReqList.add(new PostMedicineNotificationReq(userIdx, TIMES.get(0).get(i), TIMES.get(1).get(i)));
                }
                notificationDao.createMedicineNotification(postMedicineNotificationReqList);
            }
            return notificationDao.getMedicineNotification(userIdx);
        } catch (Exception exception){
            exception.printStackTrace();
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + " Fail to CREATE or GET MedicineNotification, userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkNotification(int userIdx) throws BaseException{
        try{
            return notificationDao.checkNotification(userIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkMedicineNotification(int userIdx) throws BaseException{
        try{
            return notificationDao.checkMedicineNotification(userIdx);
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
