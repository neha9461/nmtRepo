package com.itt.nmt.repositories;

import com.itt.nmt.models.Note;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


/**
 * NoteRepository interface, declares the methods exposed by the repository.
 * that supports pagination and sorting. Following default methods
 * are provided by PagingAndSortingRepository and can be used as is without requiring
 * any implementation. findAll(Sort), findAll(Pageable)
 */
public interface NoteRepository extends PagingAndSortingRepository<Note, String> {

/**
 * Implements a custom method that returns a pageable Note.
 * based on both the possible filters (creator/approver) of
 * note , with search on type,status and search key.
 * @param id id to be searched in createdBy user.
 * @param pageable object to get paginated Note list.
 * @return Note Object matching the search parameter.
 *
 * **/
    @Query("{'createdBy._id': ?0 }")
    Page<Note> findAll(ObjectId id, Pageable pageable);
}
