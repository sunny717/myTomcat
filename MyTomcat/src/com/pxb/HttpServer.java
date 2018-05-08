/**    
* @Title: HttpServer.java  
* @Package com.pxb  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年9月18日 下午4:05:04  
* @version V1.0.0    
*/  
package com.pxb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**  
 * @ClassName: HttpServer  
 * @Description: web服务器  
 * @author panxiaobo  
 * @date 2017年9月18日 下午4:05:04  
 *    
 */
public class HttpServer {
	public static Logger log = LoggerFactory.getLogger("httpServer");
	 // 关闭命令 
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	 // 收到关闭命令置为true
	   private boolean shutdown = false; 
	
	public void start(int port){
		
		log.info("HttpServer start...");
		ServerSocket serverSocket=null;
		try {
			serverSocket=new ServerSocket(port,1,
			        InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			log.error("HttpServer start failed...",e);
		}
		 while (!shutdown) { 
		       Socket socketClient = null; 
		       InputStream input = null; 
		       OutputStream output = null; 
		       try { 
		         socketClient = serverSocket.accept(); 
		         input = socketClient.getInputStream();          
		         output = socketClient.getOutputStream(); 
		         // create Request object and parse 
		         Request request = new Request(input); 
		         request.parse(); 
		 
		         // create Response object 
		         Response response = new Response(output); 
		         response.setRequest(request); 
		 
		         // 检查这个请求是servlet请求还是请求静态文件 
		         //  servlet请求以"/servlet/"开始 
		         if (request.getUri().startsWith("/servlet/")) { 
		        	 ServletProcessor processor = new ServletProcessor(); 
//		           processor.process(request, response); 
		         } 
		         else { 
		           StaticResourceProcessor processor = 
		             new StaticResourceProcessor(); 
//		           processor.process(request, response); 
		         } 
		 
		          // Close the socket 
		         socketClient.close(); 
		         //check if the previous URI is a shutdown command 
		         shutdown = request.getUri().equals(SHUTDOWN_COMMAND); 
		       } 
		       catch (Exception e) { 
		         e.printStackTrace(); 
		         System.exit(1); 
		       } 
		     } 
	}

}
