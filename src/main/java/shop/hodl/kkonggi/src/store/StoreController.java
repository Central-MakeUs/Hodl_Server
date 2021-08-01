package shop.hodl.kkonggi.src.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.src.store.model.GetStoreRes;
import shop.hodl.kkonggi.utils.JwtService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/app/v1/store")
public class StoreController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final StoreProvider storeProvider;
    @Autowired
    private final StoreService storeService;
    @Autowired
    private final JwtService jwtService;

    public StoreController(StoreProvider storeProvider, StoreService storeService, JwtService jwtService){
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetStoreRes>> getStore(){
        try{
            int userIdx = jwtService.getUserIdx();
            String bannerImgUrl1 = "https://firebasestorage.googleapis.com/v0/b/kkonggi-ca5b9.appspot.com/o/banner%2Fkkonggi_store_banner_1.png?alt=media&token=97d75acf-9f7b-4acb-9ce7-105dc489595c";
            String bannerImgUrl2 = "https://firebasestorage.googleapis.com/v0/b/kkonggi-ca5b9.appspot.com/o/banner%2Fkkonggi_store_banner_2.png?alt=media&token=1e196838-7e64-419f-8654-ec2ffb3e59d2";
            List<GetStoreRes> getStoreRes = Arrays.asList(new GetStoreRes(bannerImgUrl1,""), new GetStoreRes(bannerImgUrl2, ""));
            return new BaseResponse<>(getStoreRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
