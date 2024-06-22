package com.kimsreviews.API.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String email;
    private boolean isVerified;
    private boolean above18;

    public void setResetToken(String resetToken) {
    }

    public String getEmail() {
        return this.email;
    }

    public void setIsVerified(boolean b) {
    }
}

