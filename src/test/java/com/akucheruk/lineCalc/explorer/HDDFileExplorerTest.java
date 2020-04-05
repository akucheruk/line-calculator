package com.akucheruk.lineCalc.explorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.akucheruk.lineCalc.exception.LineCalcRuntimeException;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

public class HDDFileExplorerTest {
  private static final String JAVA_MASK = ".java";

  private FileExplorer fileExplorer = new HDDFileExplorer();

  @Test
  public void exploreFilesWithNullPathTest() {
    Exception exception = assertThrows(LineCalcRuntimeException.class, () ->
        fileExplorer.exploreFiles(null, JAVA_MASK)
    );

    assertEquals( "File path must be not empty!", exception.getMessage());
  }

  @Test
  public void exploreFilesWithEmptyPathTest() {
    Exception exception = assertThrows(LineCalcRuntimeException.class, () ->
      fileExplorer.exploreFiles(" ", JAVA_MASK)
    );

    assertEquals( "File path must be not empty!", exception.getMessage());
  }

  @Test
  public void exploreFilesWithNullFileMaskTest() {
    Exception exception = assertThrows(LineCalcRuntimeException.class, () ->
      fileExplorer.exploreFiles("C:\\Developing",null)
    );

    assertEquals("File mask must be not empty!", exception.getMessage());
  }

  @Test
  public void exploreFilesWithEmptyFileMaskTest() {
    Exception exception = assertThrows(LineCalcRuntimeException.class, () ->
        fileExplorer.exploreFiles("C:\\Developing", " ")
    );

    assertEquals("File mask must be not empty!", exception.getMessage());
  }

  @Test
  public void exploreFilesWithJavaFilePathTest() {
    String path = getFilePath("test_data/rootDir/subRoot2/ClassWithLineComment.java");
    List<File> discoveredFiles = fileExplorer.exploreFiles(path, JAVA_MASK);

    assertEquals(1, discoveredFiles.size());
    assertTrue(discoveredFiles.get(0).isFile());
    assertEquals("ClassWithLineComment.java", discoveredFiles.get(0).getName());
  }

  @Test
  public void exploreFilesWithNotJavaFilePathTest() {
    Exception exception = assertThrows(LineCalcRuntimeException.class, () -> {
      String path = getFilePath("test_data/rootDir/subRoot2/ClassWithLineComment.txt");
      fileExplorer.exploreFiles(path, JAVA_MASK);
    });

    assertEquals("Not found any files with mask: .java", exception.getMessage());
  }

  @Test
  public void exploreFilesWithDirectoryPathTest() {
    String path = getFilePath("test_data/rootDir");
    List<File> discoveredFiles = fileExplorer.exploreFiles(path, JAVA_MASK);

    assertEquals(10, discoveredFiles.size());

    List<File> directories = discoveredFiles.stream().filter(File::isDirectory).collect(Collectors.toList());
    assertEquals(4, directories.size());

    List<File> files = discoveredFiles.stream().filter(File::isFile).collect(Collectors.toList());
    assertEquals(6, files.size());
  }

  private String getFilePath(String pathInResourcesDir) {
    return this.getClass().getClassLoader().getResource(pathInResourcesDir).getFile();
  }

}
