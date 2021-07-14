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
public class GetMedChatRes {

    private List<Chat> chat;
    private Action action;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Chat {   // 말풍선
        private String chatType; //  텍스트 : NORMAL, 이미지 : IMAGE, 리스트 : LIST, 스태퍼 챗 : STEPPER,
        //private String chatId; // 챗봇 시나리오에 있는 CHAT_ID
        private String date; // YYYYMMDD
        private String time;    // hh:ss (pm/am)
        private String content; // 실제 내용
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StepperChat extends Chat {   // 말풍선
        private String chatType; //  텍스트 : NORMAL, 이미지 : IMAGE, 리스트 : LIST, 스태퍼 챗 : STEPPER,
        private int stepperChatCnt;
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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GetMedChatRes){
            for (int i = 0; i < this.getChat().size(); i++){
                if(!this.getChat().get(i).getContent().equals(((GetMedChatRes) obj).getChat().get(i).getContent()))
                    return false;
                if(!this.getChat().get(i).getChatType().equals(((GetMedChatRes) obj).getChat().get(i).getChatType()))
                    return false;
                if(!this.getChat().get(i).getDate().equals(((GetMedChatRes) obj).getChat().get(i).getDate()))
                    return false;
                if(!this.getChat().get(i).getTime().equals(((GetMedChatRes) obj).getChat().get(i).getTime()))
                    return false;
            }
            if(!this.getAction().getActionType().equals(((GetMedChatRes) obj).getAction().getActionType())) return false;

            for (int i = 0; i < this.getAction().getChoiceList().size(); i++){
                if(!this.getAction().getChoiceList().get(i).getActionId().equals(((GetMedChatRes) obj).getAction().getChoiceList().get(i).getActionId()))
                    return false;
                if(!this.getAction().getChoiceList().get(i).getContent().equals(((GetMedChatRes) obj).getAction().getChoiceList().get(i).getContent()))
                    return false;
            }

            return true;
        }

        return false;
    }
}
