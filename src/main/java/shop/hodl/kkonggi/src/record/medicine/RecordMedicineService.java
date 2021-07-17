package shop.hodl.kkonggi.src.record.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.medicine.model.PostAllMedicineRecordReq;
import shop.hodl.kkonggi.src.record.medicine.model.PostAllMedicineRecordRes;
import shop.hodl.kkonggi.src.record.medicine.model.PostMedicineRecordReq;
import shop.hodl.kkonggi.src.record.medicine.model.PostMedicineRecordRes;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        try{
            for(int i = 0; i < postReq.getMedicineIdx().length; i++) {
                amount = recordMedicineProvider.getLatestMedicineAmount(postReq.getMedicineIdx()[i], timeSlot);  // 가장  최근 복용량
                result = recordMedicineDao.createAllMedicineRecord(postReq, i, amount);
                if(result == 0) throw new BaseException(BaseResponseStatus.CREATE_FAIL_MEDICINE_RECORD);
            }
            return new PostAllMedicineRecordRes(postReq.getDate());
        } catch (Exception exception) {
            logger.error("userIdx = " + userIdx);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }

    }

    @Transactional
    public PostMedicineRecordRes createMedicineRecord(int userIdx , PostMedicineRecordReq postReq, int medicineIdx, String timeSlot) throws BaseException{
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        Date current = new Date();
        String currentTimeStr = dtFormat.format(current);

        int result = 0;
        if (postReq.getDate() == null || postReq.getDate().equals(currentTimeStr) || postReq.getDate().isEmpty())  // 오늘일 경우, Date 설정
            postReq.setDate(currentTimeStr);
        if(!isRegexDate(postReq.getDate()))
            throw  new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_DATE);
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

}
