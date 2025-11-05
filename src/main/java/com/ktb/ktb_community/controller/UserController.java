package com.ktb.ktb_community.controller;

import com.ktb.ktb_community.common.advice.ApiResponse;
import com.ktb.ktb_community.common.advice.ErrorDetail;
import com.ktb.ktb_community.common.advice.ErrorResponse;
import com.ktb.ktb_community.dto.UserRequestDto;
import com.ktb.ktb_community.dto.UserResponseDto;
import com.ktb.ktb_community.exception.DuplicatedException;
import com.ktb.ktb_community.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createUser(@RequestBody UserRequestDto userRequestDto) {

        Long userId = userService.createUser(userRequestDto);

        ApiResponse<Long> response = ApiResponse.success(
                "sign_up_success",
                userId
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserInfo(Principal principal) {
        String userEmail = principal.getName();
        UserResponseDto userResponseDto = userService.getUserInfo(userEmail);

        ApiResponse<UserResponseDto> response = ApiResponse.success(
                "get_user_success",
                userResponseDto
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@RequestBody UserRequestDto userRequestDto,
                                                        Principal principal) {

        UserResponseDto userResponseDto =  userService.updateUser(userRequestDto, principal);

        ApiResponse<UserResponseDto> response = ApiResponse.success(
                "user_update_success",
                userResponseDto
        );

        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteUser(Principal principal) {

        String userEmail = principal.getName();

        userService.deleteUser(userEmail);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email")
    public ResponseEntity<ApiResponse<Boolean>> emailExist(@RequestParam("email") String email) {

        Boolean emailUsable = userService.checkEmail(email);

        ApiResponse<Boolean> response = ApiResponse.success(
                "email_check_success",
                emailUsable
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/nickname")
    public ResponseEntity<ApiResponse<Boolean>> nicknameExist(@RequestParam("nickname") String nickname) {

        Boolean nicknameUsable = userService.checkNickname(nickname);
        ApiResponse<Boolean> response = ApiResponse.success(
                "nickname_check_success",
                nicknameUsable
        );

        return ResponseEntity.ok(response);
    }



    @ExceptionHandler(DuplicatedException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedException(DuplicatedException ex) {

        ErrorDetail errorDetail = new ErrorDetail(ex.getField(), ex.getReason());
        ErrorResponse errorResponse = new ErrorResponse("resource_conflict", Collections.singletonList(errorDetail));

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }


}
