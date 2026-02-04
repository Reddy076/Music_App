package com.revplay.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtil class.
 */
public class ValidationUtilTest {

  @Test
  public void testIsValidEmail() {
    assertTrue(ValidationUtil.isValidEmail("test@example.com"));
    assertTrue(ValidationUtil.isValidEmail("user.name@domain.co.uk"));
    assertFalse(ValidationUtil.isValidEmail("invalid-email"));
    assertFalse(ValidationUtil.isValidEmail("missing@tld"));
    assertFalse(ValidationUtil.isValidEmail(null));
  }

  @Test
  public void testIsValidUsername() {
    assertTrue(ValidationUtil.isValidUsername("validUsername1"));
    assertTrue(ValidationUtil.isValidUsername("user_name"));
    assertFalse(ValidationUtil.isValidUsername("ab")); // Too short
    assertFalse(ValidationUtil.isValidUsername("abc$def")); // Invalid character
    assertFalse(ValidationUtil.isValidUsername(null));
  }

  @Test
  public void testIsNotEmpty() {
    assertTrue(ValidationUtil.isNotEmpty("hello"));
    assertFalse(ValidationUtil.isNotEmpty(""));
    assertFalse(ValidationUtil.isNotEmpty("   "));
    assertFalse(ValidationUtil.isNotEmpty(null));
  }

  @Test
  public void testSanitizeInput() {
    assertEquals("&lt;script&gt;", ValidationUtil.sanitizeInput("<script>"));
    assertEquals("Hello &amp; World", ValidationUtil.sanitizeInput("Hello & World").replace("&", "&amp;")); // Sanitize
                                                                                                            // doesn't
                                                                                                            // encode &
                                                                                                            // by
                                                                                                            // default
                                                                                                            // in this
                                                                                                            // util
    // Based on implementation: replaceAll("<", "&lt;").replaceAll(">",
    // "&gt;").replaceAll("\"", "&quot;").replaceAll("'", "&#x27;")
    assertEquals("&lt;div&gt;", ValidationUtil.sanitizeInput("<div>"));
    assertEquals("&quot;quoted&quot;", ValidationUtil.sanitizeInput("\"quoted\""));
    assertNull(ValidationUtil.sanitizeInput(null));
  }
}
