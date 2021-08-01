package shop.hodl.kkonggi.src.record.sleep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.src.record.sleep.model.GetSleepRes;
import shop.hodl.kkonggi.src.record.sleep.model.PostSleepReq;
import shop.hodl.kkonggi.src.user.model.GetChatRes;

import javax.sql.DataSource;

@Repository
public class SleepDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetChatRes.Chat getImage(String imageName){
        String getImageQuery = "select 'BOT_IMAGE' as chatType, (select (DATE_FORMAT(now(),'%Y%m%d') )) as date, (select (DATE_FORMAT(now(),'%h:%i %p'))) as time, imageUrl as content from LagomImage where imageName = ? and status = 'Y'";
        return this.jdbcTemplate.queryForObject(getImageQuery,
                (rs, rowNum) -> new GetChatRes.Chat(
                        rs.getString("chatType"),
                        rs.getString("date"),
                        rs.getString("time"),
                        rs.getString("content")
                ) , imageName);
    }

    public int checkSleepRecord(int userIdx, String date){
        String checkQuery = "select exists(select recordIdx from Sleep where userIdx = ? and date = ?)";
        Object [] checkParams = new Object[]{userIdx, date};
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, checkParams);
    }

    public int getSleepRecord(int userIdx, String date){
        String checkQuery = "select recordIdx from Sleep where userIdx = ? and date = ? and status != 'N'";
        Object [] checkParams = new Object[]{userIdx, date};
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, checkParams);
    }

    public String getUserNickName(int userIdx){
        String getNickNameQuery = "select ifnull(nickName, \"\") as nickName from User where userIdx = ? and status = 'Y'";
        return this.jdbcTemplate.queryForObject(getNickNameQuery, String.class, userIdx);
    }

    public GetSleepRes getSleep(int userIdx, String date, int status){
        String getQuery = "select DATE_FORMAT(date, '%Y%m%d') as date, DATE_FORMAT(sleepTime, '%H:%i') as sleepTime, DATE_FORMAT(wakeUpTime, '%H:%i') as wakeUpTime, memo\n" +
                "from (select ifnull(date, STR_TO_DATE(?, '%Y%m%d')) as date, ifnull(sleepTime, STR_TO_DATE('2200', '%H%i')) as sleepTime,\n" +
                "             ifnull(wakeUpTime, STR_TO_DATE('0600', '%H%i')) as wakeUpTime, ifnull(memo, \"\") as memo from Sleep right join User on User.userIdx =  Sleep.userIdx and Sleep.status != 'N' and date = ?\n" +
                "where User.userIdx = ? and User.status = 'Y') Info";
        Object [] getParams = new Object[]{date, date, userIdx};
        return this.jdbcTemplate.queryForObject(getQuery,
                (rs, rowNum) -> new GetSleepRes(
                        status,
                        rs.getString("date"),
                        rs.getString("sleepTime"),
                        rs.getString("wakeUpTime"),
                        rs.getString("memo")
                ), getParams);
    }

    public int createSleepRecord(int userIdx, PostSleepReq postSleepReq){
        String createQuery = "insert into Sleep (userIdx, date, sleepTime, wakeUpTime, memo, status) values (?, ?, ?, ?, ?, if(? = 1, 'Y', 'P'))";
        Object [] createParams = new Object[]{userIdx, postSleepReq.getDate(), postSleepReq.getSleepTime(), postSleepReq.getWakeUpTime(), postSleepReq.getMemo(), postSleepReq.getIsSleep()};
        this.jdbcTemplate.update(createQuery, createParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int updateSleepRecord(PostSleepReq postSleepReq, int recordIdx){
        String updateQuery = "update Sleep set sleepTime= ?, wakeUpTime = ?, memo = ?, status = if( ? = 1, 'Y', 'P'), date = ? where recordIdx = ?";
        Object [] updateParams = new Object[]{postSleepReq.getSleepTime(), postSleepReq.getWakeUpTime(), postSleepReq.getMemo(), postSleepReq.getIsSleep(), postSleepReq.getDate(), recordIdx};
        return this.jdbcTemplate.update(updateQuery, updateParams);
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
}
