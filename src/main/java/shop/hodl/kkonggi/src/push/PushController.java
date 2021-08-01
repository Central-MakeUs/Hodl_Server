package shop.hodl.kkonggi.src.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.src.firebase.FirebaseCloudMessageService;

import java.io.IOException;

import static shop.hodl.kkonggi.utils.Time.getCurrentDateStr;

@RestController
@RequestMapping("/app/v1/push")
public class PushController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @GetMapping("")
    public BaseResponse<String> medicinePush(){
        try{
            FirebaseCloudMessageService fm = new FirebaseCloudMessageService();
            String token = "cZZLgiP3QdavHtdeUT2WPi:APA91bGZ84BQHEvQFOUSUycjVMg1RGhD2qkeODrJUF-sCdf_Jn7IhBihtlMHH3r4qY-1BssjPzkI7Ns0p0clIV3btwIfwWdJo_QZH6H7ssScaoYhnprYGqHiDDObApMsKWDJEt32kVPA";
            String title = "아침 물약";
            String body = "오늘 아침에 먹을 약은 몇 개가 있어요!";

            fm.sendMessageTo(token, title, body);
            logger.info(getCurrentDateStr() + " FCM 성공");

            return new BaseResponse<>(token);
        } catch (IOException ioException){
            logger.error(getCurrentDateStr() + " FCM 실패");
            ioException.printStackTrace();
            return new BaseResponse<>("실패");
        }
    }
}
