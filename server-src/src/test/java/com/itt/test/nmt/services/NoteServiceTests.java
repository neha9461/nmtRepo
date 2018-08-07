package com.itt.test.nmt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.itt.nmt.models.Note;
import com.itt.nmt.models.User;
import com.itt.nmt.models.UserResponse;
import com.itt.nmt.repositories.NoteRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailException;
import org.springframework.test.context.junit4.SpringRunner;

import com.itt.nmt.services.NoteService;
import com.itt.nmt.services.UserService;
import com.itt.test_category.ServicesTests;
import com.itt.test_data.NoteTestDataRepository;
import com.itt.test_data.UserTestDataRepository;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;

@Category(ServicesTests.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class NoteServiceTests {


    /**
     * The article type test data repository.
     */
    @Autowired
    private NoteService noteService;

    /**
     * The User service.
     */
    @MockBean
    private UserService userService;

    /**
     * The article test data repository.
     */
    @Autowired
    private NoteTestDataRepository noteTestDataRepository;

    @Autowired
    private UserTestDataRepository userTestDataRepository;

    @MockBean
    private NoteRepository noteRepository;

    private static final String JWT_TEST_TOKEN = "jwtTestToken";

    @Before
    public final void setUp() {

    }

    @Test
    public final void save() {

        // Arrange
        Note note1 = noteTestDataRepository.getArticles()
                .get("note-8");

        // Arrange
        User user = userTestDataRepository.getUsers()
                .get("user-1");
        User userTwo = userTestDataRepository.getUsers()
                .get("user-2");

        when(noteRepository.save(note1)).thenReturn(note1);
        when(userService.getUserByID(user.getId())).thenReturn(user);
        when(userService.getUserByID(userTwo.getId())).thenReturn(userTwo);
        // Act
        Note note = noteService.save(note1);

        // Assert
        assertEquals(note.getTitle(), note.getTitle());
        assertEquals(note.getDescription(), note1.getDescription());
        assertEquals(note.getCreatedBy(), note1.getCreatedBy());
        assertEquals(note.getCreatedTime(), note1.getCreatedTime());
        verify(noteRepository, times(1)).save(note1);
    }

    @Test
    public final void onSaveTestMailFailure() throws MailException, InterruptedException {

        // Arrange
        Note note = noteTestDataRepository.getArticles()
                .get("note-9");

        // Arrange
        User user = userTestDataRepository.getUsers()
                .get("user-1");
        UserResponse userResponse = new UserResponse(user.getId(), user.getFirstName(),
                user.getLastName(), user.getEmail());
        when(noteRepository.save(note)).thenReturn(note);
        // Act
        Note noteSaved = noteService.save(note);

        // Assert
        assertEquals(note.getTitle(), noteSaved.getTitle());
        assertEquals(note.getDescription(), note.getDescription());
        assertEquals(note.getCreatedTime(), note.getCreatedTime());
        verify(noteRepository, times(1)).save(note);
    }

    @Test(expected = Exception.class)
    public void getArticleByIdUnAuthorizedExceptionTest() throws Exception {
        Note note = noteTestDataRepository.getArticles().get("note-4");
        User user = userTestDataRepository.getUsers()
                .get("user-3");
        when(noteRepository.findOne(note.getId())).thenReturn(note);
        when(userService.getLoggedInUser(JWT_TEST_TOKEN)).thenReturn(user);
        when(userService.getUserByID(user.getId())).thenReturn(user);
        Note noteRetrived = noteService.getNoteById(note.getId(), JWT_TEST_TOKEN);
        // Assert
        assertThat(note.getId()).isEqualTo(noteRetrived.getId());
        verify(noteRepository, times(1)).findOne(note.getId());
    }

    @Test(expected = Exception.class)
    public void getArticleByIdTestInvalidId() throws Exception {
        Note note = noteTestDataRepository.getArticles().get("note-1");
        User user = userTestDataRepository.getUsers()
                .get("user-3");
        when(noteRepository.findOne(note.getId())).thenReturn(null);
        when(userService.getLoggedInUser(JWT_TEST_TOKEN)).thenReturn(user);
        noteService.getNoteById(note.getId(), JWT_TEST_TOKEN);
    }

    @Test
    public void updateArticleTest() {
        Note note = noteTestDataRepository.getArticles().get("note-1");
        User user = userTestDataRepository.getUsers()
                .get("user-4");
        Note updateNote = note;
        updateNote.setTitle(note.getTitle() + "test");
        when(noteRepository.findOne(note.getId())).thenReturn(note);
        when(noteRepository.save(updateNote)).thenReturn(updateNote);
        when(userService.getLoggedInUser(JWT_TEST_TOKEN)).thenReturn(user);
        Note updatedNote = noteService.updateNote(note.getId(), updateNote, JWT_TEST_TOKEN);
        assertEquals(updatedNote.getTitle(), updateNote.getTitle());
        assertThat(updatedNote.getTitle()).isEqualTo(updateNote.getTitle());
        verify(noteRepository, times(1)).findOne(note.getId());
    }

    @Test(expected = Exception.class)
    public void updateArticleTestInvalidID() {
        User user = userTestDataRepository.getUsers()
                .get("user-5");
        Note note = noteTestDataRepository.getArticles().get("note-1");
        when(noteRepository.findOne(note.getId())).thenReturn(null);
        when(userService.getLoggedInUser(JWT_TEST_TOKEN)).thenReturn(user);
        noteService.updateNote(note.getId(), note, JWT_TEST_TOKEN);
    }

    @Test
    public void deleteArticleByIdTest() throws Exception {

        User user = userTestDataRepository.getUsers()
                .get("user-5");
        Note note = noteTestDataRepository.getArticles().get("note-4");

        when(userService.getLoggedInUser(JWT_TEST_TOKEN)).thenReturn(user);
        when(noteRepository.findOne(note.getId())).thenReturn(note);

        doNothing().when(noteRepository).delete(note.getId());
        noteService.delete(note.getId(), JWT_TEST_TOKEN);

        // Assert
        verify(noteRepository, times(1)).delete(note.getId());
    }

    @Test(expected = Exception.class)
    public void deleteArticleExceptionTest() {

        User user = userTestDataRepository.getUsers()
                .get("user-3");
        Note note = noteTestDataRepository.getArticles().get("note-4");

        when(userService.getLoggedInUser(JWT_TEST_TOKEN)).thenReturn(user);
        when(noteRepository.findOne(note.getId())).thenReturn(note);
        doNothing().when(noteRepository).delete(note.getId());
        noteService.delete(note.getId(), JWT_TEST_TOKEN);

        // Assert
        verify(noteRepository, times(1)).delete(note.getId());
    }

    @Test
    public void validateNoteTest() {
        Note note = noteTestDataRepository.getArticles().get("note-4");
        BindingResult result = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("error detail", "error detail", "error detail");
        FieldError fieldErrorOne = new FieldError("error string", "error string", "error string");
        List<FieldError> errors = Arrays.asList(fieldError, fieldErrorOne);
        when(result.hasErrors()).thenReturn(true);
        when(result.getFieldErrors()).thenReturn(errors);
        String errorMsg = noteService.validateNote(note, result);
        assertNotNull(errorMsg);
    }

}
