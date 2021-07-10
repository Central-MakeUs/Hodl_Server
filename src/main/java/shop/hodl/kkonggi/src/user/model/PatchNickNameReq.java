package shop.hodl.kkonggi.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor  (access = AccessLevel.PUBLIC)
public class PatchNickNameReq {
    private String nickname;
}
