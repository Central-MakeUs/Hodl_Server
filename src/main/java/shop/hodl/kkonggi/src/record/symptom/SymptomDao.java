package shop.hodl.kkonggi.src.record.symptom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.src.record.symptom.model.GetSymptomRes;
import shop.hodl.kkonggi.src.user.model.GetChatRes;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SymptomDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkSymptomOfDay(int userIdx, String date){
        String checkQuery = "select exists(select recordIdx from SymptomRecord where userIdx = ? and date = ? and status = 'Y')";
        Object[] checkParams = new Object[]{userIdx, date};
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, checkParams);
    }

    public String getUserNickName(int userIdx){
        String getNickNameQuery = "select ifnull(nickName, \"\") as nickName from User where userIdx = ? and status = 'Y'";
        return this.jdbcTemplate.queryForObject(getNickNameQuery, String.class, userIdx);
    }

    public int getChecked(int userIdx, String date){
        String checkQuery = "select checks from SymptomRecord where userIdx = ? and date = ? and status = 'Y'";
        Object[] checkParams = new Object[]{userIdx, date};
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, checkParams);
    }

    public int createSymptomRecord(int userIdx, String date, int symptom){
        String craeteQuery = "insert into SymptomRecord (userIdx, date, checks) values (?,?,?)";
        Object[] craeteParams = new Object[]{userIdx, date, symptom};
        this.jdbcTemplate.update(craeteQuery, craeteParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int updateSymptomRecord(int recordIdx, int checks){
        String updateQuery = "update SymptomRecord set checks = ? where recordIdx = ?";
        Object[] updateParams = new Object[]{checks, recordIdx};
        return this.jdbcTemplate.update(updateQuery, updateParams);
    }

    public GetSymptomRes getEmptySymptom(int status){
        String getGroupNameQuery = "select distinct symptomGroupName from Symptom where status = 'Y'";
        String getSymptomQuery = "select symptomIdx, symptomName, 0 as isChecked from Symptom where status = 'Y' and symptomGroupName = ?";
        return new GetSymptomRes(
                status,
                this.jdbcTemplate.query(getGroupNameQuery,
                        (rs, rowNum) -> new GetSymptomRes.Symptom(
                                rs.getString("symptomGroupName"),
                                this.jdbcTemplate.query(getSymptomQuery,
                                        (rk, rkNum) -> new GetSymptomRes.Symptom.Check(
                                                rk.getInt("symptomIdx"),
                                                rk.getString("symptomName"),
                                                rk.getInt("isChecked")), rs.getString("symptomGroupName"))
                        ))
        );
    }

    public int getSymptomOfDay(int userIdx, String date){
        String getQuery = "select recordIdx from SymptomRecord where userIdx = ? and date = ? and status = 'Y'";
        Object[] getParams = new Object[]{userIdx, date};
        return this.jdbcTemplate.queryForObject(getQuery, int.class, getParams);
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
