/**    
 * @Title: ResponseFacade.java  
 * @Package com.pxb  
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author panxiaobo    
 * @date 2017年10月17日 上午10:33:32  
 * @version V1.0.0    
 */
package com.pxb;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

/**
 * @ClassName: ResponseFacade
 * @Description: response装饰器
 * @author panxiaobo
 * @date 2017年10月17日 上午10:33:32
 * 
 */
public class ResponseFacade implements ServletResponse {

	private Response response;

	public ResponseFacade(Response response) {
		this.response = response;
	}

	@Override
	public void flushBuffer() throws IOException {
		response.flushBuffer();
	}

	@Override
	public int getBufferSize() {
		return response.getBufferSize();
	}

	@Override
	public String getCharacterEncoding() {
		return response.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return response.getContentType();
	}

	@Override
	public Locale getLocale() {
		return response.getLocale();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return response.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return response.getWriter();
	}

	@Override
	public boolean isCommitted() {
		return response.isCommitted();
	}

	@Override
	public void reset() {
		response.reset();
	}

	@Override
	public void resetBuffer() {
		response.resetBuffer();
	}

	@Override
	public void setBufferSize(int arg0) {
		response.setBufferSize(arg0);
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		response.setCharacterEncoding(arg0);

	}

	@Override
	public void setContentLength(int arg0) {
		response.setContentLength(arg0);
	}

	@Override
	public void setContentType(String arg0) {
		response.setContentType(arg0);
	}

	@Override
	public void setLocale(Locale arg0) {
		response.setLocale(arg0);
	}

}
