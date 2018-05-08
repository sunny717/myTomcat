/**    
* @Title: HttpRequestFacade.java  
* @Package com.pxb.connector  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年10月17日 下午3:46:36  
* @version V1.0.0    
*/  
package com.pxb.connector.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**  
 * @ClassName: HttpRequestFacade  
 * @Description: HttpRequest外观类  
 * @author panxiaobo  
 * @date 2017年10月17日 下午3:46:36  
 *    
 */
public class HttpRequestFacade implements HttpServletRequest{
	
	private HttpServletRequest httpRequest;
	
	public HttpRequestFacade(HttpRequest httpRequest){
		this.httpRequest=httpRequest;
	}

	@Override
	public Object getAttribute(String arg0) {
		return httpRequest.getAttribute(arg0);
	}

	@Override
	public Enumeration getAttributeNames() {
		return httpRequest.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return httpRequest.getCharacterEncoding();
	}

	@Override
	public int getContentLength() {
		return httpRequest.getContentLength();
	}

	@Override
	public String getContentType() {
		return httpRequest.getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return httpRequest.getInputStream();
	}

	@Override
	public String getLocalAddr() {
		return httpRequest.getLocalAddr();
	}

	@Override
	public String getLocalName() {
		return httpRequest.getLocalName();
	}

	@Override
	public int getLocalPort() {
		return httpRequest.getLocalPort();
	}

	@Override
	public Locale getLocale() {
		return httpRequest.getLocale();
	}

	@Override
	public Enumeration getLocales() {
		return httpRequest.getLocales();
	}

	@Override
	public String getParameter(String arg0) {
		return httpRequest.getParameter(arg0);
	}

	@Override
	public Map getParameterMap() {
		return httpRequest.getParameterMap();
	}

	@Override
	public Enumeration getParameterNames() {
		return httpRequest.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return httpRequest.getParameterValues(arg0);
	}

	@Override
	public String getProtocol() {
		return httpRequest.getProtocol();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return httpRequest.getReader();
	}

	@Override
	public String getRealPath(String arg0) {
		return httpRequest.getRealPath(arg0);
	}

	@Override
	public String getRemoteAddr() {
		return httpRequest.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return httpRequest.getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return httpRequest.getRemotePort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return httpRequest.getRequestDispatcher(arg0);
	}

	@Override
	public String getScheme() {
		return httpRequest.getScheme();
	}

	@Override
	public String getServerName() {
		return httpRequest.getServerName();
	}

	@Override
	public int getServerPort() {
		return httpRequest.getServerPort();
	}

	@Override
	public boolean isSecure() {
		return httpRequest.isSecure();
	}

	@Override
	public void removeAttribute(String arg0) {
		httpRequest.removeAttribute(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		httpRequest.setAttribute(arg0, arg1);
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		httpRequest.setCharacterEncoding(arg0);
	}

	@Override
	public String getAuthType() {
		return httpRequest.getAuthType();
	}

	@Override
	public String getContextPath() {
		return httpRequest.getContextPath();
	}

	@Override
	public Cookie[] getCookies() {
		return httpRequest.getCookies();
	}

	@Override
	public long getDateHeader(String arg0) {
		return httpRequest.getDateHeader(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		return httpRequest.getHeader(arg0);
	}

	@Override
	public Enumeration getHeaderNames() {
		return httpRequest.getHeaderNames();
	}

	@Override
	public Enumeration getHeaders(String arg0) {
		return httpRequest.getHeaders(arg0);
	}

	@Override
	public int getIntHeader(String arg0) {
		return httpRequest.getIntHeader(arg0);
	}

	@Override
	public String getMethod() {
		return httpRequest.getMethod();
	}

	@Override
	public String getPathInfo() {
		return httpRequest.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return httpRequest.getPathTranslated();
	}

	@Override
	public String getQueryString() {
		return httpRequest.getQueryString();
	}

	@Override
	public String getRemoteUser() {
		return httpRequest.getRemoteUser();
	}

	@Override
	public String getRequestURI() {
		return httpRequest.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return httpRequest.getRequestURL();
	}

	@Override
	public String getRequestedSessionId() {
		return httpRequest.getRequestedSessionId();
	}

	@Override
	public String getServletPath() {
		return httpRequest.getServletPath();
	}

	@Override
	public HttpSession getSession() {
		return httpRequest.getSession();
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		return httpRequest.getSession(arg0);
	}

	@Override
	public Principal getUserPrincipal() {
		return httpRequest.getUserPrincipal();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return httpRequest.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return httpRequest.isRequestedSessionIdFromURL();
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return httpRequest.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return httpRequest.isRequestedSessionIdValid();
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return httpRequest.isUserInRole(arg0);
	}

}
