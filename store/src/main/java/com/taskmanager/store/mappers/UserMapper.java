package com.taskmanager.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.taskmanager.store.dtos.UserDto;
import com.taskmanager.store.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(target = "username", source = "realUsername")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    UserDto toUserDto(User user);
}
