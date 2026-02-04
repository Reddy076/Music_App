package com.revplay.service;

import com.revplay.dao.ArtistDAO;
import com.revplay.dao.UserDAO;
import com.revplay.model.User;
import com.revplay.model.User.UserRole;
import com.revplay.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserDAO userDAO;

  @Mock
  private ArtistDAO artistDAO;

  private UserService userService;

  @BeforeEach
  public void setUp() {
    userService = new UserService(userDAO, artistDAO);
  }

  @Test
  public void testLoginSuccess() throws Exception {
    String email = "test@example.com";
    String password = "Password123";
    String encodedPassword = PasswordUtil.hashPassword(password);

    User mockUser = new User();
    mockUser.setUserId(1);
    mockUser.setEmail(email);
    mockUser.setPassword(encodedPassword);
    mockUser.setActive(true);

    when(userDAO.findByEmail(email)).thenReturn(Optional.of(mockUser));

    User result = userService.login(email, password);

    assertNotNull(result);
    assertEquals(email, result.getEmail());
    verify(userDAO).findByEmail(email);
  }

  @Test
  public void testLoginFailure_InvalidEmail() {
    assertThrows(IllegalArgumentException.class, () -> userService.login("invalid-email", "pass"));
  }

  @Test
  public void testLoginFailure_UserNotFound() throws Exception {
    when(userDAO.findByEmail(anyString())).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class, () -> userService.login("test@example.com", "pass"));
  }

  @Test
  public void testLoginFailure_WrongPassword() throws Exception {
    String email = "test@example.com";
    String password = "Password123";
    String wrongPassword = "WrongPassword123";
    String encodedPassword = PasswordUtil.hashPassword(password);

    User mockUser = new User();
    mockUser.setEmail(email);
    mockUser.setPassword(encodedPassword);
    mockUser.setActive(true);

    when(userDAO.findByEmail(email)).thenReturn(Optional.of(mockUser));

    assertThrows(IllegalArgumentException.class, () -> userService.login(email, wrongPassword));
  }

  @Test
  public void testRegisterSuccess() throws Exception {
    String email = "new@example.com";
    String password = "Password123";
    String username = "newuser";
    UserRole role = UserRole.USER;
    String securityQuestion = "Q";
    String securityAnswer = "A";

    when(userDAO.emailExists(email)).thenReturn(false);
    when(userDAO.usernameExists(username)).thenReturn(false);
    when(userDAO.create(any(User.class))).thenReturn(1);

    User result = userService.register(email, password, username, role, securityQuestion, securityAnswer);

    assertNotNull(result);
    assertEquals(1, result.getUserId());
    assertEquals(email, result.getEmail());
    verify(userDAO).create(any(User.class));
  }

  @Test
  public void testRegisterFailure_DuplicateEmail() throws Exception {
    String email = "existing@example.com";
    when(userDAO.emailExists(email)).thenReturn(true);

    assertThrows(IllegalArgumentException.class,
        () -> userService.register(email, "Password123", "user", UserRole.USER, "Q", "A"));
  }
}
