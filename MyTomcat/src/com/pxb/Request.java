/**    
 * @Title: Request.java  
 * @Package com.pxb  
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author panxiaobo    
 * @date 2017年9月18日 下午4:19:31  
 * @version V1.0.0    
 */
package com.pxb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

/**
 * @ClassName: Request
 * @Description: request请求
 * @author panxiaobo
 * @date 2017年9月18日 下午4:19:31
 * 
 */
public class Request implements ServletRequest {

	private InputStream input;
	private String uri;
	
	public  Request(InputStream input){
		this.input=input;
	}
	
	   private String parseUri(String requestString) { 
		     int index1, index2; 
		     index1 = requestString.indexOf(' '); 
		     if (index1 != -1) { 
		       index2 = requestString.indexOf(' ', index1 + 1); 
		       if (index2 > index1) 
		         return requestString.substring(index1 + 1, index2); 
		     } 
		     return null; 
		   } 
		 
		   public void parse() { 
		     // 从socket中读取数据
		     StringBuffer request = new StringBuffer(2048); 
		     int i; 
		     byte[] buffer = new byte[2048]; 
		     try { 
		       i = input.read(buffer); 
		     } 
		     catch (IOException e) { 
		       e.printStackTrace(); 
		       i = -1; 
		     } 
		     for (int j=0; j<i; j++) { 
		       request.append((char) buffer[j]); 
		     } 
		     HttpServer.log.info(request.toString()); 
		     uri = parseUri(request.toString()); 
		   } 
		   
    public String getUri() { 
			return uri; 
	} 
	@Override
	public Object getAttribute(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameter(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParameterValues(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRemotePort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeAttribute(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub

	}

}
