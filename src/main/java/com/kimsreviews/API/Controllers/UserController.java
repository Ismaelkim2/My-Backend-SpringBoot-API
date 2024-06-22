package com.kimsreviews.API.Controllers;

import com.kimsreviews.API.DTO.UserDTO;
import com.kimsreviews.API.Implementations.ContactForm;
import com.kimsreviews.API.Implementations.JwtAuthenticationResponse;
import com.kimsreviews.API.Implementations.JwtTokenProvider;
import com.kimsreviews.API.Implementations.LoginRequest;
import com.kimsreviews.API.Repository.UserRepo;
import com.kimsreviews.API.Services.EmailService;
import com.kimsreviews.API.Services.FileStorageService;
import com.kimsreviews.API.Services.UserInterface;
import com.kimsreviews.API.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/")
@CrossOrigin("http://localhost:4200")
public class UserController {

    private final UserInterface userInterface;
    private final EmailService emailService;
    private final UserRepo userRepo; // Add UserRepo here



    @Autowired
    public UserController(UserInterface userInterface, EmailService emailService, UserRepo userRepo) {
        this.userInterface = userInterface;
        this.emailService = emailService;
        this.userRepo = userRepo;
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserDTO>> getUsers() {
        return new ResponseEntity<>(userInterface.getAllUser(), HttpStatus.OK);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserDTO> userDetails(@PathVariable int id) {
        return ResponseEntity.ok(userInterface.getUserById(id));
    }

    @GetMapping("/user/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String q) {
        List<User> searchResults = UserRepo.findByUsernameContainingIgnoreCase(q);
        return ResponseEntity.ok(searchResults);
    }

    @GetMapping("/user/upload/documents")
    public ResponseEntity<List<String>> getUploadedDocuments() {
        List<String> documents = fileStorageService.getUploadedDocuments(); // Implement this method in FileStorageService
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/user/details")
    public ResponseEntity<UserDTO> getUserDetailsByPhoneNumber(@RequestParam String phoneNumber) {
        // Check if the phoneNumber is valid
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Fetch user details from the database based on the phone number
        UserDTO userDetails = userInterface.getUserDetailsByPhoneNumber(phoneNumber);

        if (userDetails != null) {
            // If user details are found, return them in the response
            return ResponseEntity.ok(userDetails);
        } else {
            // If user details are not found, return a 404 Not Found response
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/user/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        // Validate phone number
        if (userDTO.getPhoneNumber().length() < 10) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Phone number must be at least 10 digits long."));
        }

        // Validate email format
        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        if (!userDTO.getEmail().matches(emailRegex)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Invalid email format."));
        }

        // Check if phone number is already registered
        if (userRepo.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Phone number is already registered."));
        }

        // Create user
        UserDTO createdUser = userInterface.createUserDTO(userDTO);
        if (createdUser != null) {
            // Generate JWT token and return it in the response
            String jwtToken = generateJwtToken(createdUser);
            return ResponseEntity.ok().header("Authorization", jwtToken).body(createdUser);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO) {
        // Check if user exists and credentials are correct
        if (userInterface.verifyUserCredentials(userDTO)) {
            // Generate JWT token and return it in the response
            String jwtToken = generateJwtToken(userDTO);
            return ResponseEntity.ok().body(new LoginResponse("Login successful", jwtToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Login failed. Kindly check your credentials or register."));
        }
    }

    @PostMapping("/user/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody UserDTO userDTO) {
        try {
            boolean result = userInterface.resetPassword(userDTO.getPhoneNumber());
            if (result) {
                // Send email using JavaMailSender
                emailService.sendEmailUsingJavaMailSender(userDTO.getEmail(), "Password Reset Request", "Password reset instructions...");
                return ResponseEntity.ok().body(new ResponseMessage("Password reset instructions sent to your email."));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Unable to reset password."));
            }
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error processing password reset request: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error processing password reset request."));
        }
    }


    @PostMapping("/contact")
    public ResponseEntity<String> submitContactForm(@RequestBody ContactForm contactForm) {
        try {
            // Return a success response
            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            e.printStackTrace();
            // Return an error response if something goes wrong
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending message");
        }
    }

    @PostMapping("/api/user/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Your authentication logic here
        // Check if the provided credentials (phone number and password) are valid
        // If valid, generate a JWT token and return it in the response
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();
        String token = jwtTokenProvider.generateToken(loginRequest.getPhoneNumber());
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @Autowired
    private FileStorageService fileStorageService; // Inject your file storage service here

    @PostMapping("/user/upload")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            // Save the uploaded file
            String fileName = fileStorageService.storeFile(file);
            // Construct the file download URL
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/user/download/")
                    .path(fileName)
                    .toUriString();
            return ResponseEntity.ok(fileDownloadUri);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }

    @PutMapping("/user/{id}/update")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable("id") int userId) {
        UserDTO response = userInterface.updateUser(userDTO, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/user/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int userId) {
        userInterface.deleteUserId(userId);
        return ResponseEntity.ok("User deleted");
    }

    // Method to generate JWT token
    private String generateJwtToken(UserDTO userDTO) {
        return "your-generated-jwt-token";
    }

    // Define a class for login response
    private static class LoginResponse {
        private String message;
        private String token;

        public LoginResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }

        public String getMessage() {
            return message;
        }

        public String getToken() {
            return token;
        }
    }

    // Define a class for error response
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    // Define a class for response message
    private static class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
