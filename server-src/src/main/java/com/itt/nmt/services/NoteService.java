package com.itt.nmt.services;

import java.util.List;
import java.util.Map;

import com.itt.nmt.models.Note;
import com.itt.nmt.validators.NoteValidator;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itt.nmt.jwt.exception.UnauthorizedException;
import com.itt.nmt.models.User;
import com.itt.nmt.models.UserResponse;
import com.itt.nmt.repositories.NoteRepository;

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
public class NoteService {
    /**
     * Instance of the basic Repository implementation.
     */
    @Autowired
    private NoteRepository noteRepository;

    /**
     * Instance of user service.
     */
    @Autowired
    private UserService userService;
    /**
     * Constant for id.
     */
    private static final String ID = "id";

    /**
     * Constant for note not found comment.
     */
    private static final String NOTE_NOT_FOUND = "Note not found";


    /** Description length for showing in knowledge base search. */
    @Value("${description.length}")
    private Integer descriptionLength;


    /**
     * Saves the Note to database.
     *
     * @param note
     *            Note object to be saved
     * @return Note object with userResponse included.
     */
    public Note save(final Note note) {

        if (note.getCreatedBy() != null) {
            User createdByUser = userService.getUserByID(note.getCreatedBy().toString());
            note.setCreatedBy(convertUserIntoUserResponse(createdByUser));
        }
        Note savedNote = noteRepository.save(note);
        return savedNote;
    }

    /**
     * Convert user object into note response user format.
     *
     * @param user
     *            User object to be converted
     * @return note user response details.
     */
    private UserResponse convertUserIntoUserResponse(final User user) {
        UserResponse userResponse = new UserResponse(user.getId(), user.getFirstName(), user.getLastName(),
                user.getEmail());
        return userResponse;
    }

    /**
     * updates the DBEntity(Note) from the database.
     *
     * @param id
     *            of Note to be updated.
     * @param updatedNote
     *            , Note object that needs to be updated.
     * @param token
     *            jwt token of the user.
     * @return Note
     */
    public Note updateNote(final String id, final Note updatedNote, final String token) {

        Note note = noteRepository.findOne(id);
        if (note == null) {
            log.error("Note with " + id + " Not found", NoteService.class);
            throw new RuntimeException(NOTE_NOT_FOUND);
        }
        isAuthenticatedUser(token, note);
        return noteRepository.save(updateNote(note, updatedNote));
    }

    /**
     * Gets the Note given the id.
     *
     * @param id
     *            ID of the Note.
     * @param token
     *            jwt token of the user.
     * @return Note object matching the id.
     */
    public Note getNoteById(final String id, final String token) {

        Note note = noteRepository.findOne(id);

        if (note == null) {
            log.error("Note with " + id + " Not found", NoteService.class);
            throw new RuntimeException(NOTE_NOT_FOUND);
        }

        isAuthenticatedUser(token, note);

        return note;
    }

    /**
     * Update the Note after validation.
     *
     * @param note
     *            object of the Note.
     * @param updatedNote
     *            object of the Note.
     * @return Note object matching the id.
     */
    private Note updateNote(final Note note, final Note updatedNote) {

        if (!updatedNote.getTitle().isEmpty()) {
            note.setTitle(updatedNote.getTitle());
        }
        if (!updatedNote.getDescription().isEmpty()) {
            note.setDescription(updatedNote.getDescription());
        }

        return note;
    }

    /**
     * Function to delete a note.
     *
     * @param noteID
     *            which needs to be deleted.
     * @param token
     *            jwt token of logged in user.
     */
    public void delete(final String noteID, final String token) {
        Note note = noteRepository.findOne(noteID);

        if (note == null) {
            log.error("Note with " + noteID + " Not found", NoteService.class);
            throw new RuntimeException(NOTE_NOT_FOUND);
        }
        isAuthenticatedUser(token, note);
        noteRepository.delete(noteID);

    }


    /**
     * private method to get notes based on search and filters.
     * @param page Pageable object.
     * @param token JWT token.
     * @return Page<Note> get list of notes.
     */

      public Page<Note> getAll(final Pageable page, final String token) {
          User loggedInUser = userService.getLoggedInUser(token);
          return noteRepository.findAll(new ObjectId(loggedInUser.getId()), page);
      }


    /**
     * private method to get notes based on search and filters.
     * @param token JWT token.
     * @param note Note object.
     */

    private void isAuthenticatedUser(final String token, final Note note) {
        User loggedInUser = userService.getLoggedInUser(token);

        Map<String, String> createdBy = new ObjectMapper().convertValue(note.getCreatedBy(), Map.class);

        if (!loggedInUser.getId().equals(createdBy.get(ID))) {
            log.error("User with id:" + loggedInUser.getId() + " don't have permission",
                    NoteService.class);
            throw new UnauthorizedException();
        }
    }


    /**
     * Validate Note.
     *
     * @param note the note
     * @param result the result
     * @return the string
     */
    public String validateNote(final Note note, final BindingResult result) {

        NoteValidator noteValidator = new NoteValidator();
        noteValidator.validate(note, result);
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
}
