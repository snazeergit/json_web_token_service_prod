package com.wipro.mapper;

import com.wipro.dto.UserResponse;
import com.wipro.dto.UserSummary;
import com.wipro.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    //Entity to Response DTO
    UserResponse toResponseDTO(User user);

    //Entity to Summary DTO
    UserSummary toSummaryDTO(User user);
}

