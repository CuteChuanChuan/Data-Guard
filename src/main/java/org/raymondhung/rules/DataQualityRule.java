package org.raymondhung.rules;

import org.jetbrains.annotations.NotNull;
import org.raymondhung.data.SourceData;
import org.raymondhung.results.ValidationResult;

public interface DataQualityRule {

  @NotNull
  String getName();

  @NotNull
  String getDescription();

  @NotNull
  ValidationResult validate(@NotNull SourceData data);
}
