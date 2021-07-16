package shop.hodl.kkonggi.record.medicine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.record.medicine.model.GetMedicineListRes;
import shop.hodl.kkonggi.src.medicine.model.GetMedChatRes;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RecordMedicineDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetMedicineListRes getTodayMedicineList(int userIdx, String timeSlot, String defaultTime) {
        String getMedicineQuery = "select medicineIdx, medicineRealName,\n" +
                "       case\n" +
                "           when MedicineInfo.status is null then concat(MedicineInfo.timeTime, ' 미복용')\n" +
                "           when MedicineInfo.status = 'N' then '안먹음'\n" +
                "               else MedicineInfo.recordTime\n" +
                "       end as taking, status from\n" +
                "       (select Medicine.medicineIdx, medicineRealName ,case\n" +
                "           when MedicineTime.time is null then DATE_FORMAT(TIME(?),'%h:%i %p')\n" +
                "           else (DATE_FORMAT(MedicineTime.time,'%h:%i %p'))\n" +
                "           end  as timeTime,\n" +
                "               (DATE_FORMAT(MedicineRecord.time,'%h:%i %p')) as recordTime, MedicineRecord.status as status from Medicine\n" +
                "    inner join MedicineTime on Medicine.medicineIdx = MedicineTime.medicineIdx and slot = ?\n" +
                "    left join MedicineRecord on MedicineTime.slot = MedicineRecord.slot and MedicineTime.medicineIdx = MedicineRecord.medicineIdx\n" +
                "where userIdx = ? and Medicine.status = 'Y' and MedicineTime.status = 'Y' and pow(2, weekday(now())) & days != 0 and datediff(endDay, now()) > -1) MedicineInfo";
        Object[] getMedicineParams = new Object[]{defaultTime, timeSlot, userIdx};

        return new GetMedicineListRes(timeSlot,
                this.jdbcTemplate.query(getMedicineQuery,
                        (rs, rowNum) -> new GetMedicineListRes.Medicine(
                                rs.getInt("medicineIdx"),
                                rs.getString("medicineRealName"),
                                rs.getString("taking"),
                                rs.getString("status")
                        ), getMedicineParams)
        );
    }

    public int checkUserMedicine(int userIdx){
        String checkMedicineQuery = "select exists(select userIdx from Medicine where userIdx= ? and status = 'Y')";
        return this.jdbcTemplate.queryForObject(checkMedicineQuery, int.class, userIdx);
    }

    public int checkTodayMedicine(int userIdx){
        String checkMedicineQuery = "select exists(select medicineIdx from Medicine where userIdx = ? and pow(2, weekday(now())) & days != 0 and datediff(endDay, now()) > -1 and status = 'Y')";
        return this.jdbcTemplate.queryForObject(checkMedicineQuery, int.class, userIdx);
    }

    public String getUserNickName(int userIdx){
        String getNickNameQuery = "select ifnull(nickName, \"\") as nickName from User where userIdx = ? and status = 'Y'";
        return this.jdbcTemplate.queryForObject(getNickNameQuery, String.class, userIdx);
    }

    public List<String> getTimeSlot(int userIdx){
        String getSlotsQuery = "select distinct\n" +
                "        case\n" +
                "            when slot = 'D' then '새벽'\n" +
                "            when slot = 'M' then '아침'\n" +
                "            when slot = 'L' then '점심'\n" +
                "            when slot = 'E' then '저녁'\n" +
                "            when slot = 'N' then '자기전'\n" +
                "            end as slot\n" +
                "                 from MedicineTime\n" +
                "    inner join Medicine\n" +
                "        where Medicine.medicineIdx = MedicineTime.medicineIdx and MedicineTime.status = 'Y' and Medicine.status = 'Y'\n" +
                "          and pow(2, weekday(now())) & days != 0 and datediff(endDay, now()) > -1 and userIdx = ?";

        List<String> slots = this.jdbcTemplate.queryForList(
                getSlotsQuery,
                String.class,
                userIdx
        );
        return slots;
    }

    public int getTimeSlotCnt(int userIdx){
        String getSlotCntQuery = "select count(slot) from (select distinct\n" +
                "    case\n" +
                "            when slot = 'D' then '새벽'\n" +
                "            when slot = 'M' then '아침'\n" +
                "            when slot = 'L' then '점심'\n" +
                "            when slot = 'E' then '저녁'\n" +
                "            when slot = 'N' then '자기전'\n" +
                "            end as slot\n" +
                "                 from MedicineTime\n" +
                "    inner join Medicine\n" +
                "        where Medicine.medicineIdx = MedicineTime.medicineIdx and MedicineTime.status = 'Y' and Medicine.status = 'Y'\n" +
                "          and pow(2, weekday(now())) & days != 0 and datediff(endDay, now()) > -1 and userIdx = ?) slot";

        return this.jdbcTemplate.queryForObject(getSlotCntQuery, int.class, userIdx);
    }

    public int getMedicineType(int userIdx){
        String getMedicineTypeQuery = "select count(medicineIdx) from (select medicineIdx from Medicine where Medicine.status = 'Y'\n" +
                "          and pow(2, weekday(now())) & days != 0 and datediff(endDay, now()) > -1 and userIdx = ?) Medicines";
        return this.jdbcTemplate.queryForObject(getMedicineTypeQuery, int.class, userIdx);
    }


    public GetMedChatRes getChats(String groupId, int scenarioIdx){

        String getChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and scenarioIdx = ?";
        String getActionQuery = "select distinct actionType from Action where groupId = ? and status = 'Y' and scenarioIdx =?";
        String getActionContentQuery = "select content, actionId from Action where groupId = ? and status = 'Y' and scenarioIdx =?";

        return new GetMedChatRes(this.jdbcTemplate.query(getChatQuery,
                (rs, rowNum)-> new GetMedChatRes.Chat(
                        rs.getString("chatType"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("content")
                ), groupId, scenarioIdx),

                this.jdbcTemplate.queryForObject(getActionQuery,
                        (rs, rowNum)-> new GetMedChatRes.Action(
                                rs.getString("actionType"),
                                this.jdbcTemplate.query(getActionContentQuery,
                                        (rk, rkNum)-> new GetMedChatRes.Action.Choice(
                                                rk.getString("actionId"),
                                                rk.getString("content")
                                        ), groupId, scenarioIdx)
                        ), groupId, scenarioIdx)
        );
    }

    public GetMedChatRes getChatsNoAction(String groupId, int scenarioIdx){
        String getChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and scenarioIdx = ?";

        return new GetMedChatRes(this.jdbcTemplate.query(getChatQuery,
                (rs, rowNum)-> new GetMedChatRes.Chat(
                        rs.getString("chatType"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("content")
                ), groupId, scenarioIdx),
                null
        );
    }
}
