package com.akucheruk.lineCalc.domain;

import com.akucheruk.lineCalc.parser.ParsableFile;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


public class DirectoryTree<T extends ParsableFile> {
  private Node<T> root;

  public DirectoryTree(T root) {
    this.root = new Node<>();
    this.root.data = root;
    this.root.children = new ArrayList<>();
  }

  private Node<T> makeNode(Node<T> parentNode, T root) {
    Node node = new Node<>();
    node.data = root;
    node.parent = parentNode;
    node.children = new ArrayList<>();

    return node;
  }

  public class Node<T> {
    private T data;
    private Node<T> parent;
    private List<Node<T>> children;
  }

  public void addDirectories(List<T> directories) {
    if (directories == null || directories.isEmpty()) {
      return;
    }

    Node<T> rootNode = root;
    int rootFolderDeep = root.data.getDirectoryHierarchy().size();

    for (T dir : directories) {
      for (var curDeep = rootFolderDeep; curDeep < dir.getDirectoryHierarchy().size(); curDeep++) {
        String currentDir = dir.getDirectoryHierarchy().get(curDeep);

        if (rootNode.children == null || rootNode.children.isEmpty()) {
          rootNode.children = new ArrayList<>();
          rootNode.children.add(makeNode(rootNode, dir));
          continue;
        }

        boolean hasNeededDir = false;
        for (Node<T> node : rootNode.children) {
          if (node.data.getName().equals(currentDir)) {
            hasNeededDir = true;
            rootNode = node;
            break;
          }
        }

        if (!hasNeededDir) {
          rootNode.children.add(makeNode(rootNode, dir));
        }
      }
    }

  }

  public void addFiles(List<T> parsedFiles) {
    if (parsedFiles == null || parsedFiles.isEmpty()) {
      return;
    }

    int rootFolderDeep = root.data.getDirectoryHierarchy().size();
    for (T file : parsedFiles) {
      Node<T> rootNode = root;
      boolean foundFileDir = false;

      for (var curDeep = rootFolderDeep; curDeep <= file.getDirectoryHierarchy().size(); curDeep++) {
        if (!foundFileDir) {
          if (curDeep == file.getDirectoryHierarchy().size()) {
            rootNode.children.add(makeNode(rootNode, file));
            addLineForAllStructure(rootNode, file.getCodeLines());
            break;
          }

          String pathOfLocation = file.getDirectoryHierarchy().get(curDeep);
          for (Node<T> node : rootNode.children) {
            if (node.data.getName().equals(pathOfLocation)) {
              if (curDeep != file.getDirectoryHierarchy().size() - 1) {
                rootNode = node;
              } else {
                node.children.add(makeNode(node, file));
                addLineForAllStructure(node, file.getCodeLines());
                foundFileDir = true;
                break;
              }
            }
          }
        }
      }

    }
  }

  private void addLineForAllStructure(Node<T> node, long codeLines) {
    node.data.setCodeLines(node.data.getCodeLines() + codeLines);

    Node<T> parentNode = node.parent;
    if (parentNode != null) {
      addLineForAllStructure(parentNode, codeLines);
    }
  }

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner("\n");
    appendChildNode(joiner, root.data.getDirectoryHierarchy().size(), List.of(root));

    return joiner.toString();
  }

  private void appendChildNode(StringJoiner joiner, int basicDeep, List<Node<T>> children) {
    for (Node<T> child : children) {
      int spaces = child.data.isDirectory() || joiner.length() == 0
          ? child.data.getDirectoryHierarchy().size() - basicDeep
          : child.data.getDirectoryHierarchy().size() - basicDeep + 1;

      char[] spaceArr = new char[spaces];
      for (var i = 0; i < spaces; i++) {
        spaceArr[i] = ' ';
      }

      String spaceStr = String.copyValueOf(spaceArr);
      joiner.add(spaceStr + child.data.getName() + " : " + child.data.getCodeLines());

      if (!child.children.isEmpty()) {
        appendChildNode(joiner, basicDeep, child.children);
      }
    }
  }
}
