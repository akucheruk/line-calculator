package com.akucheruk.lineCalc.domain;

import com.akucheruk.lineCalc.parser.ParsableFile;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ParsedFile implements ParsableFile, Comparable<ParsedFile> {
  private static final String DOUBLE_SLASH = "\\\\";

  private String name;
  private boolean isDirectory;
  private long codeLines;

  private List<String> directoryHierarchy;

  public ParsedFile(File file, long codeLines) {
    String[] splitPath = file.getAbsolutePath().split(DOUBLE_SLASH);
    this.name = splitPath[splitPath.length-1];
    this.isDirectory = file.isDirectory();
    this.codeLines = codeLines;

    this.directoryHierarchy = this.isDirectory
        ? Arrays.asList(splitPath)
        : Arrays.asList(Arrays.copyOfRange(splitPath, 0, splitPath.length-1));
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isDirectory() {
    return isDirectory;
  }

  @Override
  public long getCodeLines() {
    return codeLines;
  }

  @Override
  public void setCodeLines(long lines) {
    this.codeLines = lines;
  }

  @Override
  public List<String> getDirectoryHierarchy() {
    return directoryHierarchy;
  }

  @Override
  public int compareTo(ParsedFile otherObj) {
    return Integer
        .compare(this.getDirectoryHierarchy().size(), otherObj.getDirectoryHierarchy().size());
  }

  @Override
  public String toString() {
    return "ParsedFile{" +
        "name='" + name + '\'' +
        ", isDirectory=" + isDirectory +
        ", codeLines=" + codeLines +
        ", directoryHierarchy=" + directoryHierarchy +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParsedFile that = (ParsedFile) o;
    return isDirectory == that.isDirectory &&
        Objects.equals(name, that.name) &&
        Objects.equals(directoryHierarchy, that.directoryHierarchy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, isDirectory, directoryHierarchy);
  }
}
