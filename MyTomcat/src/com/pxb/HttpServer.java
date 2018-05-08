/**    
* @Title: HttpServer.java  
* @Package com.pxb  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��9��18�� ����4:05:04  
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
 * @Description: web������  
 * @author panxiaobo  
 * @date 2017��9��18�� ����4:05:04  
 *    
 */
public class HttpServer {
	public static Logger log = LoggerFactory.getLogger("httpServer");
	 // �ر����� 
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	 // �յ��ر�������Ϊtrue
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
		 
		         // ������������servlet����������̬�ļ� 
		         //  servlet������"/servlet/"��ʼ 
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
