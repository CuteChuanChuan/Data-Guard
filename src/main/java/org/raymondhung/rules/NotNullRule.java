package org.raymondhung.rules;

import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.raymondhung.data.SourceData;
import org.raymondhung.results.ValidationResult;

public class NotNullRule implements DataQualityRule {

  private final @NotNull String fieldName;

  public NotNullRule(@NotNull String fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public @NotNull String getName() {

    return "not_null_" + this.fieldName;
  }

  @Override
  public @NotNull String getDescription() {

    return "Check that field '" + this.fieldName + "' contains no null values";
  }

  @Override
  public @NotNull ValidationResult validate(SourceData data) {

    List<@Nullable Object> columnData = data.getColumn(this.fieldName);
    long totalRecords = columnData.size();
    long nullCount = columnData.stream().filter(Objects::isNull).count();
    boolean passed = nullCount == 0;
    String message =
        passed ? "No null values found" : String.format("Found %d null values", nullCount);

    return new ValidationResult(this.getName(), passed, message, totalRecords, nullCount);
  }
}
