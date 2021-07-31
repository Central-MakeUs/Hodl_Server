package shop.hodl.kkonggi.src.record.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.medicine.model.*;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

import static shop.hodl.kkonggi.config.BaseResponseStatus.POST_MEDICINE_RECORD_ALREADY;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;

@Service
public class RecordMedicineService {
    private final JwtService jwtService;
    private final RecordMedicineProvider recordMedicineProvider;
    private final RecordMedicineDao recordMedicineDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RecordMedicineService(JwtService jwtService, RecordMedicineProvider recordMedicineProvider, RecordMedicineDao recordMedicineDao) {
        this.jwtService = jwtService;
        this.recordMedicineProvider = recordMedicineProvider;
        this.recordMedicineDao = recordMedicineDao;
    }

    @Transactional
    public PostAllMedicineRecordRes createAllMedicineRecord(int userIdx, PostAllMedicineRecordReq postReq) throws BaseException {
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        Date current = new Date();
        String currentTimeStr = dtFormat.format(current);

        double amount = 1;
        int result = 0;
        String timeSlot = postReq.getTimeSlot();
        if (postReq.getDate() == null || postReq.getDate().equals(currentTimeStr) || postReq.getDate().isEmpty())  // 오늘일 경우, Date 설정
            postReq.setDate(currentTimeStr);
        if(!isRegexDate(postReq.getDate()))
            throw  new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_DATE);

        // 들어온 모든 약물들이 새로 등록되어야 할 약물들인 지 확인
        for(int i = 0; i <postReq.getMedicineIdx().length; i++){
            int isRec = recordMedicineProvider.checkRecordIdx(postReq.getMedicineIdx()[i], postReq.getTimeSlot(), postReq.getDate());
            logger.info(postReq.getMedicineIdx()[i] + "가 이미 투약한 약물입니다.");
            if(isRec == 1) throw new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_ALREADY);
        }

        try{
            for(int i = 0; i < postReq.getMedicineIdx().length; i++) {
                amount = recordMedicineProvider.getLatestMedicineAmount(postReq.getMedicineIdx()[i], timeSlot);  // 가장  최근 복용량
                result = recordMedicineDao.createAllMedicineRecord(postReq, i, amount);
                if(result == 0) throw new BaseException(BaseResponseStatus.CREATE_FAIL_MEDICINE_RECORD);
            }
            return new PostAllMedicineRecordRes(postReq.getDate());
        } catch (Exception exception) {
            logger.error(currentTimeStr + "userIdx = " + userIdx + " fail to createAllMedicineRecord");
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public PostAllMedicineRecordRes updateAllMedicineRecord(int userIdx, PostAllMedicineRecordReq patchReq) throws BaseException {
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        Date current = new Date();
        String currentTimeStr = dtFormat.format(current);

        double amount = 1;
        int result = 0;
        int recordIdx = 0;
        String timeSlot = patchReq.getTimeSlot();
        if (patchReq.getDate() == null || patchReq.getDate().equals(currentTimeStr) || patchReq.getDate().isEmpty())  // 오늘일 경우, Date 설정
            patchReq.setDate(currentTimeStr);
        if(!isRegexDate(patchReq.getDate()))
            throw  new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_DATE);

        // 들어온 모든 약물들이 수정되어야 할 약물들인 지 확인
        for(int i = 0; i <patchReq.getMedicineIdx().length; i++){
            int isRec = recordMedicineProvider.checkRecordIdx(patchReq.getMedicineIdx()[i], patchReq.getTimeSlot(), patchReq.getDate());
            if(isRec == 0) throw new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_ALREADY);
        }
        try{
            for(int i = 0; i < patchReq.getMedicineIdx().length; i++) {
                if(recordMedicineProvider.checkRecordIdx(patchReq.getMedicineIdx()[i], patchReq.getTimeSlot(), patchReq.getDate()) == 0)  // 레코드 된 것이 없는 경우
                    throw new BaseException(BaseResponseStatus.PATCH_MEDICINE_RECORD_INVALID);

                recordIdx = recordMedicineProvider.getRecordIdx(patchReq.getMedicineIdx()[i], patchReq.getTimeSlot(), patchReq.getDate());
                amount = recordMedicineProvider.getLatestMedicineAmount(patchReq.getMedicineIdx()[i], timeSlot);  // 가장  최근 복용량
                result = recordMedicineDao.updateAllMedicineRecord(patchReq, i, amount, recordIdx);
            }
            return new PostAllMedicineRecordRes(patchReq.getDate());
        } catch (Exception exception) {
            logger.error(currentTimeStr + "userIdx = " + userIdx + " fail to updateAllMedicineRecord");
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public PostMedicineRecordRes createMedicineRecord(int userIdx , PostMedicineRecordReq postReq, int medicineIdx, String timeSlot) throws BaseException{
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        Date current = new Date();
        String currentTimeStr = dtFormat.format(current);

        if(postReq.getStatus().equals("N")) postReq.setAmount(0);

        int result = 0;
        if (postReq.getDate() == null || postReq.getDate().equals(currentTimeStr) || postReq.getDate().isEmpty())  // 오늘일 경우, Date 설정
            postReq.setDate(currentTimeStr);
        if(!isRegexDate(postReq.getDate()))
            throw  new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_DATE);

        // 이미 등록된 것인지 확인!
        if(recordMedicineProvider.checkRecordIdx(medicineIdx, timeSlot, postReq.getDate()) == 1)
            throw new BaseException(POST_MEDICINE_RECORD_ALREADY);

        try {
            result = recordMedicineDao.createMedicineRecord(postReq, medicineIdx, timeSlot);
            if(result == 0) throw
                    new BaseException(BaseResponseStatus.CREATE_FAIL_MEDICINE_RECORD);

            PostMedicineRecordRes postMedicineRecordRes = new PostMedicineRecordRes(result);
            return postMedicineRecordRes;

        } catch (Exception exception) {
            logger.error("userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public int updateMedicineRecord(int userIdx , PatchMedicineRecordReq patchReq, int medicineIdx, String timeSlot) throws BaseException{
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        Date current = new Date();
        String currentTimeStr = dtFormat.format(current);

        if(patchReq.getStatus().equals("N")) patchReq.setAmount(0);

        int result = 0;
        if (patchReq.getDate() == null || patchReq.getDate().equals(currentTimeStr) || patchReq.getDate().isEmpty())  // 오늘일 경우, Date 설정
            patchReq.setDate(currentTimeStr);
        if(!isRegexDate(patchReq.getDate()))
            throw  new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_DATE);
        try{
            // recordIdx
            if(recordMedicineProvider.checkRecordIdx(patchReq, medicineIdx, timeSlot) == 0)  // 레코드 된 것이 없는 경우
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            int recordIdx = recordMedicineProvider.getRecordIdx(patchReq, medicineIdx, timeSlot);
            result = recordMedicineDao.updateMedicineRecord(recordIdx, patchReq);
            return result;

        } catch (Exception exception) {
            logger.error("userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}
