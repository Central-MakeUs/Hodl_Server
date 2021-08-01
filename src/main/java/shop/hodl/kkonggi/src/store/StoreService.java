package shop.hodl.kkonggi.src.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.utils.JwtService;

@Service
public class StoreService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreProvider storeProvider;
    private final StoreDao storeDao;
    private final JwtService jwtService;

    @Autowired
    public StoreService(StoreProvider storeProvider, StoreDao storeDao, JwtService jwtService){
        this.storeProvider = storeProvider;
        this.storeDao = storeDao;
        this.jwtService = jwtService;
    }
}
