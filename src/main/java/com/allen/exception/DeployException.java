package com.allen.exception;

/**
 * @Author: allen @Date: 2024/4/8 11:08 @Description:
 */
public class DeployException extends FormativeException {
  public DeployException(String format, Object... arguments) {
    super(format, arguments);
  }
}
