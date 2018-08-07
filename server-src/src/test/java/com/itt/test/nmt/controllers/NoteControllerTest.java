package com.itt.test.nmt.controllers;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itt.nmt.models.Note;
import com.itt.nmt.response.models.ResponseMsg;
import com.itt.nmt.services.NoteService;
import com.itt.test_data.NoteTestDataRepository;
import com.itt.utility.Constants;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * The Class ArticleControllerTest.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebMvc

/** The Constant log. */
@Slf4j
public class NoteControllerTest extends AbstractShiroTest {

    /** The mock mvc. */
    @Autowired
    private MockMvc mockMvc;

    /** The article test data repository. */
    @Autowired
    private NoteTestDataRepository noteTestDataRepository;

    /** The role test data repository. */
    @MockBean
    private NoteService noteService;

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
     * Adds the.
     *
     * @throws Exception the exception
     */
    @Test
    public void add()
            throws Exception {

        // Arrange
        Note note = noteTestDataRepository.getArticles()
                .get("note-1");
        ResponseMsg postResponseMsg = new ResponseMsg(true, Constants.NOTE_CREATED_MESSAGE);

        when(noteService.save(note)).thenReturn(note);
        HashMap<String, Note> map = new HashMap<String, Note>();
        map.put("note", note);

        String content = new ObjectMapper().writeValueAsString(map);
        // Act
        ResultActions resultActions = null;

        resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        // Assert
        resultActions.andExpect(status().isOk())
        .andExpect(
                content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
        .andExpect(jsonPath("$.success.message", is(postResponseMsg.getMessage())))
        .andExpect(jsonPath("$.success.status", is(postResponseMsg.getStatus())));
    }

    @Test
    public void updateArticleTest() throws Exception {

        // Arrange
        Note note = noteTestDataRepository.getArticles()
                .get("note-3");
        note.setVersion(note.getVersion() + 1);
        ResponseMsg putResponseMsg = new ResponseMsg(true, "Modifications have been saved successfully");

        when(noteService.updateNote(note.getId(), note, "testoken")).thenReturn(note);
        HashMap<String, Note> map = new HashMap<String, Note>();
        map.put("note", note);

        // Act
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/notes/" + note.getId())
                        .header(Constants.AUTHORIZATION, "testoken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(map)));

        // Assert
        resultActions.andExpect(status().isOk())
        .andExpect(
                content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
        .andExpect(jsonPath("$.success.message", is(putResponseMsg.getMessage())))
        .andExpect(jsonPath("$.success.status", is(putResponseMsg.getStatus())));
        verify(noteService, times(1)).updateNote(note.getId(), note, "testoken");
    }

    @Test
    public void getArticlesTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/notes").param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
    }

    @Test
    public void deleteArticleTest() throws Exception {
        // Arrange
        Note note = noteTestDataRepository.getArticles()
                .get("note-4");
        String jwtToken = "testToken";
        ResponseMsg deleteResponseMsg = new ResponseMsg(true, Constants.NOTE_DELETED_MESSAGE);

        doNothing().when(noteService).delete(note.getId(), jwtToken);

        // Act
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/notes/" + note.getId())
                        .header(Constants.AUTHORIZATION, jwtToken)
                        .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(
                        content().contentType(new MediaType("application", "json", Charset.forName("UTF-8"))))
                .andExpect(jsonPath("$.success.message", is(deleteResponseMsg.getMessage())))
                .andExpect(jsonPath("$.success.status", is(deleteResponseMsg.getStatus())));
        verify(noteService, times(1)).delete(note.getId(), jwtToken);
    }
    /**
     * Tear down.
     */
    @After
    public void tearDown() {

    }
}
