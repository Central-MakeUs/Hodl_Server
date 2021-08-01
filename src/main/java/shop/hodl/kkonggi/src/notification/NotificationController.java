package shop.hodl.kkonggi.src.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.notification.model.*;
import shop.hodl.kkonggi.utils.JwtService;

import java.util.List;

import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexTime;

@RestController
@RequestMapping("/app/v1/users/notification")
public class NotificationController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final NotificationProvider notificationProvider;
    @Autowired
    private final NotificationService notificationService;
    @Autowired
    private final JwtService jwtService;

    public NotificationController(NotificationProvider notificationProvider, NotificationService notificationService, JwtService jwtService){
        this.notificationProvider = notificationProvider;
        this.notificationService = notificationService;
        this.jwtService = jwtService;
    }

    /**
     * 알림 관리 get
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetNotificationRes> getNotification() {
        try{
            int userIdx = jwtService.getUserIdx();
            GetNotificationRes getNotificationRes = notificationProvider.getNotification(userIdx);
            return new BaseResponse<>(getNotificationRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 알림 관리 수정
     */
    @ResponseBody
    @PatchMapping("")
    public BaseResponse<Integer> updateNotification(@RequestBody PatchNotificationReqForToken getNotificationRes) {
        try{
            int userIdx = jwtService.getUserIdx();
            if(getNotificationRes.getDeviceToken() == null)
                return new BaseResponse<>(BaseResponseStatus.FCM_EMPTY_DEVICE_TOKEN);   // 기기 토큰을 입력해주세요.

            Integer result = notificationService.updateNotification(userIdx, getNotificationRes);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 복약 알림 관리
     */
    @ResponseBody
    @GetMapping("/medicine")
    public BaseResponse<GetMedicineNotificationRes> getMedicineNotification(){
        try{
            int userIdx = jwtService.getUserIdx();
            GetMedicineNotificationRes getMedicineNotificationRes = notificationProvider.getMedicineNotification(userIdx);
            return new BaseResponse<>(getMedicineNotificationRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 복약 알림 설정
    */
    @ResponseBody
    @PatchMapping("/medicine")
    public BaseResponse<Integer> updateMedicineNotification(@RequestBody List<PatchMedicineNotificationReq> patchReq){
        try{
            int userIdx = jwtService.getUserIdx();
            for (PatchMedicineNotificationReq patchMedicineNotificationReq : patchReq) {
                if (!patchMedicineNotificationReq.getTimeSlot().equals("D") && !patchMedicineNotificationReq.getTimeSlot().equals("M") && !patchMedicineNotificationReq.getTimeSlot().equals("L") && !patchMedicineNotificationReq.getTimeSlot().equals("E") && !patchMedicineNotificationReq.getTimeSlot().equals("N"))
                    throw new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_SLOT);  // 시간대 형식 확인
                else if (!patchMedicineNotificationReq.getStatus().equals("A") && !patchMedicineNotificationReq.getStatus().equals("I"))
                    throw new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_STATUS);    // 상태 형식 확인
                else if (!isRegexTime(patchMedicineNotificationReq.getNotificationTime()))
                    throw new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_TIME);  // 시간 형식 확인
            }
            Integer result = notificationService.updateMedicineNotification(userIdx, patchReq);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 기기 토큰 등록
     */
    @ResponseBody
    @PatchMapping("/token")
    public BaseResponse<Integer> deviceToken(@RequestBody PatchTokenReq patchTokenReq){
        try{
            if(patchTokenReq.getDeviceToken() == null) throw new BaseException(BaseResponseStatus.FCM_EMPTY_DEVICE_TOKEN);
            int userIdx = jwtService.getUserIdx();
            int result = notificationService.deviceToken(userIdx, patchTokenReq);
            return new BaseResponse<>(userIdx);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
