package com.revplay.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordUtil class.
 */
public class PasswordUtilTest {

  @Test
  public void testHashAndVerifyPassword() {
    String password = "Password123";
    String hash = PasswordUtil.hashPassword(password);

    assertNotNull(hash);
    assertTrue(hash.contains(":")); // Expects salt:hash format

    assertTrue(PasswordUtil.verifyPassword(password, hash));
    assertFalse(PasswordUtil.verifyPassword("WrongPassword123", hash));
  }

  @Test
  public void testIsValidPassword() {
    assertTrue(PasswordUtil.isValidPassword("StrongPass1")); // Has Upper, Lower, Digit, Min Length
    assertFalse(PasswordUtil.isValidPassword("weak")); // Too short
    assertFalse(PasswordUtil.isValidPassword("alllowercase1")); // Missing uppercase
    assertFalse(PasswordUtil.isValidPassword("ALLUPPERCASE1")); // Missing lowercase
    assertFalse(PasswordUtil.isValidPassword("NoDigitsHere")); // Missing digit
    assertFalse(PasswordUtil.isValidPassword(null));
  }

  @Test
  public void testHashConsistency() {
    // Hashing the same password twice should produce different results due to
    // random salt
    String password = "TestPassword1";
    String hash1 = PasswordUtil.hashPassword(password);
    String hash2 = PasswordUtil.hashPassword(password);

    assertNotEquals(hash1, hash2);
    assertTrue(PasswordUtil.verifyPassword(password, hash1));
    assertTrue(PasswordUtil.verifyPassword(password, hash2));
  }
}
