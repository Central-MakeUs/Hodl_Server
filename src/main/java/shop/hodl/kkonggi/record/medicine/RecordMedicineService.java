package shop.hodl.kkonggi.record.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.utils.JwtService;

@Service
public class RecordMedicineService {
    private final JwtService jwtService;
    private final RecordMedicineProvider recordMedicineProvider;
    private final RecordMedicineDao recordMedicineDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RecordMedicineService(JwtService jwtService, RecordMedicineProvider recordMedicineProvider, RecordMedicineDao recordMedicineDao) {
        this.jwtService = jwtService;
        this.recordMedicineProvider = recordMedicineProvider;
        this.recordMedicineDao = recordMedicineDao;
    }
}
