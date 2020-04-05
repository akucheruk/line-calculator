package com.akucheruk.lineCalc.parser;

import com.akucheruk.lineCalc.domain.DirectoryTree;
import com.akucheruk.lineCalc.domain.ParsedFile;
import java.io.File;
import java.util.List;
import java.util.Optional;

public interface LineParser {
  Optional<DirectoryTree<ParsedFile>> parse(List<File> files);
}
