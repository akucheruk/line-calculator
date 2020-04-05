package com.akucheruk.lineCalc.explorer;

import java.io.File;
import java.util.List;

public interface FileExplorer {
  List<File> exploreFiles(String path, String fileMask);
}
