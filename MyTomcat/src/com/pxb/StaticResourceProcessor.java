/**    
* @Title: StaticResourceProcessor.java  
* @Package com.pxb  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年9月18日 下午6:02:53  
* @version V1.0.0    
*/  
package com.pxb;

import java.io.IOException;

import com.pxb.connector.http.HttpRequest;
import com.pxb.connector.http.HttpResponse;

/**  
 * @ClassName: StaticResourceProcessor  
 * @Description: 静态资源处理类 
 * @author panxiaobo  
 * @date 2017年9月18日 下午6:02:53  
 *    
 */
public class StaticResourceProcessor {
	 public void process(HttpRequest request, HttpResponse response) { 
	     try { 
	       response.sendStaticResource(); 
	     } 
	     catch (IOException e) { 
	       e.printStackTrace(); 
	     } 
	   } 
}
