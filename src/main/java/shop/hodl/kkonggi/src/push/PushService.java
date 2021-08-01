package shop.hodl.kkonggi.src.push;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.src.firebase.FirebaseCloudMessageService;
import shop.hodl.kkonggi.src.push.model.GetMedicineNotification;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static shop.hodl.kkonggi.config.Constant.LogDateFormat;
import static shop.hodl.kkonggi.utils.Time.getCurrentDateStr;

@Service
@RequiredArgsConstructor
public class PushService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final PushDao pushDao;

    // 1분 주기 0 * * * * *
    // 5분 주기 0 */5 * * * *
    // 매일 자정 0 0 0 * * *

    public void medicinePush(String token, String title, String body){
        try{
            FirebaseCloudMessageService fm = new FirebaseCloudMessageService();
            fm.sendMessageTo(token, title, body);
            logger.info(LogDateFormat.format(System.currentTimeMillis()) + " FCM 성공");

        } catch (IOException ioException){
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + " FCM 전송 실패");
        }
    }

    @Scheduled(cron = "0 * * * * *")    // 1분 마다
    public void getMedicineNotificationInfo(){
        try{
            logger.info(LogDateFormat.format(System.currentTimeMillis()) + " 1분 마다 테스트입니다.");
            List<GetMedicineNotification> getPush = pushDao.getMedicineNotificationInfo();

            if(getPush == null) ;

            assert getPush != null;
            logger.info("리스트 사이즈 = " + getPush.size());
            for(int i = 0; i < Objects.requireNonNull(getPush).size(); i++){
                medicinePush(getPush.get(i).getDeviceToken(),
                        getPush.get(i).getTimeSlot() + " 알림",
                        getPush.get(i).getUserNickName() + "! 오늘 "  + getPush.get(i).getTimeSlot()+ "에는 " + getPush.get(i).getMedicineCnt() + " 개의 약이 있어요!");
            }

        } catch (Exception exception){
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + "[PUSH]Fail to GET MedicineNotification to push");
        }
    }
}
