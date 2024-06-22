package com.kimsreviews.API.Services;

import com.kimsreviews.API.DTO.UserDTO;

import java.util.List;

public interface UserInterface {

    UserDTO createUserDTO (UserDTO userDTO);

    List<UserDTO> getAllUser();

    UserDTO getUserById(int id);

    UserDTO updateUser(UserDTO userDTO,int id);

    void deleteUserId(int id);

    boolean verifyUserCredentials(UserDTO userDTO);

    boolean resetPassword(String phoneNumber);

    UserDTO getUserDetailsByPhoneNumber(String phoneNumber);
}
