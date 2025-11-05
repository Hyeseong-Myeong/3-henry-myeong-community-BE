package com.ktb.ktb_community.service;

import com.ktb.ktb_community.dto.UserRequestDto;
import com.ktb.ktb_community.dto.UserResponseDto;
import com.ktb.ktb_community.entity.User;
import com.ktb.ktb_community.exception.DuplicatedException;
import com.ktb.ktb_community.exception.NoPermissionException;
import com.ktb.ktb_community.exception.NotFoundException;
import com.ktb.ktb_community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long createUser(UserRequestDto userRequestDto) {

        //이메일 검증 및 닉네임 검증
        if(userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new DuplicatedException("email", "duplicated");
        }
        if(userRepository.findByNickname(userRequestDto.getNickname()).isPresent()) {
            throw new DuplicatedException("nickname", "duplicated");
        }

        User user = User.builder()
                .email(userRequestDto.getEmail())
                .nickname(userRequestDto.getNickname())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .profileImageUrl(userRequestDto.getProfileImageUrl())
                .build();

        return userRepository.save(user).getUserId();
    }

    public UserResponseDto getUserInfo(String userEmail){

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("user", "not found"));
        return UserResponseDto.from(user);
    }

    @Transactional
    public UserResponseDto updateUser(UserRequestDto userRequestDto, Principal principal) {

        //권한 검증
        if(!Objects.equals(userRequestDto.getEmail(), principal.getName())) {
            throw new NoPermissionException("user", "no_permission");
        }

        if(userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new DuplicatedException("email", "duplicated");
        }
        if(userRepository.findByNickname(userRequestDto.getNickname()).isPresent()){
            throw new DuplicatedException("nickname", "duplicated");
        }

        User user =  userRepository.findByEmail(userRequestDto.getEmail()).orElseThrow(() -> new IllegalArgumentException("email not found") );

        user.updateUser(userRequestDto.getEmail(), userRequestDto.getNickname(), userRequestDto.getProfileImageUrl());

        return UserResponseDto.from(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("email not found"));

        user.softDelete();
    }

    public boolean checkEmail(String email) {

        boolean result = userRepository.findByEmail(email).isEmpty();

        if(result) {
            return true;
        }else{
            throw new DuplicatedException("email", "duplicated");
        }
    }

    public boolean checkNickname(String nickname) {

        boolean result = userRepository.findByNickname(nickname).isEmpty();

        if(result) {
            return true;
        } else{
            throw new DuplicatedException("nickname", "duplicated");
        }
    }
}
