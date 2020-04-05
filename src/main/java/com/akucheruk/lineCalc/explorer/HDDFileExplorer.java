package com.akucheruk.lineCalc.explorer;

import com.akucheruk.lineCalc.exception.LineCalcRuntimeException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HDDFileExplorer implements FileExplorer {

  @Override
  public List<File> exploreFiles(String path, String fileMask) {
    if (path == null || path.trim().length() == 0) {
      throw new LineCalcRuntimeException("File path must be not empty!");
    }

    if (fileMask == null || fileMask.isBlank()) {
      throw new LineCalcRuntimeException("File mask must be not empty!");
    }

    return discoveryFilesInDirectory(new File(path), fileMask, new ArrayList<>());
  }

  private List<File> discoveryFilesInDirectory(File sourceFile, String fileMask, List<File> files) {
    if (files.isEmpty()) {
      if (sourceFile.isFile()) {
        files.add(
            getFile(sourceFile, fileMask)
                .orElseThrow(() -> new LineCalcRuntimeException("Not found any files with mask: " + fileMask))
        );
      } else {
        files.add(sourceFile);
      }
    }

    File[] includedFiles = sourceFile.listFiles();
    if (includedFiles == null) {
      return files;
    }

    for (File includedFile : includedFiles) {
      if (!includedFile.isDirectory()) {
        var name = includedFile.getName();
        if (name.endsWith(fileMask)) {
          files.add(includedFile);
        }
      } else {
        files.add(includedFile);
        discoveryFilesInDirectory(includedFile, fileMask, files);
      }
    }

    return files;
  }

  private Optional<File> getFile(File rootFile, String fileMask) {
    return rootFile.getName().endsWith(fileMask)
        ? Optional.of(rootFile)
        : Optional.empty();
  }
}
