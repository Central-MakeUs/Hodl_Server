package shop.hodl.kkonggi.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private int userIdx;
    private String userName;
    private String password;
    private String email;
}
