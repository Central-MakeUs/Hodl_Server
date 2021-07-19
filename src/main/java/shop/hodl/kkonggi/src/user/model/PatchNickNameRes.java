package shop.hodl.kkonggi.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor  (access = AccessLevel.PUBLIC)
public class PatchNickNameRes {
    private int userIdx;
    private String nickName;
}
