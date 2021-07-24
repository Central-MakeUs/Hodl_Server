package shop.hodl.kkonggi.utils;

import shop.hodl.kkonggi.src.record.symptom.model.GetSymptomRes;
import shop.hodl.kkonggi.src.user.model.GetChatRes;

public class Chat {
    // 닉네임 변경
    public static GetChatRes replaceNickName(GetChatRes getChatRes, String name){
        String nickReplace = "%user_nickname%";
        for(int i = 0; i < getChatRes.getChat().size(); i++){
            if(getChatRes.getChat().get(i).getContent().contains(nickReplace))
                getChatRes.getChat().get(i).setContent(getChatRes.getChat().get(i).getContent().replace(nickReplace, name));
        }
        return getChatRes;
    }

    public static GetSymptomRes getSymptoms(int isChecked, GetSymptomRes getSymptomRes){
        int symIdx = 1;
        for(int i = 0; i < getSymptomRes.getSymptoms().size(); i++){
            for(int k = 0; k < getSymptomRes.getSymptoms().get(i).getCheckList().size(); k++, symIdx++){
                if((isChecked & (int) Math.pow(2, symIdx)) == (int) Math.pow(2, symIdx)){
                    getSymptomRes.getSymptoms().get(i).getCheckList().get(k).setIsChecked(1);
                }
            }
        }
        return getSymptomRes;
    }

    public static int makeSymptoms(int[] symptoms){
        int sum = 0;
        for(int i = 0; i < symptoms.length; i++){
            sum += Math.pow(2, symptoms[i]);
        }
        return sum;
    }

    public static GetChatRes makeSaveFailChat(GetChatRes getChatRes, String actionType, String retry, String discard){
        getChatRes.getAction().setActionType(actionType);
        for(int i = 0; i < getChatRes.getAction().getChoiceList().size(); i++){
            if(getChatRes.getAction().getChoiceList().get(i).getContent().contains("취소"))
                getChatRes.getAction().getChoiceList().get(i).setActionId(retry);
            if(getChatRes.getAction().getChoiceList().get(i).getContent().contains("재전송"))
                getChatRes.getAction().getChoiceList().get(i).setActionId(discard);
        }
        return getChatRes;
    }
}
