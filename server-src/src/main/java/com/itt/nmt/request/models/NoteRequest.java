package com.itt.nmt.request.models;

import com.itt.nmt.models.Note;

import javax.validation.Valid;

/**
 * The Class NoteRequest.
 */
public class NoteRequest {


    /** The note. */
    @Valid
    private Note note;

    /**
     * Instantiates a new note requst.
     */
    public NoteRequest() {

        super();
    }

    /**
     * Gets the note.
     *
     * @return the note
     */
    public Note getNote() {

        return note;
    }

    /**
     * Sets the note.
     *
     * @param note the new note
     */
    public void setNote(final Note note) {

        this.note = note;
    }
}
