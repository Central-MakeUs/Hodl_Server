package shop.hodl.kkonggi.src.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.notification.model.GetMedicineNotificationRes;
import shop.hodl.kkonggi.src.notification.model.GetNotificationRes;
import shop.hodl.kkonggi.src.notification.model.PatchMedicineNotificationReq;
import shop.hodl.kkonggi.src.notification.model.PatchNotificationReq;
import shop.hodl.kkonggi.utils.JwtService;

import javax.sound.midi.Patch;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static shop.hodl.kkonggi.config.Constant.LogDateFormat;

@Service
public class NotificationService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NotificationProvider notificationProvider;
    private final NotificationDao notificationDao;
    private final JwtService jwtService;

    @Autowired
    public NotificationService(NotificationProvider notificationProvider, NotificationDao notificationDao, JwtService jwtService){
        this.notificationProvider = notificationProvider;
        this.notificationDao = notificationDao;
        this.jwtService = jwtService;
    }

    @Transactional
    public Integer updateNotification(int userIdx, GetNotificationRes getNotificationRes) throws BaseException {
        try{
            PatchNotificationReq patReq = new PatchNotificationReq();
            if(getNotificationRes.getIsServicePush() == 1) patReq.setIsServicePush("Y"); else patReq.setIsServicePush("N");
            if(getNotificationRes.getIsMedicinePush() == 1) patReq.setIsMedicinePush("Y"); else patReq.setIsMedicinePush("N");
            if(getNotificationRes.getIsEventPush() == 1) patReq.setIsEventPush("Y"); else patReq.setIsEventPush("N");
            if(getNotificationRes.getIsMarketingPush() == 1) patReq.setIsMarketingPush("Y"); else patReq.setIsMarketingPush("N");

            Integer result = notificationDao.updateNotification(userIdx, patReq);
            if(result > 0) result = userIdx;
            return result;
        } catch (Exception exception){
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + "Fail to MODIFY Notification, " + "userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    @Transactional
    public Integer updateMedicineNotification(int userIdx, List<PatchMedicineNotificationReq> patchReq) throws BaseException{
        try{
            // D, E, L, M, N
            // D, M, L, E, N 순서!
            patchReq = patchReq.stream().sorted(Comparator.comparing(PatchMedicineNotificationReq::getTimeSlot)).collect(Collectors.toList());

            int mIndex = patchReq.indexOf(patchReq.stream().filter(e -> e.getTimeSlot().equals("M")).findFirst().get());
            int eIndex = patchReq.indexOf(patchReq.stream().filter(e -> e.getTimeSlot().equals("E")).findFirst().get());
            Collections.swap(patchReq, mIndex, eIndex);

            int result = notificationDao.updateMedicineNotification(userIdx, patchReq);
            if(result > 0) result = userIdx;
            return result;
        } catch (Exception exception){
            exception.printStackTrace();
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + "Fail to MODIFY MedicineNotification, " + "userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}
