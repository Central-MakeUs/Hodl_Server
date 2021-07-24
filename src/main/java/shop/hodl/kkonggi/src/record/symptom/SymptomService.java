package shop.hodl.kkonggi.src.record.symptom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.record.symptom.model.PostSymptomReq;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;

import static shop.hodl.kkonggi.utils.ValidationRegex.isRegexDate;
import static shop.hodl.kkonggi.utils.Time.getCurrentDateStr;
import static shop.hodl.kkonggi.utils.Chat.makeSymptoms;

@Service
public class SymptomService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SymptomDao symptomDao;
    private final SymptomProvider symptomProvider;
    private final JwtService jwtService;


    @Autowired
    public SymptomService(SymptomDao symptomDao, SymptomProvider symptomProvider, JwtService jwtService) {
        this.symptomDao = symptomDao;
        this.symptomProvider = symptomProvider;
        this.jwtService = jwtService;
    }

    @Transactional
    public GetChatRes createSymptomRecord(int userIdx, PostSymptomReq postReq) throws BaseException {
        String currentTimeStr =  getCurrentDateStr();
        if(postReq.getDate() == null || currentTimeStr.equals(postReq.getDate()) || postReq.getDate().isEmpty()) postReq.setDate(currentTimeStr);
        else if(!isRegexDate(postReq.getDate()) || postReq.getDate().length() != 8) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);
        try{
            // todo : date에 기록되어 있는 지 체크
            if(symptomProvider.checkSymptomOfDay(userIdx, postReq.getDate()) == 1){
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }
            // 없으면 새로 만들기
            int recordIdx = symptomDao.createSymptomRecord(userIdx, postReq.getDate(), makeSymptoms(postReq.getSymptomIdx()));
            GetChatRes getChatRes;
            if(recordIdx > 0) {
                String groupId = "SYM_REC_ASK_OK";
                int scenarioIdx = 4;
                getChatRes = symptomProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            }
            else{
                String groupId = "SAVE_FAIL";
                int scenarioIdx = 0;
                getChatRes = symptomProvider.getChats(userIdx, scenarioIdx, groupId);
                String actionType = "SYM_REC_POST_CHIP_GROUP";
                String[] actionId = {"SYM_REC_POST_DISCARD", "SYM_REC_POST_RETRY"};
                getChatRes.getAction().setActionType(actionType);
                for(int i = 0; i< getChatRes.getAction().getChoiceList().size(); i++){
                    if(getChatRes.getAction().getChoiceList().get(i).getActionId().equals("SAVE_FAIL_DISCARD"))
                        getChatRes.getAction().getChoiceList().get(i).setActionId(actionId[0]);
                    if(getChatRes.getAction().getChoiceList().get(i).getActionId().equals("SAVE_FAIL_RETRY"))
                        getChatRes.getAction().getChoiceList().get(i).setActionId(actionId[1]);
                }
            }
            return getChatRes;

        } catch (Exception e){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


    @Transactional
    public GetChatRes updateSymptomRecord(int userIdx, PostSymptomReq postReq) throws BaseException {
        String currentTimeStr =  getCurrentDateStr();
        if(postReq.getDate() == null || currentTimeStr.equals(postReq.getDate()) || postReq.getDate().isEmpty()) postReq.setDate(currentTimeStr);
        else if(!isRegexDate(postReq.getDate()) || postReq.getDate().length() != 8) throw new BaseException(BaseResponseStatus.POST_MEDICINE_INVALID_DAYS);

        try{
            // todo : date에 기록되어 있는 지 체크
            if(symptomProvider.checkSymptomOfDay(userIdx, postReq.getDate()) == 0){
                throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
            }
            int recordIdx = symptomProvider.getSymptomOfDay(userIdx, postReq.getDate());
            int checks = makeSymptoms(postReq.getSymptomIdx());
            int result = symptomDao.updateSymptomRecord(recordIdx, checks);
            GetChatRes getChatRes;
            if(result > 0) {
                String groupId = "SYM_REC_ASK_OK";
                int scenarioIdx = 4;
                getChatRes = symptomProvider.getChatsNoAction(userIdx, scenarioIdx, groupId);
            }
            else{
                String groupId = "SAVE_FAIL";
                int scenarioIdx = 0;
                getChatRes = symptomProvider.getChats(userIdx, scenarioIdx, groupId);
                String actionType = "SYM_REC_PATCH_CHIP_GROUP";
                String[] actionId = {"SYM_REC_PATCH_DISCARD", "SYM_REC_PATCHD_RETRY"};
                getChatRes.getAction().setActionType(actionType);
                for(int i = 0; i< getChatRes.getAction().getChoiceList().size(); i++){
                    if(getChatRes.getAction().getChoiceList().get(i).getActionId().equals("SAVE_FAIL_DISCARD"))
                        getChatRes.getAction().getChoiceList().get(i).setActionId(actionId[0]);
                    if(getChatRes.getAction().getChoiceList().get(i).getActionId().equals("SAVE_FAIL_RETRY"))
                        getChatRes.getAction().getChoiceList().get(i).setActionId(actionId[1]);
                }
            }
            return getChatRes;
        } catch (Exception e){
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

}
