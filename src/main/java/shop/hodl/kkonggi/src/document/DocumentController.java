package shop.hodl.kkonggi.src.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.src.document.model.GetBoardRes;
import shop.hodl.kkonggi.src.document.model.GetBoradContentRes;
import shop.hodl.kkonggi.utils.JwtService;

import java.util.List;

@RestController
@RequestMapping("/app/v1/document")
public class DocumentController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final DocumentProvider documentProvider;
    @Autowired
    private final DocumentService documentService;
    @Autowired
    private final JwtService jwtService;

    public DocumentController(DocumentProvider documentProvider, DocumentService documentService, JwtService jwtService) {
        this.documentProvider = documentProvider;
        this.documentService = documentService;
        this.jwtService = jwtService;
    }

    /**
     * 공지 사항 리스트
     * @return
     */
    @ResponseBody
    @GetMapping("/noticeboard")
    public BaseResponse<List<GetBoardRes>> getBoardList(){
        try{
            List<GetBoardRes> getBoardRes = documentProvider.getBoardList();
            return new BaseResponse<>(getBoardRes);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 공지사항
     * @param noticeboardIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/noticeboard/{noticeboardIdx}")
    public BaseResponse<GetBoradContentRes> getBoardContent(@PathVariable("noticeboardIdx") int noticeboardIdx){
        try{
            if(noticeboardIdx == 0) ;
            GetBoradContentRes getBoardRes = documentProvider.getBoardContent(noticeboardIdx);
            return new BaseResponse<>(getBoardRes);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 이용 약관
     */


    /**
     * 개인 정보
     */
}
