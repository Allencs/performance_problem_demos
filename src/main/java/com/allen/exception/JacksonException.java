package com.allen.exception;

/**
 * @Author: allen @Date: 2024/3/4 19:17 @Description:
 */
public class JacksonException extends FormativeException {
  public JacksonException() {
    super();
  }

  public JacksonException(String message) {
    super(message);
  }

  public JacksonException(Throwable cause) {
    super(cause);
  }

  public JacksonException(String format, Object... arguments) {
    super(format, arguments);
  }
}
