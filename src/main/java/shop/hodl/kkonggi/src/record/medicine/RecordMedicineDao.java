package shop.hodl.kkonggi.src.record.medicine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.src.record.medicine.model.GetMedicine;
import shop.hodl.kkonggi.src.record.medicine.model.GetMedicineListRes;
import shop.hodl.kkonggi.src.record.medicine.model.PostAllMedicineRecordReq;
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

    public GetMedicineListRes getTodayMedicineList(int userIdx, String timeSlot, String defaultTime, String date) {
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
                "    left join MedicineRecord on MedicineTime.slot = MedicineRecord.slot and MedicineTime.medicineIdx = MedicineRecord.medicineIdx and MedicineRecord.day = ?\n" +
                "where userIdx = ? and Medicine.status = 'Y' and MedicineTime.status = 'Y' and pow(2, weekday(DATE(?))) & days != 0 and datediff(endDay, DATE(?)) > -1) MedicineInfo";
        Object[] getMedicineParams = new Object[]{defaultTime, timeSlot, date, userIdx, date, date};

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

    public int createAllMedicineRecord(PostAllMedicineRecordReq postReq, int index, double amount){
        String createMedicieRecordQuery = "insert into MedicineRecord (medicineIdx, slot, day, time, amount) values (?, ?, ?, ?, ?)";
        Object[] createMedicieRecordParmas = new Object[]{postReq.getMedicineIdx()[index], postReq.getTimeSlot(), postReq.getDate(), postReq.getTime(), amount};

        this.jdbcTemplate.update(createMedicieRecordQuery, createMedicieRecordParmas);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checMedicineRecordAmount(int medicineIdx, String timeSlot){
        String checkMedicineRecordQuery = "select exists(select amount from MedicineRecord where medicineIdx = ? and slot = ? and status = 'Y')";
        return this.jdbcTemplate.queryForObject(checkMedicineRecordQuery, int.class, medicineIdx, timeSlot);
    }

    public double getLatestMedicineAmount(int medicineIdx, String timeSlot){
        String getAmountQuery = "select amount from MedicineRecord where medicineIdx = ? and slot = ? and status = 'Y' order by createAt desc limit 1";
        return this.jdbcTemplate.queryForObject(getAmountQuery, double.class, medicineIdx, timeSlot);
    }

    public int checkSpecificMedicineRecord(int medicineIdx, String timeSlot, String day){
        String getCheckQuery = "select exists(select medicineIdx from MedicineRecord where medicineIdx = ? and slot = ? and day = ? and status = 'Y')";
        Object[] getCheckParams = new Object[]{medicineIdx, timeSlot, day};
        return this.jdbcTemplate.queryForObject(getCheckQuery, int.class, getCheckParams);
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

    public GetMedicine getSpecificMedicineRecordModify(int medicineIdx, String timeSlot, String date){
        String getMedicineQuery = "select MedicineRecord.medicineIdx, medicineRealName, day, time, amount, ifnull(memo, \"\") as memo, days  from MedicineRecord\n" +
                "    inner join Medicine on Medicine.medicineIdx = MedicineRecord.medicineIdx\n" +
                "where Medicine.medicineIdx = ? and slot = ? and day = ? and MedicineRecord.status = 'Y'";
        Object[] getMedicineParams = new Object[]{medicineIdx, timeSlot, date};

        return this.jdbcTemplate.queryForObject(getMedicineQuery,
                (rs, rowNum) -> new GetMedicine (
                        rs.getInt("medicineIdx"),
                        rs.getString("medicineRealName"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getDouble("amount"),
                        rs.getString("memo"),
                        rs.getInt("days")
                ), getMedicineParams);
    }

    public GetMedicine getSpecificMedicineRecord(int medicineIdx, String timeSlot){
        String getMedicineQuery = "select Medicine.medicineIdx, medicineRealName, now() as day, time, (select lastAmount from (select distinct medicineIdx ,case\n" +
                "        when (select exists(select amount from MedicineRecord where medicineIdx = ? and slot = ? and status = 'Y')) = 1\n" +
                "            then (select amount from MedicineRecord where medicineIdx = ? and slot = ? and status = 'Y' order by createAt desc limit 1)\n" +
                "        else 1\n" +
                "    end as lastAmount from MedicineRecord  where medicineIdx = ? and slot = ? and status = 'Y' order by createAt desc) last) as amount, \"\" as memo, days\n" +
                "from Medicine inner join MedicineTime on Medicine.medicineIdx = MedicineTime.medicineIdx\n" +
                "where Medicine.medicineIdx = ? and slot = ? and MedicineTime.status = 'Y'";
        Object[] getMedicineParams = new Object[]{medicineIdx, timeSlot, medicineIdx, timeSlot, medicineIdx, timeSlot, medicineIdx, timeSlot};

        return this.jdbcTemplate.queryForObject(getMedicineQuery,
                (rs, rowNum) -> new GetMedicine (
                        rs.getInt("medicineIdx"),
                        rs.getString("medicineRealName"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getDouble("amount"),
                        rs.getString("memo"),
                        rs.getInt("days")
                ), getMedicineParams);
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
