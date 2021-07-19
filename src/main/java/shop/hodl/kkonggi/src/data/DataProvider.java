package shop.hodl.kkonggi.src.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.data.model.GetBoardRes;
import shop.hodl.kkonggi.src.data.model.GetBoradContentRes;
import shop.hodl.kkonggi.utils.JwtService;

import java.util.List;

@Service
public class DataProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataDao dataDao;
    private final JwtService jwtService;

    @Autowired
    public DataProvider(DataDao dataDao, JwtService jwtService) {
        this.dataDao = dataDao;
        this.jwtService = jwtService;
    }

    public List<GetBoardRes> getBoardList() throws BaseException{
        try{
             return dataDao.getBoardList();
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetBoradContentRes getBoardContent(int noticeboardIdx) throws BaseException{

        if(checkBoard(noticeboardIdx) == 0) throw new BaseException(BaseResponseStatus.INVALID_NOTICE_BOARD);
        try {
            return dataDao.getBoradContent(noticeboardIdx);
        }
        catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public int checkBoard(int noticeboardIdx) throws BaseException{
        try{
            return dataDao.checkBoard(noticeboardIdx);
        }catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public String getLatestSetting() throws BaseException{
        try{
            return dataDao.getLatestSetting();
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
