package com.kimsreviews.API.Services;

import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.Exceptions.UserNotFoundEXceptions;
import com.kimsreviews.API.Repository.UserRepo;
import com.kimsreviews.API.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserInterface {

    private final UserRepo userRepo;
    private final JavaMailSender emailSender;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, JavaMailSender emailSender) {
        this.userRepo = userRepo;
        this.emailSender = emailSender;
    }
    @Override
    public UserDTO createUserDTO(UserDTO userDTO) {
        // Convert UserDTO to User entity and save to database
        User user = new User();
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setIsVerified(false); // Initially set as not verified

        userRepo.save(user);

        // Convert User entity back to UserDTO and return
        return userDTO;
    }


    @Override
    public List<UserDTO> getAllUser() {
        return userRepo.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(int id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundEXceptions("User not found"));
        return mapToDTO(user);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, int id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundEXceptions("User not found"));

        // Update user fields
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAbove18(userDTO.isAbove18());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail()); // Ensure email is updated

        User updatedUser = userRepo.save(user);
        return mapToDTO(updatedUser);
    }

    @Override
    public void deleteUserId(int id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundEXceptions("User not found"));
        userRepo.delete(user);
    }

    @Override
    public boolean verifyUserCredentials(UserDTO userDTO) {
        // Retrieve user information from the database based on the provided phone number
        User storedUser = userRepo.findByPhoneNumber(userDTO.getPhoneNumber());

        // Check if user exists and passwords match
        return storedUser != null && storedUser.getPassword().equals(userDTO.getPassword());
    }

    @Override
    public boolean resetPassword(String phoneNumber) {
        User user = userRepo.findByPhoneNumber(phoneNumber);
        if (user != null) {
            // Generate a password reset token and send it via email
            String resetToken = generateResetToken();
            sendResetTokenEmail(user.getEmail(), resetToken);
            user.setResetToken(resetToken);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    @Override
    public UserDTO getUserDetailsByPhoneNumber(String phoneNumber) {
        // Perform database query to fetch user details based on the phone number
        User user = userRepo.findByPhoneNumber(phoneNumber);

        // Check if user exists
        if (user != null) {
            // Map User entity to UserDTO and return
            return mapUserToDTO(user);
        } else {
            // If user does not exist, return null or handle the case appropriately
            return null;
        }
    }


    private UserDTO mapUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        // Map user fields to userDTO fields
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        // Map other fields as needed

        return userDTO;
    }

    private String generateResetToken() {
        // Generate a secure token for password reset
        return "123456"; // Replace with actual token generation logic
    }

    private void sendResetTokenEmail(String email, String token) {
        // Create a Simple MailMessage.
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Token");
        message.setText("Your password reset token is: " + token);

        // Send Message!
        emailSender.send(message);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail()); // Ensure email is correctly mapped
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setAbove18(user.isAbove18());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }

    private User mapToEntity(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail()); // Ensure email is correctly mapped
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAbove18(userDTO.isAbove18());
        user.setPassword(userDTO.getPassword());
        return user;
    }
}
