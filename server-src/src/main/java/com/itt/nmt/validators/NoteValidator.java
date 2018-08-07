package com.itt.nmt.validators;

import com.itt.nmt.models.Note;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * The Class NoteValidator.
 */
public class NoteValidator implements Validator {
    /* (non-Javadoc)
 * @see org.springframework.validation.Validator#supports(java.lang.Class)
 */
    @Override
    public boolean supports(final Class clazz) {

        return Note.class.equals(clazz);
    }

    /* (non-Javadoc)
     * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
     */
    @Override
    public void validate(final Object target, final Errors errors) {

        Note user = (Note) target;

        // do "complex" validation here

    }
}
