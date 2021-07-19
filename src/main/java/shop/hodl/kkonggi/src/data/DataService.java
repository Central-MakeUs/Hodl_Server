package shop.hodl.kkonggi.src.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.utils.JwtService;

@Service
public class DataService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DataProvider dataProvider;
    private final DataDao dataDao;
    private final JwtService jwtService;

    @Autowired
    public DataService(DataProvider dataProvider, DataDao dataDao, JwtService jwtService) {
        this.dataProvider = dataProvider;
        this.dataDao = dataDao;
        this.jwtService = jwtService;
    }
}
