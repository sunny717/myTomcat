/**    
 * @Title: MyAppException.java  
 * @Package com.pxb.exception  
 * @Description: TODO(��һ�仰�������ļ���ʲô)  
 * @author panxiaobo    
 * @date 2017��10��18�� ����6:02:53  
 * @version V1.0.0    
 */
package com.pxb.exception;

/**
 * @ClassName: MyAppException
 * @Description: �Զ����쳣
 * @author panxiaobo
 * @date 2017��10��18�� ����6:02:53
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
