package com.engagetech.expenses.service;

import com.engagetech.expenses.model.User;
import com.engagetech.expenses.repository.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class DdUserServiceTest {

    private static final String USERNAME = "test1";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private DdUserService ddUserService;
    @Mock
    private UserRepository userRepository;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        ddUserService = new DdUserService(userRepository);
    }

    @Test
    public void givenIdWhenGetUserThenUser() throws Exception {
        // arrange
        User user = new User();
        user.setUsername(USERNAME);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // action
        final User user1 = ddUserService.getUser(1);

        // assert
        assertThat(user1.getUsername(), is(USERNAME));
    }

    @Test
    public void givenUnknownIdWhenGetUserThenUser() throws Exception {
        // arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // assert
        expectedException.expect(UserNotFoundException.class);

        // action
        final User user1 = ddUserService.getUser(1);
    }

    @Test
    public void givenUsernameWhenGetUserThenUser() throws Exception {
        // arrange
        User user = new User();
        user.setUsername(USERNAME);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        // action
        final Optional<User> user1 = ddUserService.getUser(USERNAME);

        // assert
        assertTrue(user1.isPresent());
        assertThat(user1.get().getUsername(), is(USERNAME));
    }
}
