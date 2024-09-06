package com.allen.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author allen
 */
@Getter
@Setter
public class AssertException extends RuntimeException {
  private static final long serialVersionUID = -6340064885473963714L;

  private String msg;

  /** -- GETTER -- 获取数据 */
  private Object data;

  private Integer errorCode;

  public AssertException(final String message) {
    super(message);
    this.msg = message;
  }

  public AssertException(final String message, final Throwable cause) {
    super(message, cause);
    this.msg = message;
  }

  /**
   * 设置给前端的消息
   *
   * @param msg
   */
  public AssertException setMsg(final String msg) {
    this.msg = msg;
    return this;
  }

  /**
   * 设置给前端的数据
   *
   * @param data
   */
  public AssertException setData(final Object data) {
    this.data = data;
    return this;
  }

  /**
   * 设置错误代码
   *
   * @param errorCode
   * @return
   */
  public AssertException setErrorCode(final Integer errorCode) {
    this.errorCode = errorCode;
    return this;
  }
}
