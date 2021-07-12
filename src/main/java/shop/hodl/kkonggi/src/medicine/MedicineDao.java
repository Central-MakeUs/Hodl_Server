package shop.hodl.kkonggi.src.medicine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.src.medicine.model.GetMedAddTime;
import shop.hodl.kkonggi.src.medicine.model.GetStepperChatRes;
import shop.hodl.kkonggi.src.user.model.GetChatRes;

import javax.sql.DataSource;

@Repository
public class MedicineDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetChatRes getChats(String groupId, int scenarioIdx){

        String getChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and scenarioIdx = ?";
        String getActionQuery = "select distinct actionType from Action where groupId = ? and status = 'Y' and scenarioIdx =?";
        String getActionContentQuery = "select content, actionId from Action where groupId = ? and status = 'Y' and scenarioIdx =?";

        return new GetChatRes(this.jdbcTemplate.query(getChatQuery,
                (rs, rowNum)-> new GetChatRes.Chat(
                        rs.getString("chatType"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("content")
                ), groupId, scenarioIdx),

                this.jdbcTemplate.queryForObject(getActionQuery,
                        (rs, rowNum)-> new GetChatRes.Action(
                                rs.getString("actionType"),
                                this.jdbcTemplate.query(getActionContentQuery,
                                        (rk, rkNum)-> new GetChatRes.Action.Choice(
                                                rk.getString("actionId"),
                                                rk.getString("content")
                                        ), groupId, scenarioIdx)
                        ), groupId, scenarioIdx)
        );
    }

    public GetChatRes getChatsNoAction(String groupId, int scenarioIdx){
        String getChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and scenarioIdx = ?";

        return new GetChatRes(this.jdbcTemplate.query(getChatQuery,
                (rs, rowNum)-> new GetChatRes.Chat(
                        rs.getString("chatType"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("content")
                ), groupId, scenarioIdx),
                null
        );
    }

    public int getTotalStepNumber(int scenarioIdx){
        String getQuery = "select count(chatType) from (select chatType from Chat where Chat.chatType='BOT_STEPPER' and status = 'Y' and scenarioIdx = ?) Stepper";
        return this.jdbcTemplate.queryForObject(getQuery, int.class, scenarioIdx);
    }

    public GetStepperChatRes getStepperChats(String groupId, int scenarioIdx, int stepNumber){

        String getChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and Chat.chatType = 'BOT_STEPPER' and scenarioIdx = ?";
        String getActionQuery = "select distinct actionType from Action where groupId = ? and status = 'Y' and scenarioIdx =?";
        String getActionContentQuery = "select content, actionId from Action where groupId = ? and status = 'Y' and scenarioIdx =?";

        return this.jdbcTemplate.queryForObject(getChatQuery,
                ((rs, rowNum) -> new GetStepperChatRes(
                        getTotalStepNumber(scenarioIdx),
                        this.jdbcTemplate.query(getChatQuery,
                                (rr, rrNum)-> new GetStepperChatRes.StepperChat(
                                        rr.getString("chatType"),
                                        stepNumber,
                                        rr.getString("date"),
                                        rr.getString("time"),
                                        rr.getString("content")
                                ), groupId, scenarioIdx),
                        this.jdbcTemplate.queryForObject(getActionQuery,
                                (rf, rfNum)-> new GetStepperChatRes.Action(
                                        rf.getString("actionType"),
                                        this.jdbcTemplate.query(getActionContentQuery,
                                                (rk, rkNum)-> new GetStepperChatRes.Action.Choice(
                                                        rk.getString("actionId"),
                                                        rk.getString("content")
                                                ), groupId, scenarioIdx)
                                ), groupId, scenarioIdx)
                )), groupId, scenarioIdx);

    }

    public GetStepperChatRes getStepperChatsNoAction(String groupId, int scenarioIdx, int stepNumber){
        String getChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and Chat.chatType = 'BOT_STEPPER' and scenarioIdx = ?";

        return this.jdbcTemplate.queryForObject(getChatQuery,
                ((rs, rowNum) -> new GetStepperChatRes(
                getTotalStepNumber(scenarioIdx),
                this.jdbcTemplate.query(getChatQuery,
                        (rr, rrNum)-> new GetStepperChatRes.StepperChat(
                                rr.getString("chatType"),
                                stepNumber,
                                rr.getString("date"),
                                rr.getString("time"),
                                rr.getString("content")
                        ), groupId, scenarioIdx),
                        null
                )), groupId, scenarioIdx);
    }

    public GetMedAddTime getMedAddTime(String groupId, int scenarioIdx, int stepNumber){
        String getStepChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and Chat.chatType = 'BOT_STEPPER' and scenarioIdx = ?";
        String getChatQuery = "select chatType, content, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time from Chat where groupId = ? and status = 'Y' and Chat.chatType = 'BOT_NORMAL' and scenarioIdx = ?";
        String getActionQuery = "select distinct actionType from Action where groupId = ? and status = 'Y' and scenarioIdx =?";
        String getActionContentQuery = "select content, actionId from Action where groupId = ? and status = 'Y' and scenarioIdx =?";

        return new GetMedAddTime(
                getTotalStepNumber(scenarioIdx),
                this.jdbcTemplate.queryForObject(getStepChatQuery,
                        (rs, rowNum) -> new GetMedAddTime.StepperChat(
                                rs.getString("chatType"),
                                stepNumber,
                                rs.getString("date"),
                                rs.getString("time"),
                                rs.getString("content")
                        ), groupId, scenarioIdx),
                        this.jdbcTemplate.queryForObject(getChatQuery, (rs, rowNum) -> new GetMedAddTime.Chat(
                                rs.getString("chatType"),
                                rs.getString("date"),
                                rs.getString("time"),
                                rs.getString("content")
                        ), groupId, scenarioIdx),
                        this.jdbcTemplate.queryForObject(getActionQuery,
                        (rs, rowNum)-> new GetMedAddTime.Action(
                                rs.getString("actionType"),
                                this.jdbcTemplate.query(getActionContentQuery,
                                        (rk, rkNum)-> new GetMedAddTime.Action.Choice(
                                                rk.getString("actionId"),
                                                rk.getString("content")
                                        ), groupId, scenarioIdx)
                        ), groupId, scenarioIdx)
                );
    }

    public String getUserNickName(int userIdx){
        String getNickNameQuery = "select ifnull(nickName, \"\") as nickName from User where userIdx = ? and status = 'Y'";

        return this.jdbcTemplate.queryForObject(getNickNameQuery, String.class, userIdx);
    }
}
