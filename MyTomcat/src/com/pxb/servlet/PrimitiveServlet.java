/**    
* @Title: PrimitiveServlet.java  
* @Package com.pxb.servlet  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��9��19�� ����10:09:21  
* @version V1.0.0    
*/  
package com.pxb.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.pxb.HttpServer;

/**  
 * @ClassName: PrimitiveServlet  
 * @Description: �򵥵Ĳ���servlert  
 * @author panxiaobo  
 * @date 2017��9��19�� ����10:09:21  
 *    
 */
public class PrimitiveServlet implements Servlet{

	@Override
	public void destroy() {
		HttpServer.log.info("PrimitiveServlet destroy");
		
	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public String getServletInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(ServletConfig arg0) throws ServletException {
		HttpServer.log.info("PrimitiveServlet i");
		
	}

	@Override
	public void service(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		HttpServer.log.info("PrimitiveServlet start");
		response.getWriter().println("PrimitiveServlet service..."); 
		response.getWriter().print("PrimitiveServlet service end...");
	}

}
