package com.kimsreviews.API.DTO;

import lombok.Data;

@Data
public class UserDTO {

    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private  String password;
    private boolean above18;
}
