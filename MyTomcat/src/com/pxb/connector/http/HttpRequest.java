/**    
 * @Title: HttpRequest.java  
 * @Package com.pxb.connector  
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author panxiaobo    
 * @date 2017年10月17日 下午3:32:01  
 * @version V1.0.0    
 */
package com.pxb.connector.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.util.Enumerator;

import com.pxb.connector.RequestStream;
import com.pxb.exception.MyAppException;
import com.pxb.util.HttpConstants;
import com.pxb.util.ParameterMap;
import com.pxb.util.RequestUtil;

/**
 * @ClassName: HttpRequest
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author panxiaobo
 * @date 2017年10月17日 下午3:32:01
 * 
 */
public class HttpRequest implements HttpServletRequest {

	private String queryString;
	private Map headers = new HashMap(); // 请求头
	private List cookies = new ArrayList(); // cookies
	private String requestedSessionId;
	private boolean requestedSessionURL;
	private boolean requestedSessionCookie;
	private String method;
	private String protocol;
	private String requestURI;
	private String contentType;
	private int contentLength;
	private boolean prased = false; // 是否已经解析过parameter
	protected ParameterMap parameters; // 参数
	private InputStream input;
	protected BufferedReader reader = null;
	protected ServletInputStream stream = null;
	protected static ArrayList empty = new ArrayList();
	/**
	 * The set of SimpleDateFormat formats to use in getDateHeader().
	 */
	protected SimpleDateFormat formats[] = {
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
			new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
			new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US) };
	protected String pathInfo = null;

	public HttpRequest(InputStream input) {
		this.input = input;
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
		return this.contentLength;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (reader != null)
			throw new IllegalStateException("getInputStream has been called");

		if (stream == null)
			stream = createInputStream();
		return (stream);
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
	public String getParameter(String name) {
		this.praseParameters();
		String values[] = (String[]) parameters.get(name);
		if (values != null)
			return (values[0]);
		else
			return null;
	}

	@Override
	public Map getParameterMap() {
		this.praseParameters();
		return this.parameters;
	}

	@Override
	public Enumeration getParameterNames() {
		this.praseParameters();
		return (new Enumerator(parameters.keySet()));
	}

	@Override
	public String[] getParameterValues(String arg0) {
		this.praseParameters();
		String values[] = (String[]) parameters.get(arg0);
		if (values != null)
			return (values);
		else
			return null;
	}

	@Override
	public String getProtocol() {
		return this.protocol;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if (stream != null)
			throw new IllegalStateException("getInputStream has been called.");
		if (reader == null) {
			String encoding = getCharacterEncoding();
			if (encoding == null)
				encoding = "ISO-8859-1";
			InputStreamReader isr = new InputStreamReader(createInputStream(),
					encoding);
			reader = new BufferedReader(isr);
		}
		return (reader);
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

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		synchronized (cookies) {
			if (cookies.size() < 1)
				return (null);
			Cookie results[] = new Cookie[cookies.size()];
			return ((Cookie[]) cookies.toArray(results));
		}
	}

	@Override
	public long getDateHeader(String arg0) {
		String value = getHeader(arg0);
		if (value == null)
			return (-1L);

		// Work around a bug in SimpleDateFormat in pre-JDK1.2b4
		// (Bug Parade bug #4106807)
		value += " ";

		// Attempt to convert the date header in a variety of formats
		for (int i = 0; i < formats.length; i++) {
			try {
				Date date = formats[i].parse(value);
				return (date.getTime());
			} catch (ParseException e) {
				;
			}
		}
		throw new IllegalArgumentException(value);
	}

	@Override
	public String getHeader(String name) {
		name = name.toLowerCase();
		synchronized (headers) {
			List values = (ArrayList) headers.get(name);
			if (values != null)
				return ((String) values.get(0));
			else
				return null;
		}
	}

	@Override
	public Enumeration getHeaderNames() {
		synchronized (headers) {
			return (new Enumerator(headers.keySet()));
		}
	}

	@Override
	public Enumeration getHeaders(String name) {
		name = name.toLowerCase();
		synchronized (headers) {
			ArrayList values = (ArrayList) headers.get(name);
			if (values != null)
				return (new Enumerator(values));
			else
				return (new Enumerator(empty));
		}
	}

	@Override
	public int getIntHeader(String name) {
		String value = getHeader(name);
		if (value == null)
			return (-1);
		else
			return (Integer.parseInt(value));
	}

	@Override
	public String getMethod() {
		return this.method;
	}

	@Override
	public String getPathInfo() {
		return this.pathInfo;
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQueryString() {
		return this.queryString;
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestURI() {
		return this.requestURI;
	}

	@Override
	public StringBuffer getRequestURL() {
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return this.requestedSessionCookie;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return this.requestedSessionURL;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return this.requestedSessionURL;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void setRequestedSessionId(String requestedSessionId) {
		this.requestedSessionId = requestedSessionId;
	}

	public void setRequestedSessionURL(boolean requestedSessionURL) {
		this.requestedSessionURL = requestedSessionURL;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public void setRequestedSessionCookie(boolean requestedSessionCookie) {
		this.requestedSessionCookie = requestedSessionCookie;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public void addHeader(String name, String value) {
		name = name.toLowerCase();
		synchronized (headers) {
			List values = (ArrayList) headers.get(name);
			if (values == null) {
				values = new ArrayList();
				headers.put(name, values);
			}
			values.add(value);
		}
	}

	public void addCookie(Cookie cookie) {
		synchronized (cookies) {
			cookies.add(cookie);
		}
	}

	/**
	 * 解析参数
	 */
	protected void praseParameters() {
		if (this.prased) { // 已经解析过，直接返回
			return;
		}
		ParameterMap results = this.parameters;
		if (results == null) {
			results = new ParameterMap();
		}
		results.setLocked(false); // 开启锁，让map可写
		String encoding = getCharacterEncoding();
		if (encoding == null) {
			encoding = HttpConstants.HTTP_DEFAULT_ECNODING;
		}
		String queryString = getQueryString();
		try {
			RequestUtil.parseParameters(results, queryString, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new MyAppException("解析参数失败");
		}
		String contentType = getContentType();
		if (contentType == null) {
			contentType = "";
		}
		int semiconlon = contentType.indexOf(';');
		if (semiconlon >= 0) {
			contentType = contentType.substring(0, semiconlon).trim();
		} else {
			contentType = contentType.trim();
		}
		if (HttpConstants.HTTP_METHOD_POST.equals(this.getMethod())
				&& this.getContentLength() > 0
				&& HttpConstants.HTTP_METHOD_POST.equals(contentType)) {// post请求并且参数在请求体中
			try {
				int max = this.getContentLength();
				int len = 0;
				byte buf[] = new byte[max];
				ServletInputStream is = this.getInputStream();
				while (len < max) {
					int next = is.read(buf, len, max - len);
					if (next < 0) {
						break;
					}
					len += next;
				}
				is.close();
				if (len < max) {
					throw new MyAppException("Content长度不对");
				}
				RequestUtil.parseParameters(results, buf, encoding);
			} catch (Exception e) {
				throw new MyAppException("Content读取失败");
			}

		}
		results.setLocked(true);
		this.prased = true;
		parameters = results;
	}

	public InputStream getStream() {
		return input;
	}

	public ServletInputStream createInputStream() throws IOException {
		return (new RequestStream(this));
	}

}
