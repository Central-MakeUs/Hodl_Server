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

    public BaseResponse<String> medicinePush(String token, String title, String body){
        try{
            FirebaseCloudMessageService fm = new FirebaseCloudMessageService();
            //String token = "cZZLgiP3QdavHtdeUT2WPi:APA91bGZ84BQHEvQFOUSUycjVMg1RGhD2qkeODrJUF-sCdf_Jn7IhBihtlMHH3r4qY-1BssjPzkI7Ns0p0clIV3btwIfwWdJo_QZH6H7ssScaoYhnprYGqHiDDObApMsKWDJEt32kVPA";
            //String title = "아침 물약";
            //String body = "오늘 아침에 먹을 약물은 몇 개가 있어요!";

            fm.sendMessageTo(token, title, body);
            logger.info(getCurrentDateStr() + " FCM 성공");

            return new BaseResponse<>(token);
        } catch (IOException ioException){
            logger.error(getCurrentDateStr() + " FCM 실패");
            ioException.printStackTrace();
            return new BaseResponse<>("실패");
        }
    }

    @Scheduled(cron = "0 * * * * *")    // 1분 마다
    public void getMedicineNotificationInfo(){
        try{
            logger.info(LogDateFormat.format(System.currentTimeMillis()) + " 1분 마다 테스트입니다.");
            List<GetMedicineNotification> getPushList = pushDao.getMedicineNotificationInfo();

            for(int i = 0; i < getPushList.size(); i++){

            }

        } catch (Exception exception){
            logger.error(LogDateFormat.format(System.currentTimeMillis()) + "[PUSH]Fail to GET MedicineNotification to push");
        }
    }
}
