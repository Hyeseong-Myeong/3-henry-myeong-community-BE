package com.ktb.ktb_community.service;

import com.ktb.ktb_community.dto.UserRequestDto;
import com.ktb.ktb_community.entity.User;
import com.ktb.ktb_community.exception.DuplicatedException;
import com.ktb.ktb_community.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRequestDto createUserRequestDto(String email, String nickname, String password, String imageUrl) {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail(email);
        requestDto.setNickname(nickname);
        requestDto.setPassword(password);
        requestDto.setProfileImageUrl(imageUrl);
        return requestDto;
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void createUser_Success() {
        // given
        UserRequestDto requestDto = createUserRequestDto("test@test.com", "testuser", "password", null);
        User user = User.builder()
                .email(requestDto.getEmail())
                .password("encodedPassword")
                .nickname(requestDto.getNickname())
                .profileImageUrl(requestDto.getProfileImageUrl())
                .build();
        ReflectionTestUtils.setField(user, "userId", 1L);


        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByNickname(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        Long newUserId = userService.createUser(requestDto);

        // then
        assertThat(newUserId).isEqualTo(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 - 실패 (이메일 중복)")
    void createUser_Fail_EmailDuplicated() {
        // given
        UserRequestDto requestDto = createUserRequestDto("test@test.com", "testuser", "password", null);
        User existingUser = User.builder().email("test@test.com").password("p").nickname("n").build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(existingUser));

        // when & then
        assertThrows(DuplicatedException.class, () -> userService.createUser(requestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 - 실패 (닉네임 중복)")
    void createUser_Fail_NicknameDuplicated() {
        // given
        UserRequestDto requestDto = createUserRequestDto("test@test.com", "testuser", "password", null);
        User existingUser = User.builder().email("exist@test.com").password("p").nickname("testuser").build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByNickname("testuser")).thenReturn(Optional.of(existingUser));

        // when & then
        assertThrows(DuplicatedException.class, () -> userService.createUser(requestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원정보 수정 - 성공")
    void updateUser_Success() {
        // given
        UserRequestDto requestDto = createUserRequestDto("new@test.com", "newuser", "password", "new_image_url");
        User existingUser = User.builder().email("old@test.com").password("p").nickname("olduser").build();
        ReflectionTestUtils.setField(existingUser, "userId", 1L);
        User spyUser = spy(existingUser);

        Principal principal = () -> "1";

        when(userRepository.findById(1L)).thenReturn(Optional.of(spyUser));
        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByNickname("newuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(spyUser);

        // when
        userService.updateUser(requestDto, principal);

        // then
        verify(spyUser, times(1)).updateUser("new@test.com", "newuser", "new_image_url");
        verify(userRepository, times(1)).save(spyUser);
    }

    @Test
    @DisplayName("회원 탈퇴(soft-delete) - 성공")
    void deleteUser_Success() {
        // given
        String userId = "1";
        User existingUser = User.builder().email("test@test.com").password("p").nickname("test").build();
        ReflectionTestUtils.setField(existingUser, "userId", 1L);
        User spyUser = spy(existingUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(spyUser));

        // when
        userService.deleteUser(userId);

        // then
        verify(spyUser, times(1)).softDelete();
    }
}
