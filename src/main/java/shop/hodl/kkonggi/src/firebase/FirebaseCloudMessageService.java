package shop.hodl.kkonggi.src.firebase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.sun.tools.javac.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import static shop.hodl.kkonggi.config.secret.Secret.FIREBASE_PATH;
import static shop.hodl.kkonggi.config.secret.Secret.API_URI;

@RequiredArgsConstructor
public class FirebaseCloudMessageService {
    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URI)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .data(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .android(FcmMessage.Android.builder()
                                .ttl("1200s")
                                .build()
                        )
                        .build()
                )
                .validate_only(false)
                .build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = FIREBASE_PATH;
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        // accessToken 생성
        googleCredentials.refreshIfExpired();
        // 최종 토큰값
        return googleCredentials.getAccessToken().getTokenValue();
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class FcmMessage {
        private boolean validate_only;
        private Message message;

        @Builder
        @AllArgsConstructor
        @Getter
        public static class Message {
            private Notification data;
            private String token;
            private Android android;
        }

        @Builder
        @AllArgsConstructor
        @Getter
        public static class Notification {
            private String title;
            private String body;
            private String image;
        }

        @Builder
        @AllArgsConstructor
        @Getter
        public static class Android {
            private String ttl;
        }
    }
}
