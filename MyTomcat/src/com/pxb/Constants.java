/**    
* @Title: Constants.java  
* @Package com.pxb  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年9月19日 上午10:02:31  
* @version V1.0.0    
*/  
package com.pxb;

import java.io.File;

/**  
 * @ClassName: Constants  
 * @Description: 常量类  
 * @author panxiaobo  
 * @date 2017年9月19日 上午10:02:31  
 *    
 */
public class Constants {
	public final static String WEBROOT=System.getProperty("user.dir")+File.separator+"webroot";
	public static final String Package = "com.pxb.connector.http";
	public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
	public static final int PROCESSOR_IDLE = 0;
	 public static final int PROCESSOR_ACTIVE = 1;

}
