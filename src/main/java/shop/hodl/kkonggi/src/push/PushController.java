package shop.hodl.kkonggi.src.push;


import com.fasterxml.jackson.databind.ObjectMapper;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.src.firebase.FirebaseCloudMessageService;

import java.io.IOException;

public class PushController {

    public void medicinePush(int userIdx) throws BaseException {
        try{
            FirebaseCloudMessageService fm = new FirebaseCloudMessageService();
            String token = "";
            String title = "아침 물약";
            String body = "오늘 아침에 먹을 약물은 몇 개가 있어요!";

            fm.sendMessageTo( token, title, body);
        } catch (IOException ioException){
            ioException.printStackTrace();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
