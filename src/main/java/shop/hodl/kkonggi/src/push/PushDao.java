package shop.hodl.kkonggi.src.push;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.src.push.model.GetMedicineNotification;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PushDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetMedicineNotification> getMedicineNotificationInfo(){
        String getQuery = "select deviceToken, nickName, Medicine.medicineIdx, medicineRealName,\n" +
                "       case\n" +
                "           when timeSlot = 'D' then '새벽'\n" +
                "           when timeSlot = 'M' then '아침'\n" +
                "           when timeSlot = 'L' then '점심'\n" +
                "           when timeSlot = 'E' then '저녁'\n" +
                "           when timeSlot = 'N' then '자기 전'\n" +
                "           end as\n" +
                "       timeSlot, count(Medicine.medicineIdx) as medicineCnt  from MedicineNotification\n" +
                "    inner join UserToken on UserToken.userIdx = MedicineNotification.userIdx and UserToken.status = 'Y'\n" +
                "    inner join User on User.userIdx = UserToken.userIdx and User.status = 'Y'\n" +
                "    inner join Medicine\n" +
                "        on User.userIdx = Medicine.userIdx and Medicine.status = 'Y' and pow(2, weekday(DATE(now()))) & days != 0 and (datediff(DATE(now()), startDay) > -1) and if(endDay is null, TRUE, datediff(endDay, DATE(now())) > -1)\n" +
                "    inner join MedicineTime on MedicineTime.medicineIdx = Medicine.medicineIdx and slot = timeSlot and MedicineTime.status = 'Y'\n" +
                "    inner join Notification on Notification.userIdx = User.userIdx and medicinePush = 'Y' and Notification.status = 'Y'\n" +
                "    where MedicineNotification.status = 'A' and TIMESTAMPDIFF(MINUTE , now(), MedicineNotification.notificationTime) = 0 group by timeSlot";
        return this.jdbcTemplate.query(getQuery,
                (rs,rowNum) -> new GetMedicineNotification(
                        rs.getString("deviceToken"),
                        rs.getString("nickName"),
                        rs.getString("timeSlot"),
                        rs.getInt("medicineCnt")
                ));
    }
}
