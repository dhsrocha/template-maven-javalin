package template.feature.user;

import static org.junit.jupiter.api.Assertions.assertThrows;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

final class UserTest {

  @ParameterizedTest
  @CsvSource({"some_name,0", "'',1"})
  @DisplayName("Should validate as invalid.")
  final void shouldValidateAsInvalid(final String name, final int age) {
    // Arrange
    assertThrows(IllegalArgumentException.class, () -> User.of(name, age));
  }

  @Test
  @DisplayName("Should order accordingly.")
  final void shouldOrderAccordingly() {
    // Arrange
    val u1 = User.of("name2", 2);
    val u2 = User.of("name1", 3);
    val u3 = User.of("name3", 1);
    // Assert / Act
    Assertions.assertEquals(-1, u1.compareTo(u2));
    Assertions.assertEquals(1, u2.compareTo(u3));
    Assertions.assertEquals(-1, u3.compareTo(u1));
  }
}
