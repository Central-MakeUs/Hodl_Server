package shop.hodl.kkonggi.src.email;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import shop.hodl.kkonggi.src.email.model.GetEmailReq;

import javax.sql.DataSource;


@Repository
public class EmailDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createAuth(String email, String ePw){
        String createAuthQuery = "insert into Authentication (email, code) values (?,?)";
        Object[] createAuthparamas = new Object[]{email, ePw};
        this.jdbcTemplate.update(createAuthQuery, createAuthparamas);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkAuthEmail(String email){
        String checkEmailQuery = "select exists(select email from Authentication where email = ? and status = 'Y')";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }

    public int checkAuthCode(GetEmailReq getEmailReq){
        String checkEmailQuery = "select exists(select email,code from Authentication where email = ? and code = ? and status = 'Y')";
        String checkEmailParams = getEmailReq.getEmail();
        int checkPwParams = getEmailReq.getCode();
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams, checkPwParams);
    }
}
