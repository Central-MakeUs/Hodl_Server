package shop.hodl.kkonggi.src.record.exercise;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.exercise.model.PostExerciseReq;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;

import static shop.hodl.kkonggi.utils.Time.getCurrentDateStr;
import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;

@Repository
public class ExerciseService {
    private final ExerciseDao exerciseDao;
    private final ExerciseProvider exerciseProvider;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ExerciseService(ExerciseDao exerciseDao, ExerciseProvider exerciseProvider, JwtService jwtService) {
        this.exerciseDao = exerciseDao;
        this.exerciseProvider = exerciseProvider;
        this.jwtService = jwtService;
    }

    @Transactional
    public GetChatRes createExerciseRecord(int userIdx, PostExerciseReq postReq) throws BaseException {
        if(postReq.getDate() == null || getCurrentDateStr().equals(postReq.getDate()) || postReq.getDate().isEmpty()) postReq.setDate(getCurrentDateStr());
        else if(!isRegexDate(postReq.getDate()) || postReq.getDate().length() != 8)
            throw new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_DATE);
        if(exerciseProvider.checkExerciseRecord(userIdx, postReq.getDate()) == 1){
            throw new BaseException(BaseResponseStatus.POST_EXERCISE_RECORD_ALREADY);
        }

        try{
            String groupId = "EXE_REC_COM";
            int scenarioIdx = 7;
            GetChatRes getChatRes;
            if (postReq.getIsExercise() == 0) {
                postReq.setTotalTime("00:00");
                groupId = "EXE_REC_NO";
            }
            int result = exerciseDao.createExerciseRecord(userIdx, postReq);
            if(result < 0){
                groupId = "SAVE_FAIL";
                scenarioIdx = 0;
            }
            getChatRes = exerciseProvider.getChats(userIdx, scenarioIdx, groupId);
            return getChatRes;
        } catch (Exception exception){
            logger.error(getCurrentDateStr() + " userIdx = " + userIdx + "post exercise fail");
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    @Transactional
    public GetChatRes updateExerciseRecord(int userIdx, PostExerciseReq postReq) throws BaseException {
        if(postReq.getDate() == null || getCurrentDateStr().equals(postReq.getDate()) || postReq.getDate().isEmpty()) postReq.setDate(getCurrentDateStr());
        else if(!isRegexDate(postReq.getDate()) || postReq.getDate().length() != 8)
            throw new BaseException(BaseResponseStatus.POST_MEDICINE_RECORD_ALL_INVALID_DATE);
        if(exerciseProvider.checkExerciseRecord(userIdx, postReq.getDate()) == 0){
            throw new BaseException(BaseResponseStatus.POST_EXERCISE_RECORD_EMPTY);
        }

        try{
            int recordIdx = exerciseProvider.getExerciseRecord(userIdx, postReq.getDate());
            String groupId = "EXE_REC_COM";
            int scenarioIdx = 7;
            GetChatRes getChatRes;
            if (postReq.getIsExercise() == 0) {
                postReq.setTotalTime("00:00");
                groupId = "EXE_REC_NO";
            }
            int result = exerciseDao.updateExerciseRecord(recordIdx, postReq);
            if(result < 0){
                groupId = "SAVE_FAIL";
                scenarioIdx = 0;
            }
            getChatRes = exerciseProvider.getChats(userIdx, scenarioIdx, groupId);
            return getChatRes;
        } catch (Exception exception){
            logger.error( getCurrentDateStr() + " userIdx = " + userIdx + "patch exercise fail");
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
