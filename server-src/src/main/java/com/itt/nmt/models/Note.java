package com.itt.nmt.models;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.util.Date;

/**
 * This class represents a Note.
 * 
 * @author Neha Goyal
 */
@Data
public class Note {


    /**
     * unique identifier.
     */
    @Id
    private String id;
    /**
     * version of the Note.
     */
    @Version 
    private Long version;
    /**
     * lastModified date and time of the Note.
     */
    @LastModifiedDate
    private Date lastModifiedTime;

    /**
     * created by user id  of the Note.
     */
    private Object createdBy;

    /**
     * created time of the Note.
     */
    @CreatedDate
    private Date createdTime;

    /**
     * name of the Note.
     */
    @NotBlank(message = "Title cannot be blank")
    private String title;

    /**
     * html content of the Note.
     */
    @NotBlank(message = "Description cannot be blank")
    private String description;

}
