/**    
* @Title: ServletProcessor.java  
* @Package com.pxb  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��9��18�� ����5:00:05  
* @version V1.0.0    
*/  
package com.pxb;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pxb.classLoader.MyClassLoader;
import com.pxb.connector.http.HttpRequest;
import com.pxb.connector.http.HttpRequestFacade;
import com.pxb.connector.http.HttpResponse;
import com.pxb.connector.http.HttpResponseFacade;

/**  
 * @ClassName: ServletProcessor  
 * @Description: Servlet������  
 * @author panxiaobo  
 * @date 2017��9��18�� ����5:00:05  
 *    
 */
public class ServletProcessor {
	
	public void process(HttpRequest request, HttpResponse response) { 
	     String uri = request.getRequestURI(); 
	     String servletName = uri.substring(uri.lastIndexOf("/") + 1); 
	     HttpRequestFacade requestFacade=new HttpRequestFacade(request);
	     HttpResponseFacade responseFacade=new HttpResponseFacade(response);
	     Class myClass = null; 
	     try { 
	       myClass = MyClassLoader.getInstance().loadClass("com.pxb.servlet."+servletName);  
	       Servlet servlet = (Servlet) myClass.newInstance(); 
	       servlet.service((HttpServletRequest) requestFacade, 
	        (HttpServletResponse) responseFacade);
	       response.finishResponse();
	     } 
	     catch (Exception e) { 
	    	 HttpServer.log.error("ServletProcessor.process failed",e);
	     } 
	 
	   } 
}
