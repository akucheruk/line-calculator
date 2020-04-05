package com.akucheruk.lineCalc;

import com.akucheruk.lineCalc.domain.DirectoryTree;
import com.akucheruk.lineCalc.domain.ParsedFile;
import com.akucheruk.lineCalc.exception.LineCalcRuntimeException;
import com.akucheruk.lineCalc.explorer.FileExplorer;
import com.akucheruk.lineCalc.explorer.HDDFileExplorer;
import com.akucheruk.lineCalc.parser.JavaCodeLineParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Please enter path to directory or file:\n");
        try {
            analyzePath(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void analyzePath(String path) {
        FileExplorer fileExplorer = new HDDFileExplorer();
        List<File> files = fileExplorer.exploreFiles(path, ".java");

        DirectoryTree<ParsedFile> directoryTree =  new JavaCodeLineParser().parse(files).orElseThrow(
            () -> new LineCalcRuntimeException("Error. Application didn't find any folders or files for with java code"));

        System.out.print(directoryTree.toString());
    }
}
