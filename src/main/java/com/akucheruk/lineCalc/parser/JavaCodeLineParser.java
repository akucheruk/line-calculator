package com.akucheruk.lineCalc.parser;

import com.akucheruk.lineCalc.exception.LineCalcRuntimeException;
import com.akucheruk.lineCalc.domain.DirectoryTree;
import com.akucheruk.lineCalc.domain.LineObject;
import com.akucheruk.lineCalc.domain.ParsedFile;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavaCodeLineParser implements LineParser {
  private static final String COMMENT_END = "*/";
  private static final String COMMENT_START = "/*";
  private static final String SIMPLE_COMMENT = "//";

  private static final char STAR_CHAR = '*';
  private static final char SLASH_CHAR = '/';

  @Override
  public Optional<DirectoryTree<ParsedFile>> parse(List<File> files) {
    if (files == null || files.isEmpty()) {
      return Optional.empty();
    }

    List<ParsedFile> parsedFiles = files.stream()
        .map(this::getParsedFile)
        .collect(Collectors.toList());

    return buildDirectoryTree(parsedFiles);
  }

  private Optional<DirectoryTree<ParsedFile>> buildDirectoryTree(List<ParsedFile> parsedFiles) {
    ParsedFile rootFile = getRootFile(parsedFiles);
    DirectoryTree<ParsedFile> directoryTree = new DirectoryTree<>(rootFile);
    parsedFiles.remove(rootFile);

    Map<Boolean, List<ParsedFile>> fileMap = parsedFiles.stream()
        .collect(Collectors.groupingBy(ParsedFile::isDirectory));

    List<ParsedFile> directories = fileMap.get(Boolean.TRUE);
    if (directories != null) {
      Collections.sort(directories);
      directoryTree.addDirectories(directories);
    }

    directoryTree.addFiles(fileMap.get(Boolean.FALSE));

    return Optional.of(directoryTree);
  }

  private ParsedFile getRootFile(List<ParsedFile> parsedFiles) {
    return (parsedFiles.size() == 1)
        ? parsedFiles.get(0)
        : parsedFiles.stream()
            .filter(ParsedFile::isDirectory)
            .min(Comparator.comparing(parsedFile -> parsedFile.getDirectoryHierarchy().size()))
            .orElseThrow(() -> new LineCalcRuntimeException("Filed to find root directory"));
  }

  private ParsedFile getParsedFile(File file) {
    if (file.isDirectory()) {
      return new ParsedFile(file, 0L);
    }

    StringBuilder textBuilder = new StringBuilder();
    try (Reader reader = new FileReader(file, Charset.forName(StandardCharsets.UTF_8.name()))) {
      var c = 0;
      while ((c = reader.read()) != -1) {
        textBuilder.append((char) c);
      }
    } catch (IOException ioe) {
      throw new LineCalcRuntimeException(String.format("Failed to parse file: [%s]. Error: ", file.getAbsolutePath()), ioe);
    }

    return new ParsedFile(file, calculateCodeLines(textBuilder.toString()));
  }

  private long calculateCodeLines(String fileBody) {
    String[] allLines = fileBody.split("\n");
    long codeLines = 0;
    boolean isCommendOpen = false;

    for (String line : allLines) {
      line = line.replaceAll("[\t\r\b\f]", "");
      LineObject lineObject = analyzeLine(line, false, isCommendOpen);
      isCommendOpen = lineObject.isCommendOpen();
      if (lineObject.isHasCodeLine()) {
        codeLines += 1;
      }
    }

    return codeLines;
  }

  private LineObject analyzeLine(String line, boolean hasCodeLine, boolean isCommendOpen) {
    if (hasCodeLine) {
      isCommendOpen = isCommendOpen
          ? !line.trim().endsWith(COMMENT_END)
          : line.trim().endsWith(COMMENT_START);

      return new LineObject(true, isCommendOpen);
    }

    LineObject result = new LineObject();
    result.setHasCodeLine(false);
    result.setCommendOpen(isCommendOpen);

    if (line != null && !line.isBlank()) {
      line = line.trim();

      if (isCommendOpen || !line.startsWith(SIMPLE_COMMENT)) {
        if (isCommendOpen || line.startsWith(COMMENT_START)) {

          if (line.length() >= 2) {
            String code = getCodeWithoutComment(line);
            result = code == null
                ? new LineObject(false, true)
                : analyzeLine(code, false, false);
          } else {
            result.setCommendOpen(true);
          }

        } else {
          return analyzeLine(line,true, false);
        }
      } else {
        result.setCommendOpen(false);
      }

    } else {
      result = new LineObject(false, isCommendOpen);
    }

    return result;
  }

  private String getCodeWithoutComment(String line) {
    boolean checkNextChar = false;
    boolean isClosedComment = false;
    int position;

    for (position = 0; position < line.length(); position++) {
      char ch = line.charAt(position);

      if (ch == STAR_CHAR) {
        checkNextChar = true;
      } else {
        if (checkNextChar) {
          if (ch == SLASH_CHAR) {
            position++;
            isClosedComment = true;
            break;
          } else {
            checkNextChar = false;
          }
        }
      }

    }

    return isClosedComment ? line.substring(position) : null;
  }
}
