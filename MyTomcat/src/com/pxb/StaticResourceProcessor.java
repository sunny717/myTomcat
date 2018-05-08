/**    
* @Title: StaticResourceProcessor.java  
* @Package com.pxb  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��9��18�� ����6:02:53  
* @version V1.0.0    
*/  
package com.pxb;

import java.io.IOException;

import com.pxb.connector.http.HttpRequest;
import com.pxb.connector.http.HttpResponse;

/**  
 * @ClassName: StaticResourceProcessor  
 * @Description: ��̬��Դ������ 
 * @author panxiaobo  
 * @date 2017��9��18�� ����6:02:53  
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
