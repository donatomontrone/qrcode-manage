package com.example.qrapp.mapper;

import com.example.qrapp.dto.UserEditDTO;
import com.example.qrapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class InstanceMapper {

  public UserEditDTO userToUserEditDTO(User user) {
    if (user == null) return null;
    UserEditDTO userEditDTO = new UserEditDTO();
    userEditDTO.setId(user.getId());
    userEditDTO.setFirstName(user.getFirstName());
    userEditDTO.setLastName(user.getLastName());
    userEditDTO.setEmail(user.getEmail());
    userEditDTO.setPassword(user.getPassword());
    userEditDTO.setConfirmPassword(user.getPassword());
    return userEditDTO;
  }
}
