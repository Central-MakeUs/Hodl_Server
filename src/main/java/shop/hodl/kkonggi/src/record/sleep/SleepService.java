package shop.hodl.kkonggi.src.record.sleep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.utils.JwtService;

@Service
public class SleepService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SleepDao sleepDao;
    private final SleepProvider sleepProvider;
    private final JwtService jwtService;


    @Autowired
    public SleepService(SleepDao sleepDao, SleepProvider sleepProvider, JwtService jwtService) {
        this.sleepDao = sleepDao;
        this.sleepProvider = sleepProvider;
        this.jwtService = jwtService;
    }
}
