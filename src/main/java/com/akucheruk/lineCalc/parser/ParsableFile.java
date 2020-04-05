package com.akucheruk.lineCalc.parser;

import java.util.List;

public interface ParsableFile {
  String getName();

  boolean isDirectory();

  long getCodeLines();

  void setCodeLines(long lines);

  List<String> getDirectoryHierarchy();

}
