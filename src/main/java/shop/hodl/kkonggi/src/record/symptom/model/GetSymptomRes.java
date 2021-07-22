package shop.hodl.kkonggi.src.record.symptom.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSymptomRes {
    private int status;
    private List<Symptom> symptoms;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Symptom {
        private String groupName;
        private List<Check> checkList;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Check {
            private int symptomIdx;
            private String name;
            private int isChecked;
        }
    }
}
