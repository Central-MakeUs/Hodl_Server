package shop.hodl.kkonggi.src.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetBoradContentRes {
    private String title;
    private int isNew;
    private String date;
    private String content;
}
