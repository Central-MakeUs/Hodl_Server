package shop.hodl.kkonggi.src.user.model;

import lombok.*;
import java.util.List;
import static shop.hodl.kkonggi.utils.Time.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetChatRes {
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

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StepperChat extends GetChatRes.Chat {   // 말풍선
        private String chatType; //  텍스트 : NORMAL, 이미지 : IMAGE, 리스트 : LIST, 스태퍼 챗 : STEPPER,
        private int stepperChatCnt;
        private int chatOrder;  // stepperchat 순서
        private String date; // YYYYMMDD
        private String time;    // hh:ss (pm/am)
        private String content; // 실제 내용
    }

    public GetChatRes createExceptionChat(String content){
        GetChatRes getChatRes =
                new GetChatRes((List<Chat>) new Chat("BOT_NORMAL", getCurrentDateStr(), getCurrentTimeStr(), content), null);
        return getChatRes;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GetChatRes){
            for (int i = 0; i < this.getChat().size(); i++){
                if(!this.getChat().get(i).getContent().equals(((GetChatRes) obj).getChat().get(i).getContent()))
                    return false;
                if(!this.getChat().get(i).getChatType().equals(((GetChatRes) obj).getChat().get(i).getChatType()))
                    return false;
                if(!this.getChat().get(i).getDate().equals(((GetChatRes) obj).getChat().get(i).getDate()))
                    return false;
                if(!this.getChat().get(i).getTime().equals(((GetChatRes) obj).getChat().get(i).getTime()))
                    return false;
            }
            if(!this.getAction().getActionType().equals(((GetChatRes) obj).getAction().getActionType())) return false;

            for (int i = 0; i < this.getAction().getChoiceList().size(); i++){
                if(!this.getAction().getChoiceList().get(i).getActionId().equals(((GetChatRes) obj).getAction().getChoiceList().get(i).getActionId()))
                    return false;
                if(!this.getAction().getChoiceList().get(i).getContent().equals(((GetChatRes) obj).getAction().getChoiceList().get(i).getContent()))
                    return false;
            }

            return true;
        }
        return false;
    }
}
