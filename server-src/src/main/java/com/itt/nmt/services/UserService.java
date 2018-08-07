
package com.itt.nmt.services;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.itt.nmt.jwt.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.itt.nmt.jwt.BCrypt;
import com.itt.nmt.jwt.JWTUtil;
import com.itt.nmt.models.User;
import com.itt.nmt.repositories.UserRepository;
import com.itt.nmt.response.models.ResponseMsg;
import com.itt.nmt.validators.UserValidator;
import com.itt.utility.Constants;
import com.itt.utility.EmailConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class that acts as an intermediary between controller and the
 * database for all basic CRUD operations. The business logic should reside in
 * service class.
 * 
 * @author Neha Goyal
 */
@Slf4j
@Service
public class UserService {

    /**
     * Instance of the basic User Repository implementation.
     */
    @Autowired
    private UserRepository repository;

    /**
     * Instance of mail service.
     */
    @Autowired
    private MailService mailService;

    /** The Constant PASSWORD_PREFIX_LENGTH. */
    public static final int PASSWORD_PREFIX_LENGTH = 3;

    /** The Constant PASSWORD_SALT_LENGTH. */
    public static final int PASSWORD_SALT_LENGTH = 4;
    
    /** The Constant SALT_LONG_ROUND. */
    public static final int SALT_LONG_ROUND = 12;

    /**
     * Gets the User given the email.
     * 
     * @param email Email of the User.
     * @return User object matching the email.
     */
    public User getUserByEmail(final String email) {

        return repository.findByEmailContainingIgnoreCase(email);
    }

    /**
     * Gets all the Users.
     *
     * @param page Page consisting Users.
     * @return List of all the users.
     */
    public Page<User> getAllUsers(final Pageable page) {

        return repository.findAll(page);
    }

    /**
     * Gets the logged in User.
     * 
     * @param jwtToken jwtToken of the logged in User.
     * @return User who has logged in.
     */
    public User getLoggedInUser(final String jwtToken) {

        String email = JWTUtil.getemail(jwtToken);
        return getUserByEmail(email);
    }

    /**
     * Saves the User.
     * 
     * @param user User object to be saved.
     * @return Users saved.
     */
    public User save(final User user) {

        User existingUser = getUserByEmail(user.getEmail());
        if (existingUser == null) {
            user.setDateJoined(new Date());
            String password = null;

            User savedUser = null;
            if (user.getPassword() != null) {
                password = user.getPassword();
                user.setPassword(encryptContent(password));
                savedUser = repository.save(user);
            } else {
                throw new RuntimeException(Constants.NULL_PASSWORD_MESSAGE);
            }

            try {
                mailService.sendUserCreatedMail(savedUser.getId(), password, 
                        EmailConstants.PARAM_PORTAL_LOGIN_LINK);
            } catch (MailException | InterruptedException e) {
                log.error(e.getMessage());
            }
            return savedUser;
        } else {
            throw new RuntimeException(Constants.DUPLICATE_RECORD);
        }
    }

    /**
     * Updates User.
     * 
     * @param user user to be updated.
     * @param id id of the user to be updated.
     * @param token jwt token of logged in user.
     * @return user.
     */
    public User updateUser(final User user, final String id, final String token) {

        User loggedInUser = getLoggedInUser(token);
        if (!loggedInUser.getId().equals(id)) {
            throw new UnauthorizedException();
        }
        return updateUser(user, id);
    }

    /**
     * Updates User.
     * 
     * @param user user to be updated.
     * @param id id of the user to be updated.
     * @return user.
     */
    public User updateUser(final User user, final String id) {

        User existingUser = repository.findOne(id);

        if (existingUser != null) {
            String requestedPassword = user.getPassword();

            boolean changePassword = false;
            if (user.getPassword() == null) {
                changePassword = false;
            } else {
                changePassword = !isContentMatched(requestedPassword, existingUser.getPassword());
            }

            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());

            if (changePassword) {
                existingUser.setPassword(encryptContent(requestedPassword));

            }
            User savedUser = repository.save(existingUser);

            if (changePassword) {
                try {
                    mailService.sendResetPasswordMail(savedUser, user.getPassword());
                } catch (MailException | InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
            return savedUser;
        } else {
            throw new RuntimeException(Constants.USER_DOES_NOT_EXIST_ERROR_MSG);
        }
    }

    /**
     * Get the User by ID.
     * 
     * @param id Id of the User.
     * @return User.
     */
    public User getUserByID(final String id) {

        User user = repository.findOne(id);
        if (user == null) {
            throw new RuntimeException(Constants.USER_DOES_NOT_EXIST_ERROR_MSG);
        }
        user.setPassword(null);
        return user;
    }

    /**
     * Validate user.
     *
     * @param user the user
     * @param result the result
     * @return the string
     */
    public String validateUser(final User user, final BindingResult result) {

        UserValidator userValidator = new UserValidator();
        userValidator.validate(user, result);
        String errorMsg = "";

        if (result.hasErrors()) {

            List<FieldError> errors = result.getFieldErrors();
            for (FieldError error : errors) {
                if (errorMsg.isEmpty()) {
                    errorMsg = error.getField() + " - " + error.getDefaultMessage();
                    continue;
                }
                errorMsg = errorMsg + "," + error.getField() + " - " + error.getDefaultMessage();
            }
        }
        return errorMsg;
    }

    /**
     * Sets the user session.
     *
     * @param jwtToken the jwt token
     * @param status the status
     */
    public void setUserSession(final String jwtToken, final boolean status) {

        User loggedInUser = getLoggedInUser(jwtToken);

        if (loggedInUser != null) {
            loggedInUser.setSession(status);
            repository.save(loggedInUser);

        } else {
            throw new RuntimeException(Constants.USER_DOES_NOT_EXIST_ERROR_MSG);
        }
    }

    /**
     * Process forgot passowrd.
     *
     * @param emailId the email id
     * @return the response msg
     */
    public ResponseMsg processForgotPassowrd(final String emailId) {

        String message = "";
        boolean status = false;
        if (emailId != null 
                && !emailId.isEmpty()) {
            User user = getUserByEmail(emailId);
            if (user != null) {
                String password = generateRandomPassword(user);
                user.setPassword(password);

                updateUser(user, user.getId());
                message = Constants.PASSWORD_RESET_SUCCESS;
                status = true;

            } else {
                message = Constants.USER_NOT_EXIST;
            }

        } else {
            message = Constants.INVALID_EMAIL_ID;
        }

        return new ResponseMsg(status, message);
    }

    /**
     * Generate random password.
     *
     * @param user the user
     * @return the string
     */
    public String generateRandomPassword(final User user) {

        String password = "";
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        if (firstName.length() >= PASSWORD_PREFIX_LENGTH) {
            password = firstName.substring(0, PASSWORD_PREFIX_LENGTH);
        } else {
            password = firstName;
        }

        if (lastName.length() >= PASSWORD_PREFIX_LENGTH) {
            password = password + lastName.substring(0, PASSWORD_PREFIX_LENGTH);
        } else {
            password = password + lastName;
        }

        String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < PASSWORD_SALT_LENGTH) { // length of the random
            // string.
            int index = (int) (rnd.nextFloat() * saltChars.length());
            salt.append(saltChars.charAt(index));
        }
        String saltStr = salt.toString();
        password = password + saltStr;
        return password;
    }
    
    /**
     * Encrypt content.
     *
     * @param content the content
     * @return the string
     */
    public static String encryptContent(final String content) {

        return BCrypt.hashpw(content, BCrypt.gensalt(SALT_LONG_ROUND));
    }

    /**
     * Checks if is content matched.
     *
     * @param content the content
     * @param encryptedContent the encrypted content
     * @return true, if is content matched
     */
    public static boolean isContentMatched(final String content, final String encryptedContent) {

        boolean isMatched = false;
        try {
            isMatched = BCrypt.checkpw(content, encryptedContent);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return isMatched;
    }
}

