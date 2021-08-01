package shop.hodl.kkonggi.src.store;

import com.google.api.services.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponse;
import shop.hodl.kkonggi.src.store.model.GetStoreItemsRes;
import shop.hodl.kkonggi.src.store.model.GetStoreRes;
import shop.hodl.kkonggi.utils.JwtService;

import java.util.ArrayList;
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

    @ResponseBody
    @GetMapping("/items")
    public BaseResponse<List<GetStoreItemsRes>> getStoreItems(){
        try{
            int userIdx = jwtService.getUserIdx();

            List<GetStoreItemsRes.Item> realItem = new ArrayList<>();
            realItem.add(new GetStoreItemsRes.Item(
                    "https://firebasestorage.googleapis.com/v0/b/kkonggi-ca5b9.appspot.com/o/shop%2Fkkonggi%2Fimg_calcium.png?alt=media&token=1e0e32f5-fa41-4cdd-9d7b-a12dd387baff",
                    "https://smartstore.naver.com/coms_store/products/5584220406",
                    "우울증, 불안완화에 좋은 마그네슘 20매입", "바이오렉트라", "47900", "58%","20000"));
            realItem.add(new GetStoreItemsRes.Item(
                    "https://firebasestorage.googleapis.com/v0/b/kkonggi-ca5b9.appspot.com/o/shop%2Fkkonggi%2Fimg_magnesium.png?alt=media&token=1f120ab0-a775-4655-8a15-6a340666cbb2",
                    "https://smartstore.naver.com/coms_store/products/5584233151",
                    "우울증, 조현증에 좋은 아연 셀레늄 20매입 ", "바이오렉트라", "90000", "33%", "60000"));
            realItem.add(new GetStoreItemsRes.Item(
                    "https://firebasestorage.googleapis.com/v0/b/kkonggi-ca5b9.appspot.com/o/shop%2Fkkonggi%2Fimg_zincselenium.png?alt=media&token=b1ecfc14-8763-451b-aa90-9073a2e7694f",
                    "https://smartstore.naver.com/coms_store/products/5584225694",
                    "불면증, 노인우울증에 좋은 칼슘 20매입", "바이오렉트라", "49000", "38%","30000"));
            List<GetStoreItemsRes> getStoreItemsRes = new ArrayList<>();
            getStoreItemsRes.add(new GetStoreItemsRes("꽁기스토어", realItem));
            return new BaseResponse<>(getStoreItemsRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
