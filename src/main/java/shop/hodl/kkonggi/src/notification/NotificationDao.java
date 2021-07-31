package shop.hodl.kkonggi.src.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.src.notification.model.*;
import shop.hodl.kkonggi.src.record.medicine.model.PostAllMedicineRecordReq;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class NotificationDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetNotificationRes getNotification(int userIdx){
        String getQuery = "select if(servicePush = 'Y', 1, 0) as servicePush, if(medicinePush = 'Y', 1, 0) as medicinePush, if(eventPush = 'Y', 1, 0) as eventPush, if(Notification.marketingPush = 'Y', 1, 0) as marketingPush\n" +
                "from Notification where userIdx = ? and status = 'Y'";
        return this.jdbcTemplate.queryForObject(getQuery,
                (rs, rowNum) -> new GetNotificationRes(
                        rs.getInt("servicePush"),
                        rs.getInt("medicinePush"),
                        rs.getInt("eventPush"),
                        rs.getInt("marketingPush")
                ), userIdx);
    }

    public Integer updateNotification(int userIdx, PatchNotificationReq patchReq){
        String updateQuery = "update Notification set servicePush = ?, medicinePush = ?, eventPush = ?, marketingPush = ? where userIdx = ? and status = 'Y'";
        Object[] updateParams = new Object[]{patchReq.getIsServicePush(), patchReq.getIsMedicinePush(), patchReq.getIsEventPush(), patchReq.getIsMarketingPush(), userIdx};
        return this.jdbcTemplate.update(updateQuery, updateParams);
    }

    public Integer updateMedicineNotification(int userIdx, List<PatchMedicineNotificationReq> patchReq){
        String updateQuery = "update MedicineNotification\n" +
                "set notificationTime = case when timeSlot = 'D' and ? is not null then ? else notificationTime end,\n" +
                "    notificationTime = case when timeSlot = 'M' and ? is not null then ? else notificationTime end,\n" +
                "    notificationTime = case when timeSlot = 'L' and ? is not null then ? else notificationTime end,\n" +
                "    notificationTime = case when timeSlot = 'E' and ? is not null then ? else notificationTime end,\n" +
                "    notificationTime = case when timeSlot = 'N' and ? is not null then ? else notificationTime end,\n" +
                "    status = case when timeSlot = 'D' and ? is not null then ? else status end,\n" +
                "    status = case when timeSlot = 'M' and ? is not null then ? else status end,\n" +
                "    status = case when timeSlot = 'L' and ? is not null then ? else status end,\n" +
                "    status = case when timeSlot = 'E' and ? is not null then ? else status end,\n" +
                "    status = case when timeSlot = 'N' and ? is not null then ? else status end\n" +
                "where userIdx = ? and status != 'N'";
        Object[] updateParamsTimeSlot =
                new Object[]{
                        patchReq.get(0).getNotificationTime(), patchReq.get(0).getNotificationTime(),
                        patchReq.get(1).getNotificationTime(), patchReq.get(1).getNotificationTime(),
                        patchReq.get(2).getNotificationTime(), patchReq.get(2).getNotificationTime(),
                        patchReq.get(3).getNotificationTime(), patchReq.get(3).getNotificationTime(),
                        patchReq.get(4).getNotificationTime(), patchReq.get(4).getNotificationTime(),
                        patchReq.get(0).getStatus(), patchReq.get(0).getStatus(),
                        patchReq.get(1).getStatus(), patchReq.get(1).getStatus(),
                        patchReq.get(2).getStatus(), patchReq.get(2).getStatus(),
                        patchReq.get(3).getStatus(), patchReq.get(3).getStatus(),
                        patchReq.get(4).getStatus(), patchReq.get(4).getStatus(), userIdx };
        return this.jdbcTemplate.update(updateQuery, updateParamsTimeSlot);
    }

    public int checkNotification(int userIdx){
        String checkQuery = "select exists(select userIdx from Notification where userIdx = ? and status = 'Y')";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, userIdx);
    }

    public int checkMedicineNotification(int userIdx){
        String checkQuery = "select exists(select userIdx from MedicineNotification where userIdx = ? and status != 'N')";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, userIdx);
    }

    public int createNotification(int userIdx){
        String createQuery = "insert into Notification (userIdx) values (?)";
        return this.jdbcTemplate.update(createQuery, userIdx);
    }


    public int createMedicineNotification(List<PostMedicineNotificationReq> postReq){
        String createQuery = "insert into MedicineNotification (userIdx, timeslot, notificationTime) VALUES (?,?,?), (?,?,?), (?,?,?), (?,?,?), (?,?,?)";
        Object createParams = new Object[]{
                postReq.get(0).getUserIdx(), postReq.get(0).getTimeSlot(), postReq.get(0).getNotificationTime(),
                postReq.get(1).getUserIdx(), postReq.get(1).getTimeSlot(), postReq.get(1).getNotificationTime(),
                postReq.get(2).getUserIdx(), postReq.get(2).getTimeSlot(), postReq.get(2).getNotificationTime(),
                postReq.get(3).getUserIdx(), postReq.get(3).getTimeSlot(), postReq.get(3).getNotificationTime(),
                postReq.get(4).getUserIdx(), postReq.get(4).getTimeSlot(), postReq.get(4).getNotificationTime(),
        };
        return this.jdbcTemplate.update(createQuery, createParams);
    }

    public GetMedicineNotificationRes getMedicineNotification(int userIdx){
        String getQuery = "select timeSlot, case\n" +
                "    when notificationTime like('%AM%') then REPLACE(notificationTime,'AM', '오전')\n" +
                "    when notificationTime like('%PM%') then REPLACE(notificationTime,'PM', '오후')\n" +
                "end as notificationTime, status from\n" +
                "(select timeSlot, date_format(notificationTime, '%p %h:%i') as notificationTime, if(status = 'A', 1, 0) as status\n" +
                "from MedicineNotification where userIdx = ? and status != 'N') medicineNoti";
        return new GetMedicineNotificationRes(
                this.jdbcTemplate.query(getQuery,
                (rs,rowNum) -> new GetMedicineNotificationRes.MedicineNotification(
                        rs.getString("timeSlot"),
                        rs.getString("notificationTime"),
                        rs.getInt("status")
                ), userIdx));
    }
}
