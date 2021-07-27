package shop.hodl.kkonggi.src.medicine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.src.medicine.model.*;

import javax.sql.DataSource;


import java.util.List;

import static shop.hodl.kkonggi.utils.Time.getDays;
import static shop.hodl.kkonggi.utils.Cycle.getTimeSlotOfMedicineTime;

@Repository
public class MedicineDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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

    public GetMedicine getMyMedicines(int userIdx){
        String getCntQuery = "select count(medicineidx) as count from Medicine where userIdx = ? and status = 'Y'";
        String getMyMedicineQuery = "select * from\n" +
                "              (select Medicine.medicineIdx, medicineRealName, days, ifnull(amount, 1) as amount, endDay\n" +
                "              from Medicine left join MedicineRecord on MedicineRecord.medicineIdx = Medicine.medicineIdx where userIdx = ? and Medicine.status = 'Y' order by day desc limit 18446744073709551615) as orderby\n" +
                "group by medicineIdx";
        Object[] getMyMedicineParams = new Object[]{userIdx};

        return this.jdbcTemplate.queryForObject(getCntQuery,
                (rs, rowNum) -> new GetMedicine(
                        rs.getInt("count"),
                        this.jdbcTemplate.query(getMyMedicineQuery,
                                (rk, rkNum) -> new GetMedicine.Medicine (
                                        rk.getInt("medicineIdx"),
                                        rk.getString("medicineRealName"),
                                        (String.join(",", getDays(rk.getInt("days")))),
                                        rk.getString("amount"),
                                        rk.getString("endDay")
                        ), getMyMedicineParams))
        , getMyMedicineParams);
    }

    public GetMedicine getMyMedicinesHasSlot(int userIdx, String[] arr){
        String getCntQuery = "select count(medicineidx) as count from Medicine where userIdx = ? and status = 'Y'";
        String getMyMedicineQuery = "select * from (select Medicine.medicineIdx, medicineRealName, days, ifnull(amount, 1) as amount, endDay\n" +
                "                from Medicine\n" +
                "                        inner join MedicineTime on Medicine.medicineIdx = MedicineTime.medicineIdx\n" +
                "                                                        and slot in (substring_index(?, ',', 1), substring_index(?, ',', 2), substring_index(?, ',', 3), substring_index(?, ',', 4), substring_index(?, ',', 5))\n" +
                "                         left join MedicineRecord on MedicineRecord.medicineIdx = Medicine.medicineIdx\n" +
                "                where userIdx = ? and Medicine.status = 'Y' order by day desc limit 18446744073709551615) orderby group by medicineidx";
        Object[] getMyMedicineParams = new Object[]{arr[0], arr[1], arr[2], arr[3], arr[4], userIdx};

        return this.jdbcTemplate.queryForObject(getCntQuery,
                (rs, rowNum) -> new GetMedicine(
                        rs.getInt("count"),
                        this.jdbcTemplate.query(getMyMedicineQuery,
                                (rk, rkNum) -> new GetMedicine.Medicine (
                                        rk.getInt("medicineIdx"),
                                        rk.getString("medicineRealName"),
                                        (String.join(",", getDays(rk.getInt("days")))),
                                        rk.getString("amount"),
                                        rk.getString("endDay")
                                ), getMyMedicineParams))
                , userIdx);
    }

    public int getMedicineCnt(int userIdx){
        String getQuery = "select count(medicineIdx) as count from Medicine where userIdx = ? and status ='Y'";
        return this.jdbcTemplate.queryForObject(getQuery, int.class, userIdx);
    }

    public int getTotalStepNumber(int scenarioIdx){
        String getQuery = "select count(chatType) from (select chatType from Chat where Chat.chatType='BOT_STEPPER' and status = 'Y' and scenarioIdx = ?) Stepper";
        return this.jdbcTemplate.queryForObject(getQuery, int.class, scenarioIdx);
    }

    public GetMedChatRes getMedChatRes(String groupId, int scenarioIdx, int stepNumber){
        String getChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and Chat.chatType = 'BOT_STEPPER' and scenarioIdx = ?";
        String getActionQuery = "select distinct actionType from Action where groupId = ? and status = 'Y' and scenarioIdx =?";
        String getActionContentQuery = "select content, actionId from Action where groupId = ? and status = 'Y' and scenarioIdx =?";

        return new GetMedChatRes(this.jdbcTemplate.query(getChatQuery,
                (rs, rowNum) -> new GetMedChatRes.StepperChat(
                        rs.getString("chatType"),
                        getTotalStepNumber(scenarioIdx),
                        stepNumber,
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
                        ), groupId, scenarioIdx
                ));
    }

    public GetMedChatRes getMedAddTime(String groupId, int scenarioIdx, int stepNumber, GetMedChatRes getMedChatRes, int i){
        String getStepChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and Chat.chatType = 'BOT_STEPPER' and scenarioIdx = ?";
        String getChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and Chat.chatType = 'BOT_NORMAL' and scenarioIdx = ?";
        String getActionQuery = "select distinct actionType from Action where groupId = ? and status = 'Y' and scenarioIdx =?";
        String getActionContentQuery = "select content, actionId from Action where groupId = ? and status = 'Y' and scenarioIdx =?";

        if(i == 0){
            // Chat에 추가
            getMedChatRes.setChat(this.jdbcTemplate.query(getStepChatQuery,
                    (rs, rowNum) -> new GetMedChatRes.StepperChat(
                            rs.getString("chatType"),
                            getTotalStepNumber(scenarioIdx),
                            stepNumber,
                            rs.getString("date"),
                            rs.getString("time"),
                            rs.getString("content")
                    ), groupId, scenarioIdx));

            // Action
            getMedChatRes.setAction(this.jdbcTemplate.queryForObject(getActionQuery,
                    (rs, rowNum)-> new GetMedChatRes.Action(
                            rs.getString("actionType"),
                            this.jdbcTemplate.query(getActionContentQuery,
                                    (rk, rkNum)-> new GetMedChatRes.Action.Choice(
                                            rk.getString("actionId"),
                                            rk.getString("content")
                                    ), groupId, scenarioIdx)
                    ), groupId, scenarioIdx));
        }
        if (i == 1) {
            // Chat에 추가
            getMedChatRes.getChat().add(this.jdbcTemplate.queryForObject(getChatQuery,
                    (rs, rowNum) -> new GetMedChatRes.Chat(
                            rs.getString("chatType"),
                            rs.getString("date"),
                            rs.getString("time"),
                            rs.getString("content")
                    ), groupId, scenarioIdx));

        }
        return getMedChatRes;
    }

    public String getUserNickName(int userIdx){
        String getNickNameQuery = "select ifnull(nickName, \"\") as nickName from User where userIdx = ? and status = 'Y'";

        return this.jdbcTemplate.queryForObject(getNickNameQuery, String.class, userIdx);
    }

    public GetMedicineDetailRes getMedicineDetail(int userIdx, int medicineIdx){
        String getMedicineDetailQuery = "select Medicine.medicineIdx, medicineRealName, date_format(startDay, '%Y.%m.%d') as medicineStartDay, date_format(endDay, '%Y.%m.%d') as medicineEndDay, ifnull(detail, concat(date_format(Medicine.createAt, '%Y.%m.%d'), '에 추가한 약이에요')) as medicineDatail ,\n" +
                "       days, ifnull(amount, 1.0) as amount\n" +
                "from Medicine left join MedicineRecord on Medicine.medicineIdx = MedicineRecord.medicineIdx\n" +
                "where userIdx = ? and Medicine.medicineIdx = ? and Medicine.status = 'Y' order by day desc limit 1";
        String getMedicineSlotQuery = "select slot from MedicineTime where medicineIdx = ? and status = 'Y'";
        Object[] getMedicineDetailParams = new Object[]{userIdx, medicineIdx};

        return this.jdbcTemplate.queryForObject(getMedicineDetailQuery,
                (rs, rowNum) -> new GetMedicineDetailRes(
                        rs.getInt("medicineIdx"),
                        rs.getString("medicineRealName"),
                        rs.getString("medicineDatail"),
                        rs.getString("medicineStartDay"),
                        rs.getString("medicineEndDay"),
                        String.join(",", getDays(rs.getInt("days"))),
                        String.join(",", getTimeSlotOfMedicineTime(this.jdbcTemplate.queryForList(getMedicineSlotQuery, String.class, medicineIdx))),
                        rs.getDouble("amount")
                ),getMedicineDetailParams);
    }

    public Integer updateMedicineDetail(PutMedicineReq putReq){
        String updateQuery = "update Medicine set medicineRealName = ?, detail = ?, startDay = ?, endDay = ? where medicineIdx = ? and userIdx = ? and status = 'Y'";
        Object[] updateParams = new Object[]{putReq.getMedicineRealName(), putReq.getMedicineDetail(), putReq.getStartDay(), putReq.getEndDay(), putReq.getMedicineIdx(), putReq.getUserIdx()};
        return this.jdbcTemplate.update(updateQuery, updateParams);
    }

    public Integer updateMedicineTime(int medicineIdx, String timeSlot, String status){
        String updateQuery = "update MedicineTime set status = ? where medicineIdx = ? and slot = ?";
        Object[] updateParams = new Object[]{status, medicineIdx, timeSlot};

        return this.jdbcTemplate.update(updateQuery, updateParams);
    }

    public List<String> getTimeSlot(int medicineIdx){
        String getQuery = "select slot from MedicineTime where medicineIdx = ? and status = 'Y'";
        return this.jdbcTemplate.queryForList(getQuery, String.class, medicineIdx);
    }

    public int checkMedicine(int userIdx, String medicineRealName){
        String checkMedicineQuery = "select exists(select medicineRealName from Medicine where userIdx= ? and medicineRealName = ? and status = 'Y')";
        Object[] checkMedicineParams = new Object[]{userIdx, medicineRealName};
        return this.jdbcTemplate.queryForObject(checkMedicineQuery, int.class, checkMedicineParams);
    }

    public int checkMedicine(int userIdx, int medicineIdx){
        String checkMedicineQuery = "select exists(select medicineIdx from Medicine where userIdx= ? and medicineIdx = ? and status = 'Y')";
        Object[] checkMedicineParams = new Object[]{userIdx, medicineIdx};
        return this.jdbcTemplate.queryForObject(checkMedicineQuery, int.class, checkMedicineParams);
    }

    public int checkMedicineTime(int medicineIdx){
        String checkMedicineQuery = "select exists(select medicineIdx from MedicineTime where medicineIdx = ? and status = 'Y')";
        Object[] checkMedicineParams = new Object[]{medicineIdx};
        return this.jdbcTemplate.queryForObject(checkMedicineQuery, int.class, checkMedicineParams);
    }

    public int checkMedicineTime(int medicineIdx, String timeSlot, String status){
        String checkMedicineQuery = "select exists(select medicineIdx from MedicineTime where medicineIdx = ? and slot = ? and status = ?)";
        Object[] checkMedicineParams = new Object[]{medicineIdx, timeSlot, status};
        return this.jdbcTemplate.queryForObject(checkMedicineQuery, int.class, checkMedicineParams);
    }

    public int createMedicine(PostMedicineReq postMedicineReq){
        String createMedicineQuery = "INSERT INTO  Medicine (userIdx, medicineRealName, days, startDay, endDay) values (?, ?, ?, ?, ?)";
        Object[] createMedicineParams = new Object[]{postMedicineReq.getUserIdx(), postMedicineReq.getMedicineRealName(), postMedicineReq.getDays(), postMedicineReq.getStartDay(), postMedicineReq.getEndDay()};

        this.jdbcTemplate.update(createMedicineQuery, createMedicineParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int deleteMedicine(PatchDeleteReq patchDeleteReq){
        String deleteQuery = "update Medicine set status = 'N' where medicineIdx = ?";
        Object[] deleteParams = new Object[]{patchDeleteReq.getMedicineIdx()};
        return this.jdbcTemplate.update(deleteQuery, deleteParams);
    }

    public int deleteMedicineTime(PatchDeleteReq patchDeleteReq){
        String deleteQuery = "update MedicineTime set status = 'N' where medicineIdx = ?";
        Object[] deleteParams = new Object[]{patchDeleteReq.getMedicineIdx()};
        return this.jdbcTemplate.update(deleteQuery, deleteParams);
    }

    public int createMedicineTime(int medicineIdx, String timeSlot){
        String createMedicineTimeQuery = "insert into MedicineTime (medicineIdx, slot) values (?, ?)";
        Object[] createMedicineTimeParams = new Object[]{medicineIdx, timeSlot};

        return this.jdbcTemplate.update(createMedicineTimeQuery, createMedicineTimeParams);
    }
}
