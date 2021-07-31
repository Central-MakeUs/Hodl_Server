package shop.hodl.kkonggi.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchUserInfoReq {
    private String nickName;
    private String birthYear;   // YYYY
    private String sex; // F, M
}
