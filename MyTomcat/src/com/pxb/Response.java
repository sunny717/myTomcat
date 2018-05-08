/**    
 * @Title: Response.java  
 * @Package com.pxb  
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author panxiaobo    
 * @date 2017年9月18日 下午4:43:25  
 * @version V1.0.0    
 */
package com.pxb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

/**
 * @ClassName: Response
 * @Description: response对象
 * @author panxiaobo
 * @date 2017年9月18日 下午4:43:25
 * 
 */
public class Response implements ServletResponse {

	private static final int BUFFER_SIZE = 1024;
	Request request;
	OutputStream output;
	PrintWriter writer;

	/* This method is used to serve static pages */
	public void sendStaticResource() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		FileInputStream fis = null;
		try {
			/* request.getUri has been replaced by request.getRequestURI */
			File file = new File(Constants.WEBROOT, request.getUri());
			fis = new FileInputStream(file);
			/*
			 * HTTP Response = Status-Line(( general-header | response-header |
			 * entity-header ) CRLF) CRLF [ message-body ] Status-Line =
			 * HTTP-Version SP Status-Code SP Reason-Phrase CRLF
			 */
			int ch = fis.read(bytes, 0, BUFFER_SIZE);
			while (ch != -1) {

				output.write(bytes, 0, ch);
				ch = fis.read(bytes, 0, BUFFER_SIZE);
			}
		} catch (FileNotFoundException e) {
			String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
					+ "Content-Type: text/html\r\n" + "Content-Length: 23\r\n"
					+ "\r\n" + "<h1>File Not Found</h1>";
			output.write(errorMessage.getBytes());
		} finally {
			if (fis != null)
				fis.close();
		}
	}
	
	public Response(OutputStream output) {
		this.output = output;
		this.writer=new PrintWriter(output);
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		return this.writer;
	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBufferSize(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCharacterEncoding(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentLength(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentType(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocale(Locale arg0) {
		// TODO Auto-generated method stub

	}

}
