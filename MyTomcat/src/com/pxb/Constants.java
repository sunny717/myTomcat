/**    
* @Title: Constants.java  
* @Package com.pxb  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��9��19�� ����10:02:31  
* @version V1.0.0    
*/  
package com.pxb;

import java.io.File;

/**  
 * @ClassName: Constants  
 * @Description: ������  
 * @author panxiaobo  
 * @date 2017��9��19�� ����10:02:31  
 *    
 */
public class Constants {
	public final static String WEBROOT=System.getProperty("user.dir")+File.separator+"webroot";
	public static final String Package = "com.pxb.connector.http";
	public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
	public static final int PROCESSOR_IDLE = 0;
	 public static final int PROCESSOR_ACTIVE = 1;

}
