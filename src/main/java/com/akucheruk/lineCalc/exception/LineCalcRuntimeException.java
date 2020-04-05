package com.akucheruk.lineCalc.exception;

public class LineCalcRuntimeException extends RuntimeException {

  public LineCalcRuntimeException() {
    super();
  }

  public LineCalcRuntimeException(String message) {
    super(message);
  }

  public LineCalcRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public LineCalcRuntimeException(Throwable cause) {
    super(cause);
  }


}
