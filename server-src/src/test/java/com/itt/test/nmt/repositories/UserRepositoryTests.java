
package com.itt.test.nmt.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.itt.nmt.models.User;
import com.itt.nmt.repositories.UserRepository;
import com.itt.test_category.RepositoryTests;
import com.itt.test_data.UserTestDataRepository;

/**
 * The Sample tests, Note that the test uses Arrange,Act, Assert pattern to
 * logically separate the parts of the test code. Note that @DataMongoTest will
 * cause the tests to run against an in-memory mongo instance.
 *
 * @author Neha Goyal
 */
@Category(RepositoryTests.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository repository;
    
    @Autowired
    private UserTestDataRepository userTestDataRepository;

    @Before
    public final void setUp() {

        repository.deleteAll();
        repository.save(userTestDataRepository.getUsers().get("user-1"));
        repository.save(userTestDataRepository.getUsers().get("user-2"));
    }

    @Test
    public final void onSaveSetId() {

        // Act
        User user = repository.save(userTestDataRepository.getUsers()
                                                       .get("user-3"));

        // Assert
        assertThat(user.getId()).isNotNull();

        // Cleanup
        repository.delete(user);
    }

    @Test
    public final void findByEmail() {

        // Arrange
        User user = userTestDataRepository.getUsers()
                                      .get("user-1");
        // Act
        User users = repository.findByEmailContainingIgnoreCase(user.getEmail());
        // Assert
        assertThat(users).isNotNull();
        assertThat(users.getEmail()).isEqualTo(user.getEmail());
    }
}
