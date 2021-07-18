package shop.hodl.kkonggi.src.record.medicine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.src.record.medicine.model.*;
import shop.hodl.kkonggi.src.medicine.model.GetMedChatRes;

import javax.sound.midi.Patch;
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
        String getMedicineQuery = "select medicineIdx,medicineRealName,\n" +
                "                    case\n" +
                "                        when taking like('%AM%') then REPLACE(taking,'AM', '오전')\n" +
                "                        when taking like('%PM%') then REPLACE(taking,'PM', '오후')\n" +
                "                        else taking\n" +
                "                    end as taking, status\n" +
                "                from(\n" +
                "                select medicineIdx, medicineRealName,\n" +
                "                       case\n" +
                "                           when MedicineInfo.status is null then concat(MedicineInfo.timeTime, ' 미복용')\n" +
                "                           when MedicineInfo.status = 'N' then '안먹음'\n" +
                "                               else MedicineInfo.recordTime\n" +
                "                       end as taking, status from\n" +
                "                       (select Medicine.medicineIdx, medicineRealName ,case\n" +
                "                           when MedicineTime.time is null then DATE_FORMAT(TIME(?),'%p %h:%i')\n" +
                "                           else (DATE_FORMAT(MedicineTime.time,'%p %h:%i'))\n" +
                "                           end  as timeTime,\n" +
                "                               (DATE_FORMAT(MedicineRecord.time,'%p %h:%i')) as recordTime, MedicineRecord.status as status from Medicine\n" +
                "                    inner join MedicineTime on Medicine.medicineIdx = MedicineTime.medicineIdx and slot = ?\n" +
                "                    left join MedicineRecord on MedicineTime.slot = MedicineRecord.slot and MedicineTime.medicineIdx = MedicineRecord.medicineIdx and MedicineRecord.day = ?\n" +
                "                where userIdx = ? and Medicine.status = 'Y' and MedicineTime.status = 'Y' and pow(2, weekday(DATE(?))) & days != 0 and (datediff(DATE(?), startDay) > -1) and if(endDay is null, TRUE, datediff(endDay, DATE(?)) > -1)) MedicineInfo) slotList";
        Object[] getMedicineParams = new Object[]{defaultTime, timeSlot, date, userIdx, date, date, date};

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

    public int updateAllMedicineRecord(PostAllMedicineRecordReq postReq, int index, double amount, int recordIdx){
        String createMedicieRecordQuery = "update MedicineRecord set medicineIdx =?, slot = ?, day =?, time = ?, amount =?, status = 'Y' where recordIdx = ?";
        Object[] createMedicieRecordParmas = new Object[]{postReq.getMedicineIdx()[index], postReq.getTimeSlot(), postReq.getDate(), postReq.getTime(), amount, recordIdx};

        this.jdbcTemplate.update(createMedicieRecordQuery, createMedicieRecordParmas);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int createMedicineRecord(PostMedicineRecordReq postReq, int medicineIdx, String timeSlot){
        String createMedicieRecordQuery = "insert into MedicineRecord (medicineIdx, slot, day, time, memo, amount, status) values (?, ?, ?, ?, ?, ?, ?)";
        Object[] createMedicieRecordParmas = new Object[]{medicineIdx, timeSlot, postReq.getDate(), postReq.getTime(), postReq.getMemo(), postReq.getAmount(), postReq.getStatus()};

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

    public int checkRecordIdx(PatchMedicineRecordReq patchReq, int medicineIdx, String timeSlot){
        String checkQuery = "select (exists(select recordIdx from MedicineRecord where medicineIdx = ? and slot = ? and day = ?))";
        Object[] checkParams = new Object[]{medicineIdx, timeSlot, patchReq.getDate()};
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, checkParams);
    }

    public int checkRecordIdx(int medicineIdx, String slot, String date){
        String checkQuery = "select (exists(select recordIdx from MedicineRecord where medicineIdx = ? and slot = ? and day = ?))";
        Object[] checkParams = new Object[]{medicineIdx, slot, date};
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, checkParams);
    }

    public int getRecordIdx(PatchMedicineRecordReq patchReq, int medicineIdx, String timeSlot){
        String getRecordIdxQuery = "select recordIdx from MedicineRecord where medicineIdx = ? and slot = ? and day = ?";
        Object[] getRecordIdxParams = new Object[]{medicineIdx, timeSlot, patchReq.getDate()};
        return this.jdbcTemplate.queryForObject(getRecordIdxQuery, int.class, getRecordIdxParams);
    }

    public int getRecordIdx(int medicineIdx, String slot, String date){
        String getRecordIdxQuery = "select recordIdx from MedicineRecord where medicineIdx = ? and slot = ? and day = ?";
        Object[] getRecordIdxParams = new Object[]{medicineIdx, slot, date};
        return this.jdbcTemplate.queryForObject(getRecordIdxQuery, int.class, getRecordIdxParams);
    }

    public int updateMedicineRecord(int recordIdx, PatchMedicineRecordReq patchReq){
        String updateRecordIdxQuery = "update MedicineRecord set amount = ?, time = ?, memo = ?, status = ? , day = ? where recordIdx = ?;";
        Object[] updateRecordIdxQueryRecordIdxParams = new Object[]{patchReq.getAmount(), patchReq.getTime(), patchReq.getMemo(), patchReq.getStatus(), patchReq.getDate(), recordIdx};
        return this.jdbcTemplate.update(updateRecordIdxQuery, updateRecordIdxQueryRecordIdxParams);
    }

    public int checkSpecificMedicineRecord(int medicineIdx, String timeSlot, String day){
        String getCheckQuery = "select exists(select medicineIdx from MedicineRecord where medicineIdx = ? and slot = ? and day = ?)";
        Object[] getCheckParams = new Object[]{medicineIdx, timeSlot, day};
        return this.jdbcTemplate.queryForObject(getCheckQuery, int.class, getCheckParams);
    }

    public int checkUserMedicine(int userIdx){
        String checkMedicineQuery = "select exists(select userIdx from Medicine where userIdx= ? and status = 'Y')";
        return this.jdbcTemplate.queryForObject(checkMedicineQuery, int.class, userIdx);
    }

    public int checkTodayMedicine(int userIdx){
        String checkMedicineQuery = "select exists(select medicineIdx from Medicine where userIdx = ? and pow(2, weekday(now())) & days != 0 and datediff(DATE(now()), startDay) > -1 and if(endDay is null, TRUE, datediff(endDay, DATE(now())) > -1) and status = 'Y')";
        return this.jdbcTemplate.queryForObject(checkMedicineQuery, int.class, userIdx);
    }

    public String getUserNickName(int userIdx){
        String getNickNameQuery = "select ifnull(nickName, \"\") as nickName from User where userIdx = ? and status = 'Y'";
        return this.jdbcTemplate.queryForObject(getNickNameQuery, String.class, userIdx);
    }

    public int getTodayRecordCnt(int userIdx, String date){
        String query = "select count(*) from (select Medicine.medicineIdx, medicineRealName, slot, days from MedicineTime inner join Medicine on Medicine.medicineIdx = MedicineTime.medicineIdx\n" +
                "where userIdx = ? and Medicine.status = 'Y' and MedicineTime.status = 'Y' and pow(2, weekday(DATE(?))) & days != 0 and datediff(DATE(?), startDay) > -1 and if(endDay is null, TRUE, datediff(endDay, DATE(?)) > -1)) Cnt";
        Object[] params = new Object[]{userIdx, date, date, date};
        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }

    public int getTodayYesCnt(int userIdx, String date){
        String query = "select count(*) from\n" +
                "(select MedicineRecord.slot from MedicineRecord\n" +
                "    inner join MedicineTime on MedicineTime.medicineIdx = MedicineRecord.medicineIdx and MedicineTime.slot = MedicineRecord.slot\n" +
                "    inner join Medicine on Medicine.medicineIdx = MedicineRecord.medicineIdx\n" +
                "where userIdx = ? and Medicine.status = 'Y' and MedicineTime.status = 'Y' and pow(2, weekday(DATE(?))) & days != 0 and datediff(DATE(?), startDay) > -1 and if(endDay is null, TRUE, datediff(endDay, DATE(?)) > -1) and MedicineRecord.status = 'Y') yes";
        Object[] params = new Object[]{userIdx, date, date, date};
        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }

    public int getTodayNoCnt(int userIdx, String date){
        String query = "select count(*) from\n" +
                "(select MedicineRecord.slot from MedicineRecord\n" +
                "    inner join MedicineTime on MedicineTime.medicineIdx = MedicineRecord.medicineIdx and MedicineTime.slot = MedicineRecord.slot\n" +
                "    inner join Medicine on Medicine.medicineIdx = MedicineRecord.medicineIdx\n" +
                "where userIdx = ? and Medicine.status = 'Y' and MedicineTime.status = 'Y' and pow(2, weekday(DATE(?))) & days != 0 and datediff(DATE(?), startDay) > -1 and if(endDay is null, TRUE, datediff(endDay, DATE(?)) > -1) and MedicineRecord.status = 'N') noRecord";
        Object[] params = new Object[]{userIdx, date, date, date};
        return this.jdbcTemplate.queryForObject(query, int.class, params);
    }


    public GetMedicine getSpecificMedicineRecordModify(int medicineIdx, String timeSlot, String date){
        String getMedicineQuery = "select MedicineRecord.medicineIdx, medicineRealName, day, DATE_FORMAT(time,'%H:%i') as time, amount, ifnull(memo, \"\") as memo, days  from MedicineRecord\n" +
                "                inner join Medicine on Medicine.medicineIdx = MedicineRecord.medicineIdx\n" +
                "                where Medicine.medicineIdx = ? and slot = ? and day = ?";
        Object[] getMedicineParams = new Object[]{medicineIdx, timeSlot, date};
        String getSlotQuery = "select\n" +
                "       case\n" +
                "       when slot = 'D' then '새벽'\n" +
                "            when slot = 'M' then '아침'\n" +
                "            when slot = 'L' then '점심'\n" +
                "            when slot = 'E' then '저녁'\n" +
                "            when slot = 'N' then '자기전'\n" +
                "            end as slot\n" +
                "       from MedicineTime where medicineIdx = ?";

        return this.jdbcTemplate.queryForObject(getMedicineQuery,
                (rs, rowNum) -> new GetMedicine (
                        rs.getInt("medicineIdx"),
                        rs.getString("medicineRealName"),
                        rs.getString("day"),
                        rs.getString("time"),
                        rs.getDouble("amount"),
                        rs.getString("memo"),
                        rs.getInt("days"),
                        this.jdbcTemplate.queryForList(getSlotQuery, String.class, medicineIdx),
                        0
                ), getMedicineParams);
    }

    public GetMedicine getSpecificMedicineRecord(String date,String defaultTime ,int medicineIdx, String timeSlot){
        String getMedicineQuery = "select Medicine.medicineIdx, medicineRealName, DATE_FORMAT(?, '%Y-%m-%d') as day,\n" +
                "       ifnull(DATE_FORMAT(time,'%H:%i'), ?) as time,ifnull((select lastAmount from (select distinct medicineIdx ,case\n" +
                "        when (select exists(select amount from MedicineRecord where medicineIdx = ? and slot = ? and status = 'Y')) = 1\n" +
                "            then (select amount from MedicineRecord where medicineIdx = ? and slot = ? and status = 'Y' order by createAt desc limit 1)\n" +
                "        else 1\n" +
                "    end as lastAmount from MedicineRecord  where medicineIdx = ? and slot = ? and status = 'Y' order by createAt desc) last), 1) as amount, \"\" as memo, days\n" +
                "from Medicine inner join MedicineTime on Medicine.medicineIdx = MedicineTime.medicineIdx\n" +
                "where Medicine.medicineIdx = ? and slot = ? and MedicineTime.status = 'Y'";
        Object[] getMedicineParams = new Object[]{date ,defaultTime, medicineIdx, timeSlot, medicineIdx, timeSlot, medicineIdx, timeSlot, medicineIdx, timeSlot};
        String getSlotQuery = "select\n" +
                "       case\n" +
                "       when slot = 'D' then '새벽'\n" +
                "            when slot = 'M' then '아침'\n" +
                "            when slot = 'L' then '점심'\n" +
                "            when slot = 'E' then '저녁'\n" +
                "            when slot = 'N' then '자기전'\n" +
                "            end as slot\n" +
                "       from MedicineTime where medicineIdx = ?";

        return this.jdbcTemplate.queryForObject(getMedicineQuery,
                (rs, rowNum) -> new GetMedicine (
                        rs.getInt("medicineIdx"),
                        rs.getString("medicineRealName"),
                        rs.getString("day"),
                        rs.getString("time"),
                        rs.getDouble("amount"),
                        rs.getString("memo"),
                        rs.getInt("days"),
                        this.jdbcTemplate.queryForList(getSlotQuery, String.class, medicineIdx),
                        0
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
                "          and pow(2, weekday(now())) & days != 0 and datediff(DATE(now()), startDay) > -1 and if(endDay is null, TRUE, datediff(endDay, DATE(now())) > -1) and userIdx = ?";

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
                "          and pow(2, weekday(now())) & days != 0 and datediff(DATE(now()), startDay) > -1 and if(endDay is null, TRUE, datediff(endDay, DATE(now())) > -1) and userIdx = ?) slot";

        return this.jdbcTemplate.queryForObject(getSlotCntQuery, int.class, userIdx);
    }

    public int getMedicineType(int userIdx){
        String getMedicineTypeQuery = "select count(medicineIdx) from (select medicineIdx from Medicine where Medicine.status = 'Y'\n" +
                "          and pow(2, weekday(now())) & days != 0 and datediff(DATE(now()), startDay) > -1 and if(endDay is null, TRUE, datediff(endDay, DATE(now())) > -1) and userIdx = ?) Medicines";
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
