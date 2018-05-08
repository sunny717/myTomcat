/**    
* @Title: HttpProcessor.java  
* @Package com.pxb.processor  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年10月17日 上午11:31:05  
* @version V1.0.0    
*/  
package com.pxb.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.servlet.http.Cookie;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pxb.ServletProcessor;
import com.pxb.StaticResourceProcessor;
import com.pxb.connector.http.HttpConnector;
import com.pxb.connector.http.HttpHeader;
import com.pxb.connector.http.HttpRequest;
import com.pxb.connector.http.HttpRequestLine;
import com.pxb.connector.http.HttpResponse;
import com.pxb.connector.http.SocketInputStream;
import com.pxb.exception.MyAppException;
import com.pxb.util.RequestUtil;

/**  
 * @ClassName: HttpProcessor  
 * @Description: http消息处理类 
 * @author panxiaobo  
 * @date 2017年10月17日 上午11:31:05  
 *    
 */
public class HttpProcessor implements Lifecycle, Runnable {
	
	private HttpConnector httpConnector;
	private HttpRequestLine requestLine = new HttpRequestLine();
	private HttpRequest request;
	private HttpResponse response;
	
	
	public static Logger log = LoggerFactory.getLogger("httpServer");
	 /**
     * 新的socket是否可用?
     */
    private boolean available = false;
    
    /**
     * The socket we are currently processing a request for.  This object
     * is used for inter-thread communication only.
     */
    private Socket socket = null;
    
    /**
     * The debugging detail level for this component.
     */
    private int debug = 0;
    /**
     * The shutdown signal to our background thread
     */
    private boolean stopped = false;
    /**
     * 同步对象
     */
	private Object threadSync=new Object();

	public HttpProcessor(HttpConnector httpConnector,int id){
		this.httpConnector=httpConnector;
	}
	
	public void process(Socket socket){
		SocketInputStream input=null;
		OutputStream output=null;
		try {
			input=new SocketInputStream(socket.getInputStream(),httpConnector.getBufferSize());
			output=socket.getOutputStream();
			request=new HttpRequest(input);
			response=new HttpResponse(output);
			response.setRequest(request);
			response.setHeader("Server", "pxb Servlet Container");
			this.paresRequest(input, output);
			this.parseHeaders(input);
			 if (request.getRequestURI().startsWith("/servlet/")) { 
	        	 ServletProcessor processor = new ServletProcessor(); 
	           processor.process(request, response); 
	         } 
	         else { 
	           StaticResourceProcessor processor = 
	             new StaticResourceProcessor(); 
	           processor.process(request, response); 
	         }
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	 /**
     * Process an incoming TCP/IP connection on the specified socket.  Any
     * exception that occurs during processing must be logged and swallowed.
     * <b>NOTE</b>:  This method is called from our Connector's thread.  We
     * must assign it to our own thread so that multiple simultaneous
     * requests can be handled.
     *
     * @param socket TCP socket to process
     */
	 public synchronized void assign(Socket socket) {

	        // Wait for the Processor to get the previous Socket
	        while (available) {
	            try {
	                wait();
	            } catch (InterruptedException e) {
	            }
	        }

	        // Store the newly available Socket and notify our thread
	        this.socket = socket;
	        available = true;
	        notifyAll();
	        log.debug(" An incoming request is being assigned");
	    }
	/**
	 * 解析请求
	 * @param input
	 * @param output
	 * @throws IOException 
	 */
	public void paresRequest(SocketInputStream input,OutputStream output) throws IOException{
		input.readRequestLine(requestLine);
		String method=new String(requestLine.method,0,requestLine.methodEnd);
		String uri=null;
		String queryString=null;
		String protocol=new String(requestLine.protocol,0,requestLine.protocolEnd);
		if(method.length()<1){
			throw new MyAppException("请求方法解析失败");
		}else if(requestLine.uriEnd<1){
			throw new MyAppException("uri解析失败");
		}
		int parametersPostion=requestLine.indexOf("?");
		if(parametersPostion>=0){//有请求参数
			queryString=new String(requestLine.uri,parametersPostion+1,requestLine.uriEnd
					-parametersPostion-1);
			uri=new String(requestLine.uri,0,parametersPostion);
		}else{//没有请求参数
			uri=new String(requestLine.uri,0,requestLine.uriEnd);
		}
		request.setQueryString(queryString);
		if(!uri.startsWith("/")){
			int pos=uri.indexOf("://");
			if(pos!=-1){
				pos=uri.indexOf("/",pos+3);
			}
			if(pos==-1){
				uri="";
			}else{
				uri=uri.substring(pos);
			}
		}
		String match=";jsessionid=";
		int semicolon=uri.indexOf(match);
		if(semicolon>=0){//uri中存在sessionId
			String rest=uri.substring(semicolon+match.length());
			int semicolon2=rest.indexOf(';');
			if(semicolon2>=0){
				request.setRequestedSessionId(rest.substring(0,semicolon2));
				rest=rest.substring(semicolon2);
			}else{
				request.setRequestedSessionId(rest);
				rest="";
			}
			request.setRequestedSessionURL(true);
			uri=uri.substring(0,semicolon)+rest;
		}else{//uri中不存在sesssionId,sessionId应该是在cookie中
			request.setRequestedSessionId(null);
			request.setRequestedSessionURL(false);
		}
		String normalizedUri=normalize(uri);
		request.setMethod(method);
		request.setProtocol(protocol);
		if(normalizedUri!=null){
			request.setRequestURI(normalizedUri);
		}else{
			request.setRequestURI(uri);
		}
		if(normalizedUri==null){
			throw new MyAppException("无效的uri");
		}
		
		
	}
	
	 /**
	   * Return a context-relative path, beginning with a "/", that represents
	   * the canonical version of the specified path after ".." and "." elements
	   * are resolved out.  If the specified path attempts to go outside the
	   * boundaries of the current context (i.e. too many ".." path elements
	   * are present), return <code>null</code> instead.
	   *
	   * @param path Path to be normalized
	   */
	  protected String normalize(String path) {
	    if (path == null)
	      return null;
	    // Create a place for the normalized path
	    String normalized = path;

	    // Normalize "/%7E" and "/%7e" at the beginning to "/~"
	    if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
	      normalized = "/~" + normalized.substring(4);

	    // Prevent encoding '%', '/', '.' and '\', which are special reserved
	    // characters
	    if ((normalized.indexOf("%25") >= 0)
	      || (normalized.indexOf("%2F") >= 0)
	      || (normalized.indexOf("%2E") >= 0)
	      || (normalized.indexOf("%5C") >= 0)
	      || (normalized.indexOf("%2f") >= 0)
	      || (normalized.indexOf("%2e") >= 0)
	      || (normalized.indexOf("%5c") >= 0)) {
	      return null;
	    }

	    if (normalized.equals("/."))
	      return "/";

	    // Normalize the slashes and add leading slash if necessary
	    if (normalized.indexOf('\\') >= 0)
	      normalized = normalized.replace('\\', '/');
	    if (!normalized.startsWith("/"))
	      normalized = "/" + normalized;

	    // Resolve occurrences of "//" in the normalized path
	    while (true) {
	      int index = normalized.indexOf("//");
	      if (index < 0)
	        break;
	      normalized = normalized.substring(0, index) +
	        normalized.substring(index + 1);
	    }

	    // Resolve occurrences of "/./" in the normalized path
	    while (true) {
	      int index = normalized.indexOf("/./");
	      if (index < 0)
	        break;
	      normalized = normalized.substring(0, index) +
	        normalized.substring(index + 2);
	    }

	    // Resolve occurrences of "/../" in the normalized path
	    while (true) {
	      int index = normalized.indexOf("/../");
	      if (index < 0)
	        break;
	      if (index == 0)
	        return (null);  // Trying to go outside our context
	      int index2 = normalized.lastIndexOf('/', index - 1);
	      normalized = normalized.substring(0, index2) +
	        normalized.substring(index + 3);
	    }

	    // Declare occurrences of "/..." (three or more dots) to be invalid
	    // (on some Windows platforms this walks the directory tree!!!)
	    if (normalized.indexOf("/...") >= 0)
	      return (null);

	    // Return the normalized path that we have completed
	    return (normalized);

	  }
	  /**
	   * 解析请求头
	   * @param input
	 * @throws IOException 
	   */
	  private void parseHeaders(SocketInputStream input) throws IOException{
		  while(true){
			  HttpHeader header=new HttpHeader();
			  input.readHeader(header);
			  if(header.nameEnd==0){
				  if(header.valueEnd==0){//所有的头都读完了，退出循环
					  return; 
				  }else{
					  throw new MyAppException("解析header失败");
				  }
			  }
			  String name=new String(header.name,0,header.nameEnd);
			  String value=new String(header.value,0,header.valueEnd);
			  request.addHeader(name, value);
			  if(name.equals("cookie")){
				  Cookie cookies[] = RequestUtil.parseCookieHeader(value);
				  for(Cookie cookie:cookies){
					  if(cookie.getName().equals("jsessionid")){
						  request.setRequestedSessionId(cookie.getValue());
						  request.setRequestedSessionURL(false);
						  request.setRequestedSessionCookie(true);
					  }
					  request.addCookie(cookie);
				  }
			  }else if(name.equals("content-length")){
				  int n=Integer.parseInt(value);
				  request.setContentLength(n);
			  }else if(name.equals("content-type")){
				  request.setContentType(value);
			  }
		  }
		  
	  }

	@Override
	public void run() {
		// Process requests until we receive a shutdown signal
        while (!stopped) {

            // Wait for the next socket to be assigned
            Socket socket = await();
            if (socket == null)
                continue;

            // Process the request from this socket
            try {
                process(socket);
            } catch (Throwable t) {
                log.error("process.invoke", t);
            }

            // Finish up this request
            httpConnector.recycle(this);

        }

        // Tell threadStop() we have shut ourselves down successfully
        synchronized (threadSync) {
            threadSync.notifyAll();
        }
		
	}

	
	/**
     * Await a newly assigned Socket from our Connector, or <code>null</code>
     * if we are supposed to shut down.
     */
    private synchronized Socket await() {

        // Wait for the Connector to provide a new Socket
        while (!available) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        // Notify the Connector that we have received this Socket
        Socket socket = this.socket;
        available = false;
        notifyAll();

        if ((debug >= 1) && (socket != null)){
            log.debug("  The incoming request has been awaited");
        }

        return (socket);

    }
	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() throws LifecycleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() throws LifecycleException {
		// TODO Auto-generated method stub
		
	}
}
