package org.raymondhung.results;

import org.jetbrains.annotations.NotNull;

public record ValidationResult(
    @NotNull String ruleName,
    boolean passed,
    @NotNull String message,
    long totalRecords,
    long failedRecords) {

  public ValidationResult(@NotNull String ruleName, boolean passed, @NotNull String message) {
    this(ruleName, passed, message, 0, 0);
  }

  @Override
  public @NotNull String toString() {
    return String.format(
        "Rule: %s, IsPassed: %s, Message: %s", this.ruleName, this.passed, this.message);
  }

  public double getFailureRate() {
    return totalRecords > 0 ? (double) failedRecords / totalRecords : 0.0;
  }
}
