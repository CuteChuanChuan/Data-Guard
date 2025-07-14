package org.raymondhung.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.raymondhung.data.SourceData;
import org.raymondhung.results.ValidationResult;

class NotNullRuleTest {

  @Test
  void shouldPassWhenNoNullValues() {
    SourceData mockedData = mock(SourceData.class);
    when(mockedData.getColumn("email"))
        .thenReturn(Arrays.asList("user1@test.com", "user2@test.com"));

    NotNullRule notNullRule = new NotNullRule("email");
    ValidationResult validatedResult = notNullRule.validate(mockedData);

    assertThat(validatedResult.passed()).isTrue();
    assertThat(validatedResult.message()).isEqualTo("No null values found");
    assertThat(validatedResult.failedRecords()).isEqualTo(0);
    assertThat(validatedResult.totalRecords()).isEqualTo(2);
    assertThat(validatedResult.getFailureRate()).isEqualTo(0.0);
    verify(mockedData).getColumn("email");
  }

  @Test
  void shouldFailWhenNullValues() {
    SourceData mockedData = mock(SourceData.class);
    when(mockedData.getColumn("email")).thenReturn(Arrays.asList("user1@test.com", null));

    NotNullRule notNullRule = new NotNullRule("email");
    ValidationResult validatedResult = notNullRule.validate(mockedData);

    assertThat(validatedResult.passed()).isFalse();
    assertThat(validatedResult.message()).isEqualTo("Found 1 null values");
    assertThat(validatedResult.failedRecords()).isEqualTo(1);
    assertThat(validatedResult.totalRecords()).isEqualTo(2);
    assertThat(validatedResult.getFailureRate()).isEqualTo(0.5);
    verify(mockedData).getColumn("email");
  }
}
