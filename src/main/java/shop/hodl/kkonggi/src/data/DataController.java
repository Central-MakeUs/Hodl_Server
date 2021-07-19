package shop.hodl.kkonggi.src.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.src.data.model.GetBoardRes;
import shop.hodl.kkonggi.src.data.model.GetBoradContentRes;
import shop.hodl.kkonggi.utils.JwtService;

import java.util.List;

@RestController
@RequestMapping("/app/v1/data")
public class DataController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final DataProvider dataProvider;
    @Autowired
    private final DataService dataService;
    @Autowired
    private final JwtService jwtService;

    public DataController(DataProvider dataProvider, DataService dataService, JwtService jwtService) {
        this.dataProvider = dataProvider;
        this.dataService = dataService;
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
            List<GetBoardRes> getBoardRes = dataProvider.getBoardList();
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
            GetBoradContentRes getBoardRes = dataProvider.getBoardContent(noticeboardIdx);
            return new BaseResponse<>(getBoardRes);
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/version")
    public BaseResponse<String> getLatestSetting(){
        try{
            return new BaseResponse<>(dataProvider.getLatestSetting());
        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
