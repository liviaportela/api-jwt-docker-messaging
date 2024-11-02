package com.liviaportela.web.dto.mapper;

import com.liviaportela.entities.User;
import com.liviaportela.web.dto.UserCreateDto;
import com.liviaportela.web.dto.UserResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toUser(UserCreateDto dto) {
        return new ModelMapper().map(dto, User.class);
    }

    public static UserResponseDto toDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setAddress(user.getAddress());
        return userResponseDto;
    }
}
