package shop.hodl.kkonggi.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMyRes {
    private String userNickName;
    private String email;
    private int medicineCnt;
    private int startCnt;
}
