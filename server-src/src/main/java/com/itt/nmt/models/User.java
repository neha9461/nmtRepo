package com.itt.nmt.models;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a User.
 * 
 * @author Neha Goyal
 */
@Data
@Getter
@Setter
public class User implements Serializable {
    /**
     * unique identifier.
     */
    @Id
    private String id;
    /**
     * firstname of the user.
     */
    @NotBlank
    private String firstName;
    /**
     * lastname of the user.
     */
    @NotBlank
    private String lastName;
    /**
     * email of the user.
     */
    @NotBlank
    @Email
    private String email;
    /**
     * joining date of the user.
     */
    private Date dateJoined;
    /**
     * password of the user.
     */
    private String password;
    /**
     * session of the user.
     */
    private boolean session = false;
}
