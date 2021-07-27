package shop.hodl.kkonggi.src.record.exercise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.src.record.exercise.model.PostExerciseReq;
import shop.hodl.kkonggi.src.record.sun.model.GetSunRes;
import shop.hodl.kkonggi.src.user.model.GetChatRes;

import javax.sql.DataSource;

@Repository
public class ExerciseDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkExerciseRecord(int userIdx, String date){
        String getQuery = "select exists(select recordIdx from ExerciseRecord where userIdx = ? and date = ? and status != 'N')";
        Object[] getParams = new Object[]{userIdx, date};
        return this.jdbcTemplate.queryForObject(getQuery, int.class, getParams);
    }

    public int getExerciseRecord(int userIdx, String date){
        String getQuery = "select recordIdx from ExerciseRecord where userIdx = ? and date = ? and status != 'N'";
        Object[] getParams = new Object[]{userIdx, date};
        return this.jdbcTemplate.queryForObject(getQuery, int.class, getParams);
    }

    public int createExerciseRecord(int userIdx, PostExerciseReq postReq){
        String createQuery = "insert into ExerciseRecord (userIdx, date, startTime, totalTime, memo, status) values (?, ?, ?, ?, ?, if(? = 1, 'Y', 'P'))";
        Object[] createParams = new Object[]{userIdx, postReq.getDate(), postReq.getStartTime(), postReq.getTotalTime(), postReq.getMemo(), postReq.getIsExercise()};

        this.jdbcTemplate.update(createQuery, createParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int updateExerciseRecord(int recordIdx, PostExerciseReq postReq){
        String createQuery = "update ExerciseRecord set startTime = ?, totalTime = ?, memo = ?, status = if( ? = 1, 'Y', 'P'), date = ? where recordIdx = ?";
        Object[] createParams = new Object[]{postReq.getStartTime(), postReq.getTotalTime(), postReq.getMemo(), postReq.getIsExercise(), postReq.getDate(), recordIdx};
        return this.jdbcTemplate.update(createQuery, createParams);
    }

    public GetSunRes getExercise(int userIdx, String date, int status){
        String getQuery = "select DATE_FORMAT(ifnull(date, STR_TO_DATE(?, '%Y%m%d')), '%Y%m%d') as date,\n" +
                "                       DATE_FORMAT(ifnull(startTime, STR_TO_DATE('0900', '%H%i')), '%H:%i') as startTime,\n" +
                "                       DATE_FORMAT(ifnull(totalTime, STR_TO_DATE('0015', '%H%i')), '%H:%i') as totalTime, ifnull(memo, \"\") as memo\n" +
                "                from ExerciseRecord right join User on User.userIdx = ExerciseRecord.userIdx and ExerciseRecord.status != 'N' and date = ? where User.userIdx = ?";
        Object[] getParams = new Object[]{date, date, userIdx};
        return this.jdbcTemplate.queryForObject(getQuery,
                (rs, rowNum) -> new GetSunRes(
                        status,
                        rs.getString("date"),
                        rs.getString("startTime"),
                        rs.getString("totalTime"),
                        rs.getString("memo"))
                , getParams);
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

    public String getUserNickName(int userIdx){
        String getNickNameQuery = "select ifnull(nickName, \"\") as nickName from User where userIdx = ? and status = 'Y'";
        return this.jdbcTemplate.queryForObject(getNickNameQuery, String.class, userIdx);
    }
}
