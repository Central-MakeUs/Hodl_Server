package shop.hodl.kkonggi.src.email.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor (access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class GetEmailReq {
    private String email;
    private Integer code;
}
