package shop.hodl.kkonggi.src.user;


import shop.hodl.kkonggi.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(){
        String getUsersQuery = "select * from UserInfo";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password"))
                );
    }

    public List<GetUserRes> getUsersByEmail(String email){
        String getUsersByEmailQuery = "select * from UserInfo where email =?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                getUsersByEmailParams);
    }

    public GetUserRes getUser(int userIdx){
        String getUserQuery = "select * from UserInfo where userIdx = ?";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                getUserParams);
    }
    

    public int createUser(PostUserReq postUserReq, String userInfo){
        String createUserQuery = "insert into User (email, password, userInfoStatus) values (?, ?, ?)";
        Object[] createUserParams = new Object[]{postUserReq.getEmail(), postUserReq.getPassword(), userInfo};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ? and status = 'Y')";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ?";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public List<String> getSuccessModifyUserNickName(int groupId){
        String getPwdQuery = "select content from Chat where Chat.groupId = ? and status = 'Y'";
        int getPwdParams = groupId;

        List<String> chatArr = this.jdbcTemplate.queryForList(
                getPwdQuery,
                String.class,
                getPwdParams
        );
        return chatArr;
    }

    public List<String> getFalseModifyUserNickName(){
        String getPwdQuery = "select content from Chat where Chat.groupId = ? and status = 'Y'";
        int getPwdParams = 3;

        List<String> chatArr = this.jdbcTemplate.queryForList(
                getPwdQuery,
                String.class,
                getPwdParams
        );
        return chatArr;
    }

    public List<String> getFailModifyNickName(){
        String getPwdQuery = "select content from Chat where Chat.groupId = ? and status = 'Y'";
        int getPwdParams = 2;

        List<String> chatArr = this.jdbcTemplate.queryForList(
                getPwdQuery,
                String.class,
                getPwdParams
        );
        return chatArr;
    }

    public String getUserNickName(int userIdx){
        String getNickNameQuery = "select ifnull(nickName, \"\") as nickName from User where userIdx = ? and status = 'Y'";

        return this.jdbcTemplate.queryForObject(getNickNameQuery, String.class, userIdx);
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

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select userIdx, email, password, ifnull(nickName, '') as  nickName from User where email = ? and status = 'Y'";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("nickName")
                ),
                getPwdParams
                );
    }

}
