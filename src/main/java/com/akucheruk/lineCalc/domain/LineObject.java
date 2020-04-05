package com.akucheruk.lineCalc.domain;

public class LineObject {
  private boolean hasCodeLine ;
  private boolean isCommendOpen;

  public LineObject() {
  }

  public LineObject(boolean hasCodeLine, boolean isCommendOpen) {
    this.hasCodeLine = hasCodeLine;
    this.isCommendOpen = isCommendOpen;
  }

  public boolean isHasCodeLine() {
    return hasCodeLine;
  }

  public void setHasCodeLine(boolean hasCodeLine) {
    this.hasCodeLine = hasCodeLine;
  }

  public boolean isCommendOpen() {
    return isCommendOpen;
  }

  public void setCommendOpen(boolean commendOpen) {
    isCommendOpen = commendOpen;
  }

  @Override
  public String toString() {
    return "LineObject{" +
        "hasCodeLine=" + hasCodeLine +
        ", isCommendOpen=" + isCommendOpen +
        '}';
  }
}
