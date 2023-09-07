package com.sonet.core.model.mapper;

import com.sonet.core.model.dto.SignupRequestDto;
import com.sonet.core.model.dto.UserProfileDto;
import com.sonet.core.model.entity.User;
import org.mapstruct.*;

@Mapper(componentModel =  "spring")
public interface UserMapper {

    UserProfileDto toUserProfileDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "uuid", ignore = true)
    User toUser(@MappingTarget User user, UserProfileDto profileDto);

    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "role", expression = "java(com.sonet.core.model.Role.USER)")
    @Mapping(target = "password", source = "encodedPassword")
    User toUser(SignupRequestDto signupRequestDto, String encodedPassword);
}
