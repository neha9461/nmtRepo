package com.itt.test.nmt.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.junit4.SpringRunner;

import com.itt.nmt.models.User;
import com.itt.nmt.repositories.UserRepository;
import com.itt.nmt.services.MailService;
import com.itt.nmt.services.UserService;
import com.itt.test_category.ServicesTests;
import com.itt.test_data.UserTestDataRepository;
import com.itt.utility.Constants;
import com.itt.utility.EmailConstants;

@Category(ServicesTests.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Autowired
    private UserTestDataRepository userTestDataRepository;

    @MockBean
    private UserRepository userRepository;


    @MockBean
    private MailService mailService;

    @Before
    public final void setUp() {

    }

    @Test
    public final void save() throws MailException, InterruptedException, ExecutionException {

        // Arrange
        User user = userTestDataRepository.getUsers()

                .get("user-7");

        when(userRepository.save(user)).thenReturn(user);
        when(mailService.sendUserCreatedMail(user.getId(), user.getPassword(),
                EmailConstants.PARAM_PORTAL_LOGIN_LINK)).thenReturn(new AsyncResult<Boolean>(true));

        // Act
        user.setPassword("aaa");
        User createdUser = userService.save(user);

        // Assert
        assertEquals(createdUser.getEmail(), user.getEmail());
        assertEquals(createdUser.getFirstName(), user.getFirstName());
        assertEquals(createdUser.getLastName(), user.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public final void getUserByEmail() {
        // Arrange
        User user = userTestDataRepository.getUsers()
                .get("user-1");
        when(userRepository.findByEmailContainingIgnoreCase(user.getEmail())).thenReturn(user);

        // Act
        User userRecieved = userService.getUserByEmail(user.getEmail());
        // Assert
        assertEquals(userRecieved.getEmail(), user.getEmail());
        assertEquals(userRecieved.getFirstName(), user.getFirstName());
        assertEquals(userRecieved.getLastName(), user.getLastName());
        verify(userRepository, times(1)).findByEmailContainingIgnoreCase(user.getEmail());
    }

    @Test
    public final void getUserByID() {

        // Arrange
        User user = userTestDataRepository.getUsers()
                .get("user-1");
        when(userRepository.findOne(user.getId())).thenReturn(user);

        // Act
        User userRecieved = userService.getUserByID(user.getId());

        // Assert
        assertEquals(userRecieved.getId(), user.getId());
        assertEquals(userRecieved.getEmail(), user.getEmail());
        assertEquals(userRecieved.getFirstName(), user.getFirstName());
        assertEquals(userRecieved.getLastName(), user.getLastName());
        verify(userRepository, times(1)).findOne(user.getId());
    }

    @Test(expected = RuntimeException.class)
    public final void getNonExistingUser() {

        // Arrange
        User user = userTestDataRepository.getUsers()
                .get("user-1");
        when(userRepository.findOne(user.getId())).thenReturn(null);
        when(userService.getUserByID(user.getId()))
        .thenThrow(new RuntimeException(Constants.USER_DOES_NOT_EXIST_ERROR_MSG));

        // Act
        userService.getUserByID(user.getId());

        // Assert
        verify(userRepository, times(1)).findOne(user.getId());
    }

    @Test(expected = RuntimeException.class)
    public final void updateNonExistantUser() {

        // Arrange
        User user = userTestDataRepository.getUsers()
                .get("user-3");

        when(userRepository.findOne(user.getId())).thenReturn(null);
        user.setFirstName("test");
        when(userService.updateUser(user, user.getId()))
        .thenThrow(new RuntimeException(Constants.USER_DOES_NOT_EXIST_ERROR_MSG));

        // Act
        userService.updateUser(user, user.getId());

        // Assert
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public final void generateRandomPassword() {

        // Arrange
        User user = userTestDataRepository.getUsers()
                                      .get("user-1");

        String password = userService.generateRandomPassword(user);
        assertNotNull(password);
    }
}
