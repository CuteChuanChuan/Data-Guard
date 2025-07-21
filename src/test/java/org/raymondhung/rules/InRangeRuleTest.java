package org.raymondhung.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.raymondhung.data.SourceData;
import org.raymondhung.results.ValidationResult;

class InRangeRuleTest {

  @Test
  @DisplayName("Should pass when in range and no null values")
  void shouldPassWhenInRangeAndNoNullValues() {
    SourceData mockedData = mock(SourceData.class);
    when(mockedData.getColumn("salary")).thenReturn(Arrays.asList(10000, 20000));

    InRangeRule inRangeRule = InRangeRule.compareIntegers("salary", 10000, 20000);
    ValidationResult validatedResult = inRangeRule.validate(mockedData);

    assertThat(validatedResult.passed()).isTrue();
    assertThat(validatedResult.message()).isEqualTo("All values in range [10000.00, 20000.00]");
    assertThat(validatedResult.failedRecords()).isEqualTo(0);
    assertThat(validatedResult.totalRecords()).isEqualTo(2);
    assertThat(validatedResult.getFailureRate()).isEqualTo(0.0);
    verify(mockedData).getColumn("salary");
  }

  @Test
  @DisplayName("Should pass when in range and null values")
  void shouldPassWhenInRangeAndNullValues() {
    SourceData mockedData = mock(SourceData.class);
    when(mockedData.getColumn("salary")).thenReturn(Arrays.asList(10000, null, 15000, 20000));

    InRangeRule inRangeRule = InRangeRule.compareIntegers("salary", 10000, 20000);
    ValidationResult validatedResult = inRangeRule.validate(mockedData);

    assertThat(validatedResult.passed()).isTrue();
    assertThat(validatedResult.message()).isEqualTo("All values in range [10000.00, 20000.00]");
    assertThat(validatedResult.failedRecords()).isEqualTo(0);
    assertThat(validatedResult.totalRecords()).isEqualTo(4);
    assertThat(validatedResult.getFailureRate()).isEqualTo(0.0);
  }

  @Test
  @DisplayName("Should fail when not in range and no null values")
  void shouldFailWhenNotInRangeAndNoNullValues() {
    SourceData mockedData = mock(SourceData.class);
    when(mockedData.getColumn("salary")).thenReturn(Arrays.asList(10000, 20001));

    InRangeRule inRangeRule = InRangeRule.compareIntegers("salary", 10000, 20000);
    ValidationResult validatedResult = inRangeRule.validate(mockedData);

    assertThat(validatedResult.passed()).isFalse();
    assertThat(validatedResult.message())
        .isEqualTo("Found 1 values out of range [10000.00, 20000.00]");
    assertThat(validatedResult.failedRecords()).isEqualTo(1);
    assertThat(validatedResult.totalRecords()).isEqualTo(2);
    assertThat(validatedResult.getFailureRate()).isEqualTo(0.5);
    verify(mockedData).getColumn("salary");
  }

  @Test
  @DisplayName("Should fail when not in range and null values")
  void shouldFailWhenNotInRangeAndNullValues() {
    SourceData mockedData = mock(SourceData.class);
    when(mockedData.getColumn("salary")).thenReturn(Arrays.asList(10000, null, 20001));

    InRangeRule inRangeRule = InRangeRule.compareIntegers("salary", 10000, 20000);
    ValidationResult validatedResult = inRangeRule.validate(mockedData);

    assertThat(validatedResult.passed()).isFalse();
    assertThat(validatedResult.message())
        .isEqualTo("Found 1 values out of range [10000.00, 20000.00]");
    assertThat(validatedResult.failedRecords()).isEqualTo(1);
    assertThat(validatedResult.totalRecords()).isEqualTo(3);
    assertThat(validatedResult.getFailureRate()).isCloseTo(0.33, within(0.01));
  }

  @Test
  @DisplayName("Should handle different numeric types")
  void shouldHandleDifferentNumericTypes() {
    SourceData mockedData = mock(SourceData.class);
    when(mockedData.getColumn("mixedType"))
        .thenReturn(Arrays.asList(85, 90L, 95.5f, 88.0, "92", new BigDecimal("84.3")));

    InRangeRule inRangeRule = InRangeRule.compareDoubles("mixedType", 80, 100);
    ValidationResult validatedResult = inRangeRule.validate(mockedData);

    assertThat(validatedResult.passed()).isTrue();
    assertThat(validatedResult.message()).isEqualTo("All values in range [80.00, 100.00]");
    assertThat(validatedResult.failedRecords()).isEqualTo(0);
    assertThat(validatedResult.totalRecords()).isEqualTo(6);
    assertThat(validatedResult.getFailureRate()).isEqualTo(0.0);
  }

  @Test
  @DisplayName("Should treat non-numeric as out of range")
  void shouldTreatNonNumericAsOutOfRanger() {
    SourceData mockedData = mock(SourceData.class);
    when(mockedData.getColumn("mixedType"))
        .thenReturn(Arrays.asList(85, "invalid_string", new Date(), 88, "92"));

    InRangeRule inRangeRule = InRangeRule.compareIntegers("mixedType", 80, 100);
    ValidationResult validatedResult = inRangeRule.validate(mockedData);

    assertThat(validatedResult.passed()).isFalse();
    assertThat(validatedResult.message()).isEqualTo("Found 2 values out of range [80.00, 100.00]");
    assertThat(validatedResult.failedRecords()).isEqualTo(2);
    assertThat(validatedResult.totalRecords()).isEqualTo(5);
    assertThat(validatedResult.getFailureRate()).isCloseTo(0.4, within(0.01));
  }

  @Test
  @DisplayName("Should reject invalid range")
  void shouldRejectInvalidRange() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          InRangeRule.compareIntegers("salary", 20000, 10000);
        });
  }

  @ParameterizedTest
  @DisplayName("Should handle boundary values")
  @CsvSource({
    "50, 50, 100, true",
    "100, 50, 100, true",
    "49, 50, 100, false",
    "101, 50, 100, false"
  })
  void shouldHandleBoundaryValues(int value, int min, int max, boolean expected) {
    SourceData mockedData = mock(SourceData.class);
    when(mockedData.getColumn("test")).thenReturn(Arrays.asList(value));

    InRangeRule inRangeRule = InRangeRule.compareIntegers("test", min, max);
    ValidationResult validatedResult = inRangeRule.validate(mockedData);

    assertThat(validatedResult.passed()).isEqualTo(expected);
  }

  @Test
  @DisplayName("Test factory method")
  void shouldCreateRulesWithFactoryMethod() {
    InRangeRule intRule = InRangeRule.compareIntegers("age", 0, 100);
    ;
    InRangeRule longRule = InRangeRule.compareLongs("population", 0L, 10000000L);
    InRangeRule floatRule = InRangeRule.compareFloats("temperature", 0.0f, 100.0f);
    InRangeRule doubleRule = InRangeRule.compareDoubles("gpa", 0.0, 4.0);
    InRangeRule decimalRule =
        InRangeRule.compareDecimals("price", new BigDecimal("0.00"), new BigDecimal("1000.00"));

    assertThat(intRule.getName()).isEqualTo("in_range_age");
    assertThat(longRule.getName()).isEqualTo("in_range_population");
    assertThat(floatRule.getName()).isEqualTo("in_range_temperature");
    assertThat(doubleRule.getName()).isEqualTo("in_range_gpa");
    assertThat(decimalRule.getName()).isEqualTo("in_range_price");
  }
}
