package com.estatehub.backend.mappers;

import com.estatehub.backend.dtos.RegisterRequest;
import com.estatehub.backend.dtos.UserDTO;
import com.estatehub.backend.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)      
    User toEntity(RegisterRequest request);
}
