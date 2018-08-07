package com.itt.nmt.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.itt.nmt.models.User;
/**
 * UserRepostory interface, declares the methods exposed by the repository.
 * Following default methods are provided by CrudRepository and can be used as
 * is without requiring any implementation. save, findOne, exists, findAll,
 * count, delete and deleteAll. Please refer to the javadocs for more details.
 */
public interface UserRepository extends PagingAndSortingRepository<User, String> {
    /**
     * Finds a User object that matches the email parameter. Spring
     * automatically formulates appropriate query depending on the name of the
     * method. findByXXX() method would look for a match for XXX property and
     * return the object instance.
     *
     * @param email of the User
     * @return User Object matching the email parameter
     */
   User findByEmailContainingIgnoreCase(String email);
   /**
    * Finds list of User object that matches the parameters passed. Spring
    * automatically formulates appropriate query depending on the name of the
    * method. findByXXX() method would look for a match for XXX property and
    * return the object instance.
    *
    * @param emailOfLoggedInUser email of the user logged in.
    * @param page Page consisting of users.
    * @return Page<User> page of users.
    */
   @Query("{'email': { $ne: ?0 } }")
   Page<User> findAll(String emailOfLoggedInUser, Pageable page);
   /**
    * Finds list of User object that matches the parameters passed. Spring
    * automatically formulates appropriate query depending on the name of the
    * method. findByXXX() method would look for a match for XXX property and
    * return the object instance.
    *
    * @param active status of user
    * @param emailOfLoggedInUser email of the user logged in.
    * @param page Page consisting of users.
    * @return Page<User> page of users.
    */
   @Query("{'email': { $ne: ?1 }, 'active': ?0 }")
   Page<User> findByActive(boolean active,  String emailOfLoggedInUser, Pageable page);

}
