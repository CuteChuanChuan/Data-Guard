package org.raymondhung.rules;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.raymondhung.data.SourceData;
import org.raymondhung.results.ValidationResult;

public class InRangeRule implements DataQualityRule {

  private final @NotNull String fieldName;

  private final double min;
  private final double max;

  public static InRangeRule compareIntegers(@NotNull String fieldName, int min, int max) {
    return new InRangeRule(fieldName, min, max);
  }

  public static InRangeRule compareLongs(@NotNull String fieldName, long min, long max) {
    return new InRangeRule(fieldName, min, max);
  }

  public static InRangeRule compareFloats(@NotNull String fieldName, float min, float max) {
    return new InRangeRule(fieldName, min, max);
  }

  public static InRangeRule compareDoubles(@NotNull String fieldName, double min, double max) {
    return new InRangeRule(fieldName, min, max);
  }

  public static InRangeRule compareDecimals(
      @NotNull String fieldName, BigDecimal min, BigDecimal max) {
    return new InRangeRule(fieldName, min.doubleValue(), max.doubleValue());
  }

  private InRangeRule(@NotNull String fieldName, double min, double max) {

    if (min > max) {
      throw new IllegalArgumentException("Minimum value must be less than maximum value");
    }

    this.fieldName = fieldName;
    this.min = min;
    this.max = max;
  }

  @Override
  public @NotNull String getName() {

    return "in_range_" + this.fieldName;
  }

  @Override
  public @NotNull String getDescription() {

    return String.format("Check that field '%s' is in range [%.2f, %.2f]", fieldName, min, max);
  }

  @Override
  public @NotNull ValidationResult validate(SourceData data) {
    final List<@Nullable Object> columnData = data.getColumn(this.fieldName);
    long totalRecords = columnData.size();

    long outOfRangeCount = countOutOfRange(columnData);
    boolean passed = outOfRangeCount == 0;
    String message =
        passed
            ? String.format("All values in range [%.2f, %.2f]", min, max)
            : String.format("Found %d values out of range [%.2f, %.2f]", outOfRangeCount, min, max);

    return new ValidationResult(this.getName(), passed, message, totalRecords, outOfRangeCount);
  }

  private long countOutOfRange(List<@Nullable Object> columnData) {
    return columnData.stream()
        .filter(Objects::nonNull)
        .mapToLong(this::isOutOfRange)
        .sum();
  }

  private long isOutOfRange(@NotNull Object value) {
    try {
      double numericValue = extractNumericValue(value);
      return (numericValue < min || numericValue > max) ? 1 : 0;
    } catch (NumberFormatException e) {
      return 1;
    }
  }

  private double extractNumericValue(@NotNull Object value) {
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    } else if (value instanceof String) {
      return Double.parseDouble((String) value);
    } else {
      throw new NumberFormatException(
          "Cannot convert " + value.getClass().getSimpleName() + " to numeric value");
    }
  }
}
