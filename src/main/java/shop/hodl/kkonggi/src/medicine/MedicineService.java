package shop.hodl.kkonggi.src.medicine;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.hodl.kkonggi.config.BaseException;
import shop.hodl.kkonggi.config.BaseResponseStatus;
import shop.hodl.kkonggi.src.medicine.model.PostMedicineReq;
import shop.hodl.kkonggi.src.user.model.GetChatRes;
import shop.hodl.kkonggi.utils.JwtService;

import javax.transaction.Transactional;

@Service
public class MedicineService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MedicineProvider medicineProvider;
    private final MedicineDao medicineDao;
    private final JwtService jwtService;

    @Autowired
    public MedicineService(MedicineProvider medicineProvider, MedicineDao medicineDao, JwtService jwtService){
        this.medicineProvider = medicineProvider;
        this.medicineDao = medicineDao;
        this.jwtService = jwtService;
    }

    /**
    @Transactional
    public GetChatRes createMedicine(int userIdx, PostMedicineReq postMedicineReq) throws BaseException {
        try{

        } catch (Exception exception) {
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
    */

}
