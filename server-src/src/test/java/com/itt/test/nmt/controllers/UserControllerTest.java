
package com.itt.test.nmt.controllers;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.HashMap;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itt.nmt.models.User;
import com.itt.nmt.response.models.ResponseMsg;
import com.itt.nmt.services.UserService;
import com.itt.test_data.UserTestDataRepository;
import com.itt.utility.Constants;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class UserControllerTest.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebMvc

/** The Constant log. */
@Slf4j
public class UserControllerTest extends AbstractShiroTest {

    /** The mock mvc. */
    @Autowired
    private MockMvc mockMvc;

    /** The user service. */
    @MockBean
    private UserService userService;

    /** The test data repository. */
    @Autowired
    private UserTestDataRepository userTestDataRepository;

    /** The content type. */
    private MediaType contentType = new MediaType("application", "json", Charset.forName("UTF-8"));

    /** The ctx. */
    @Autowired
    private WebApplicationContext ctx;

    /** The subject under test. */
    private Subject subjectUnderTest;

    /** The mock session. */
    private MockHttpSession mockSession;

    /** The wac. */
    @Autowired
    private WebApplicationContext wac;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp()
        throws Exception {

        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                                 .build();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                      .build();
        DefaultWebSecurityManager securityManger = Mockito.mock(DefaultWebSecurityManager.class, RETURNS_DEEP_STUBS);
        ThreadContext.bind(securityManger);
        // 1. Create a mock authenticated Subject instance for the test to run:
        subjectUnderTest = new Subject.Builder((DefaultWebSecurityManager) getSecurityManager()).buildSubject();

        mockSession = new MockHttpSession(
            ctx.getServletContext(), subjectUnderTest.getSession()
                                                     .getId()
                                                     .toString());
        // 2. Bind the subject to the current thread:
        setSubject(subjectUnderTest);
    }
    /**
     * Gets the user.
     *
     * @throws Exception the exception
     */
    @Test
    public void getUser()
        throws Exception {

        // Arrange
        User user1 = userTestDataRepository.getUsers()
                                      .get("user-1");
        User user2 = userTestDataRepository.getUsers()
                .get("user-2");
        String jwtToken = "testtoken";
        when(userService.getLoggedInUser(jwtToken)).thenReturn(user1);
        when(userService.getUserByID(user2.getId())).thenReturn(user2);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/" + user2.getId())
                                                              .header(Constants.AUTHORIZATION, jwtToken)
                                                              .accept(MediaType.APPLICATION_JSON);

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assert
        resultActions.andExpect(status().isOk())
                     .andExpect(content().contentType(contentType))
                     .andExpect(jsonPath("$.user.firstName", is(user2.getFirstName())))
                     .andExpect(jsonPath("$.user.lastName", is(user2.getLastName())))
                     .andExpect(jsonPath("$.user.email", is(user2.getEmail())));

        verify(userService, times(1)).getUserByID(user2.getId());
    }
    /**
     * Gets the user.
     *
     * @throws Exception the exception
     */

    public void getUserWithException()
        throws Exception {

        // Arrange
        User user2 = userTestDataRepository.getUsers()
                                      .get("user-2");

        User user3 = userTestDataRepository.getUsers()
                .get("user-3");
        String jwtToken = "testtoken";
        when(userService.getLoggedInUser(jwtToken)).thenReturn(user2);
 
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/" + user3.getId())
                                                              .header(Constants.AUTHORIZATION, jwtToken)
                                                              .accept(MediaType.APPLICATION_JSON);
        ResponseMsg getResponseMessage = new ResponseMsg(false, Constants.USER_VIEWS_OTHER_USER_ERROR_MSG);

        // Act
        ResultActions resultActions = mockMvc.perform(requestBuilder);

        // Assert
        resultActions.andExpect(status().isBadRequest())
                     .andExpect(content().contentType(contentType))
                     .andExpect(jsonPath("$.success.message", is(getResponseMessage.getMessage())))
                     .andExpect(jsonPath("$.success.status", is(getResponseMessage.getStatus())));

    }
    /**
     * Adds the.
     *
     * @throws Exception the exception
     */
    @Test
    public void add()
        throws Exception {

        // Arrange
        User user = userTestDataRepository.getUsers()
                                      .get("user-1");
        ResponseMsg postResponseMsg = new ResponseMsg(true, Constants.USER_ADDED_SUCCESS_MSG);

        when(userService.save(user)).thenReturn(user);
        HashMap<String, User> map = new HashMap<String, User>();
        map.put("user", user);

        String content = new ObjectMapper().writeValueAsString(map);
        // Act
        ResultActions resultActions = null;

        resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post("/users")
                                  .contentType(MediaType.APPLICATION_JSON)
                                  .content(content));

        // Assert
        resultActions.andExpect(status().isOk())
                     .andExpect(
                         content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
                     .andExpect(jsonPath("$.success.message", is(postResponseMsg.getMessage())))
                     .andExpect(jsonPath("$.success.status", is(postResponseMsg.getStatus())));

        verify(userService, times(1)).save(user);
    }
    /**
     * Update user.
     *
     * @throws Exception the exception
     */
    @Test
    public void updateUser()
        throws Exception {

        // Arrange
        User user = userTestDataRepository.getUsers()
                                      .get("user-1");
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        user.setFirstName(firstName);
        user.setLastName(lastName);

        when(userService.updateUser(user, user.getId(), "testtoken")).thenReturn(user);
        HashMap<String, User> map = new HashMap<String, User>();
        map.put("user", user);

        String content = new ObjectMapper().writeValueAsString(map);
        // Act
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.put("/users/" + user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(Constants.AUTHORIZATION, "testtoken")
                    .content(content));

        ResponseMsg updateResponseMsg = new ResponseMsg(true, Constants.DEFAULT_UPDATE_SUCCESS_MSG);
        // Assert
        resultActions.andExpect(status().isOk())
                     .andExpect(content().contentType(contentType))
                     .andExpect(jsonPath("$.success.message", is(updateResponseMsg.getMessage())))
                     .andExpect(jsonPath("$.success.status", is(updateResponseMsg.getStatus())));

        verify(userService, times(1)).updateUser(user, user.getId(), "testtoken");
    }
    /**
     * Get Users Tests.
     *
     * @throws Exception the exception
     */
    @Test
    public void getUsersTest() throws Exception {

        User user = userTestDataRepository.getUsers()
                .get("user-3");
        when(userService.getLoggedInUser("testToken")).thenReturn(user);
        // Arrange
        mockMvc.perform(
                MockMvcRequestBuilders.get("/users").param("size", "10")
                        .header(Constants.AUTHORIZATION, "testToken")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    /**
     * Tear down.
     */
    @After
    public void tearDown() {

    }
}
