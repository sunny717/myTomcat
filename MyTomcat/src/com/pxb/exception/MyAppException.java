/**    
 * @Title: MyAppException.java  
 * @Package com.pxb.exception  
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author panxiaobo    
 * @date 2017年10月18日 下午6:02:53  
 * @version V1.0.0    
 */
package com.pxb.exception;

/**
 * @ClassName: MyAppException
 * @Description: 自定义异常
 * @author panxiaobo
 * @date 2017年10月18日 下午6:02:53
 * 
 */
public class MyAppException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -394798486453767197L;

	public MyAppException(String msg) {
		super(msg);
	}

	public MyAppException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
