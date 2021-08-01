package shop.hodl.kkonggi.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStoreItemsRes {
    private String title;
    private List<Item> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item{
        private String imageUrl;
        private String linkUrl;
        private String name;
        private String logo;
        private String actualPrice;
        private String salePercent;
        private String salePrice;
    }

}
