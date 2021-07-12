package shop.hodl.kkonggi.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_NO_EMAIL(false,2018,"존재하지 않는 이메일입니다."),
    POST_USERS_INVALID_PASSWORD(false, 2030, "비밀번호 형식을 확인해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 2031, "비밀번호를 입력해주세요"),
    POST_USERS_EMPTY_USERINFO(false, 2032, "개인정보 동의를 체크해주세요."),

    INVALID_AUTH_EMAIL_CODE(false, 2020, "인증코드가 틀렸습니다."),
    POST_AUTH_EMPTY_CODE(false, 2021, "인증코드를 입력해주세요."),
    POST_AUTH_EMPTY_EMAIL(false, 2022, "인증코드를 보낸 이메일이 없습니다."),
    POST_AUTH_EXISTS_EMAIL(false, 2023, "이미 회원가입이 완료된 이메일입니다."),

    PATCH_USERS_EMPTY_NICKNAME(false, 2024, "닉네임을 입력해주세요."),
    PATCH_USERS_ALREADY_NICKNAME(false, 2025, "똑같은 닉네임입니다."),


    GET_MEDICINE_EMPTY_NAME(false, 2030, "약 이름을 입력해주세요."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    // Send Mail
    SEND_MAIL_ERROR(false, 4020, "인증코드 전송에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
