package shop.hodl.kkonggi.src.user;


import shop.hodl.kkonggi.src.notification.model.PostMedicineNotificationReq;
import shop.hodl.kkonggi.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

import static shop.hodl.kkonggi.utils.ValidationRegex.getMaskedEmail;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // Notification 테이블 만들기
    public int createNotification(int userIdx){
        String createQuery = "insert into Notification (userIdx) values (?)";
        return this.jdbcTemplate.update(createQuery, userIdx);
    }

    // MedicineNotification 테이블 만들기
    public int createMedicineNotification(List<PostMedicineNotificationReq> postReq){
        String createQuery = "insert into MedicineNotification (userIdx, timeslot, notificationTime) VALUES (?,?,?), (?,?,?), (?,?,?), (?,?,?), (?,?,?)";
        Object[] createParams = new Object[]{
                postReq.get(0).getUserIdx(), postReq.get(0).getTimeSlot(), postReq.get(0).getNotificationTime(),
                postReq.get(1).getUserIdx(), postReq.get(1).getTimeSlot(), postReq.get(1).getNotificationTime(),
                postReq.get(2).getUserIdx(), postReq.get(2).getTimeSlot(), postReq.get(2).getNotificationTime(),
                postReq.get(3).getUserIdx(), postReq.get(3).getTimeSlot(), postReq.get(3).getNotificationTime(),
                postReq.get(4).getUserIdx(), postReq.get(4).getTimeSlot(), postReq.get(4).getNotificationTime(),
        };
        return this.jdbcTemplate.update(createQuery, createParams);
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

    public GetUserInfo getUser(int userIdx){
        String getUserQuery = "select email, ifnull(nickName, '') as nickName, '이메일 회원가입' as signUpType, date_format(birthYear, '%Y') as birthYear, sex from User\n" +
                "    left join UserInfo on User.userIdx = UserInfo.userIdx and UserInfo.status = 'Y'\n" +
                "where User.status = 'Y' and User.userIdx = ?";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserInfo(
                        rs.getString("nickName"),
                        getMaskedEmail(rs.getString("email")),
                        rs.getString("signUpType"),
                        rs.getString("birthYear"),
                        rs.getString("sex")),
                getUserParams);
    }

    public int checkUserInfo(int userIdx){
        String checkQuery = "select exists(select userIdx from UserInfo where userIdx = ? and status = 'Y')";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, userIdx);
    }

    public int updateUserInfo(int userIdx, PatchUserInfoReq patchReq){
        String updateQuery = "update User left join UserInfo on User.userIdx = UserInfo.userIdx\n" +
                "    set nickName = ?, sex = ?, birthYear = ?\n" +
                "where User.userIdx = ?";
        Object[] updateParams = new Object[]{patchReq.getNickName(), patchReq.getSex(), patchReq.getBirthYear(), userIdx};
        return this.jdbcTemplate.update(updateQuery, updateParams);
    }

    public int createUserInfo(int userIdx, PatchUserInfoReq patchReq){
        String createQuery = "insert into UserInfo (useridx, sex, birthyear) values (?,?,?)";
        Object[] createParams = new Object[]{userIdx, patchReq.getSex(), patchReq.getBirthYear()};
        return this.jdbcTemplate.update(createQuery, createParams);
    }

    public int checkUser(int userIdx){
        String checkQuery = "select exists(select userIdx from User where status = 'Y' and userIdx = ?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, userIdx);
    }

    public GetMyRes getMyPage(int userIdx){
        String getQuery = "select nickName, email, count(medicineIdx) as medicineCnt ,datediff(now(), User.createAt) + 1 as startCnt  from User\n" +
                "                    left join Medicine on User.userIdx = Medicine.userIdx and User.status = 'Y' and Medicine.status = 'Y' where User.useridx = ?";
        return this.jdbcTemplate.queryForObject(getQuery,
                (rs, rowNum) -> new GetMyRes(
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getInt("medicineCnt"),
                        rs.getInt("startCnt")
                        ), userIdx);
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
        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ? and status = 'Y'";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
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
