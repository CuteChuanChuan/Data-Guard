package org.raymondhung.data;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface SourceData {

  @NotNull
  List<Object> getColumn(@NotNull String columnName);
}
