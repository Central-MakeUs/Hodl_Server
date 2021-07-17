package shop.hodl.kkonggi.src.document.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBoardRes {
    private int boradIdx;
    private String title;
    private String date;
    private int isNew;
}
