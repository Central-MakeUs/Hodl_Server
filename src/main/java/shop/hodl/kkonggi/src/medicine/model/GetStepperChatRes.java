package shop.hodl.kkonggi.src.medicine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStepperChatRes {
    private int stepperChatCnt; // 스탭퍼 챗 전체 갯수
    private List<StepperChat> stepperChat;
    private Action action;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StepperChat {   // 말풍선
        private String chatType; //  텍스트 : NORMAL, 이미지 : IMAGE, 리스트 : LIST, 스태퍼 챗 : STEPPER,
        private int chatOrder;  // stepperchat 순서
        private String date; // YYYYMMDD
        private String time;    // hh:ss (pm/am)
        private String content; // 실제 내용
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Action{ // 유저의 선택
        private String actionType;  // USER_INPUT_CHIP_GROUP, USER_INPUT_EDIT_TEXT, USER_INPUT_BOTTOM_SHEET,...
        // 바텀 시트(checkbox, wheel_number_ui, medicine_add_custom_ui, medicine_record_custom_ui)
        private List<Choice> choiceList;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Choice {
            private String actionId;    // 버튼 id
            private String content; // 내용
        }
    }
}
