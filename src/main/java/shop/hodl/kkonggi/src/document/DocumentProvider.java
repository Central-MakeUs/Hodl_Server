package shop.hodl.kkonggi.src.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.document.model.GetBoardRes;
import shop.hodl.kkonggi.src.document.model.GetBoradContentRes;
import shop.hodl.kkonggi.utils.JwtService;

import java.util.List;

@Service
public class DocumentProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DocumentDao documentDao;
    private final JwtService jwtService;

    @Autowired
    public DocumentProvider(DocumentDao documentDao, JwtService jwtService) {
        this.documentDao = documentDao;
        this.jwtService = jwtService;
    }

    public List<GetBoardRes> getBoardList() throws BaseException{
        try{
             return documentDao.getBoardList();
        } catch (Exception exception){
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public GetBoradContentRes getBoardContent(int noticeboardIdx) throws BaseException{
        try {
            return documentDao.getBoradContent(noticeboardIdx);
        }
        catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


}
