package com.skhu.moodfriend.app.controller.auth;

import com.skhu.moodfriend.app.dto.auth.reqDto.*;
import com.skhu.moodfriend.app.dto.auth.resDto.AuthResDto;
import com.skhu.moodfriend.app.dto.auth.resDto.EmailCheckResDto;
import com.skhu.moodfriend.app.service.auth.*;
import com.skhu.moodfriend.global.template.ApiResponseTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Tag(name = "회원가입/로그인", description = "회원가입/로그인을 담당하는 api 그룹")
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final EmailService emailService;
    private final SignUpService signUpService;
    private final LoginService loginService;
    private final GoogleOAuthService googleOauthService;
    private final KakaoOAuthService kakaoOAuthService;
    private final TokenRenewService tokenRenewService;

    @PostMapping("/email-check")
    @Operation(
            summary = "이메일 중복 검사",
            description = "사용자가 입력한 이메일이 이미 존재하는지 여부를 체크합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "이메일 중복 검사 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public ResponseEntity<ApiResponseTemplate<EmailCheckResDto>> checkEmailDuplication(
            @Valid @RequestBody EmailCheckReqDto reqDto) {

        ApiResponseTemplate<EmailCheckResDto> data = signUpService.checkEmailDuplication(reqDto);
        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @PostMapping("/email-send")
    @Operation(
            summary = "이메일 인증 코드 전송",
            description = "사용자가 입력한 이메일 주소로 인증 코드를 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public ResponseEntity<ApiResponseTemplate<Void>> sendCode(
            @Valid @RequestBody EmailSendReqDto reqDto) {

        ApiResponseTemplate<Void> data = emailService.sendVerificationCode(reqDto);
        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @PostMapping("/email-verify")
    @Operation(
            summary = "이메일 인증 코드 검증",
            description = "사용자가 입력한 인증 코드를 검증합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 코드 검증 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 or 인증 코드 불일치"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public ResponseEntity<ApiResponseTemplate<Void>> verifyCode(
            @Valid @RequestBody EmailVerifyReqDto reqDto) {

        ApiResponseTemplate<Void> data = emailService.verifyCode(reqDto);
        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @PostMapping("/signUp")
    @Operation(
            summary = "자체 회원가입",
            description = "사용자 정보를 받아 DB에 저장합니다. refreshToken, accessToken을 발급하여 회원가입을 진행하고 사용자 정보와 함께 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "403", description = "url문제 or 관리자 문의"),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public ResponseEntity<ApiResponseTemplate<AuthResDto>> signUp(
            @Valid @RequestBody SignUpReqDto reqDto) {

        ApiResponseTemplate<AuthResDto> data = signUpService.signUp(reqDto);
        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @PostMapping("/login")
    @Operation(
            summary = "자체 로그인",
            description = "사용자의 이메일, 비밀번호를 받습니다. 암호화되어 저장된 비밀번호와 일치하는지 검사하고, 로그인을 통해서 갱신된 refreshToken, accessToken을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 암호 입력"),
                    @ApiResponse(responseCode = "404", description = "이메일을 찾을수 없거나, 잘못된 값을 입력"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public ResponseEntity<ApiResponseTemplate<AuthResDto>> login(
            @Valid @RequestBody LoginReqDto reqDto) {

        ApiResponseTemplate<AuthResDto> data = loginService.login(reqDto);
        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @GetMapping("/callback/google")
    @Operation(
            summary = "구글 회원가입/로그인 콜백",
            description = "구글 로그인 후 리다이렉션된 URI입니다. 인가 코드를 받아서 accessToken을 요청하고, 회원가입 또는 로그인을 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입/로그인 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public ResponseEntity<ApiResponseTemplate<AuthResDto>> googleCallback(
            @RequestParam(name = "code") String code) {

        ApiResponseTemplate<AuthResDto> data = googleOauthService.signUpOrLogin(googleOauthService.getGoogleAccessToken(code).getData());
        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @GetMapping("/callback/kakao")
    @Operation(
            summary = "카카오 회원가입/로그인 콜백",
            description = "카카오 로그인 후 리다이렉션된 URI입니다. 인가 코드를 받아서 accessToken을 요청하고, 회원가입 또는 로그인을 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원가입/로그인 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public ResponseEntity<ApiResponseTemplate<AuthResDto>> kakaoCallback(
            @RequestParam(name = "code") String code) {

        ApiResponseTemplate<AuthResDto> data = kakaoOAuthService.signUpOrLogin(kakaoOAuthService.getKakaoAccessToken(code).getData());
        return ResponseEntity.status(data.getStatus()).body(data);
    }

    @PostMapping("/renew")
    @Operation(
            summary = "accessToken 재발급",
            description = "refreshToken을 사용하여 새로운 accessToken을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            }
    )
    public ResponseEntity<ApiResponseTemplate<AuthResDto>> renewAccessToken(
            @RequestBody RefreshTokenReqDto reqDto) {

        ApiResponseTemplate<AuthResDto> data = tokenRenewService.renewAccessToken(reqDto.refreshToken());
        return ResponseEntity.status(data.getStatus()).body(data);
    }
}
