
package com.itt.nmt.controllers;

import com.itt.nmt.models.Note;
import com.itt.nmt.request.models.NoteRequest;
import com.itt.nmt.response.models.ResponseMsg;
import com.itt.nmt.services.NoteService;
import com.itt.utility.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * This class is responsible for exposing REST APis for Note.
 */
@RestController
@RequestMapping("/notes")
public class NoteController {

    /**
     * Service implementation for DB entity that provides retrieval methods.
     */
    @Autowired
    private NoteService noteService;


    /**
     * REST API to add a new Note.
     *
     * @param noteRequest from which we can take note to be added
     * @param result the result
     * @return Success object
     */
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ModelMap addNote(@Valid @RequestBody
                                   final NoteRequest noteRequest, final BindingResult result) {

        Note note = noteRequest.getNote();
        String errorMsg = noteService.validateNote(note, result);
        if (errorMsg != null && !errorMsg.isEmpty()) {
            ResponseMsg postResponseMsg = new ResponseMsg(false, errorMsg);
            return new ModelMap().addAttribute("success", postResponseMsg);
        }
        note = noteService.save(note);

        ResponseMsg postResponseMsg = new ResponseMsg(true, Constants.NOTE_CREATED_MESSAGE);
        return new ModelMap().addAttribute("success", postResponseMsg);
    }

    /**
     * REST Interface for Note updation by id.
     *
     * @param id ID of the Note.
     * @param noteRequest  from which we can take note to be updated.
     * @param httpServletRequest servlet request.
     * @param result the result
     * @return Success object
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ModelMap updateNote(@PathVariable(value = "id")
                                      final String id, @Valid @RequestBody
                    final NoteRequest noteRequest, final BindingResult result,
                                  final HttpServletRequest httpServletRequest) {

        Note note = noteRequest.getNote();
        String errorMsg = noteService.validateNote(note, result);
        if (errorMsg != null && !errorMsg.isEmpty()) {
            ResponseMsg postResponseMsg = new ResponseMsg(false, errorMsg);
            return new ModelMap().addAttribute("success", postResponseMsg);
        }
        noteService.updateNote(id, note, httpServletRequest.getHeader(Constants.AUTHORIZATION));
        return new ModelMap().addAttribute("success", new ResponseMsg(true, Constants.DEFAULT_UPDATE_SUCCESS_MSG));
    }

    /**
     * REST Interface for Note retrieval by id.
     *
     * @param id ID of the Note.
     * @param httpServletRequest servlet request.
     * @return Note object that corresponds to Note id.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ModelMap getNoteById(@PathVariable(value = "id") final String id,
                                   final HttpServletRequest httpServletRequest) {
        return new ModelMap().addAttribute("note",
                noteService.getNoteById(id, httpServletRequest.getHeader(Constants.AUTHORIZATION)));
    }

    /**
     * REST Interface for Note retrieval by id.
     *
     * @param id ID of the Note.
     * @param httpServletRequest servlet request.
     * @return Note object that corresponds to Note id.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ModelMap deleteNoteById(@PathVariable(value = "id")
                                      final String id, final HttpServletRequest httpServletRequest) {

        String jwtToken = httpServletRequest.getHeader(Constants.AUTHORIZATION);
        noteService.delete(id, jwtToken);
        return new ModelMap().addAttribute("success", new ResponseMsg(true, Constants.NOTE_DELETED_MESSAGE));
    }

    /**
     * REST API for retrieval of Note list.
     *
     * @param httpServletRequest , It is a HttpServletRequest object.
     * @param pageablePage , It is a pageable object with default size of 10 elements.
     * @return Page<Note> objects.
     */
    @RequestMapping(method = RequestMethod.GET)
    public Page<Note> getNotes(final HttpServletRequest httpServletRequest,
                                  @PageableDefault(value = Constants.PAGE_SIZE)final Pageable pageablePage) {
        return noteService.getAll(pageablePage, httpServletRequest.getHeader(Constants.AUTHORIZATION));
    }

}
