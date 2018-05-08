/**    
* @Title: RequestStream.java  
* @Package com.pxb.connector  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年10月23日 上午10:02:17  
* @version V1.0.0    
*/  
package com.pxb.connector;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

import com.pxb.connector.http.HttpRequest;
import com.pxb.exception.MyAppException;

/**  
 * @ClassName: RequestStream  
 * @Description: TODO(这里用一句话描述这个类的作用)  
 * @author panxiaobo  
 * @date 2017年10月23日 上午10:02:17  
 *    
 */
public class RequestStream extends ServletInputStream{
	

    // ----------------------------------------------------- Instance Variables


    /**
     * Has this stream been closed?
     */
    protected boolean closed = false;


    /**
     * The number of bytes which have already been returned by this stream.
     */
    protected int count = 0;


    /**
     * The content length past which we will not read, or -1 if there is
     * no defined content length.
     */
    protected int length = -1;




    /**
     * The underlying input stream from which we should read data.
     */
    protected InputStream stream = null;
	
	
    /**
     * Construct a servlet input stream associated with the specified Request.
     *
     * @param request The associated request
     */
    public RequestStream(HttpRequest request) {

        super();
        closed = false;
        count = 0;
        length = request.getContentLength();
        stream = request.getStream();

    }

	@Override
	public int read() throws IOException {
        // Has this stream been closed?
        if (closed)
            throw new MyAppException("流已经关闭");

        // Have we read the specified content length already?
        if ((length >= 0) && (count >= length))
            return (-1);        // End of file indicator

        // Read and count the next byte, then return it
        int b = stream.read();
        if (b >= 0)
            count++;
        return (b);
	}

	@Override
	public int read(byte[] b) throws IOException {
		 return (read(b, 0, b.length));
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int toRead = len;
        if (length > 0) {
            if (count >= length)
                return (-1);
            if ((count + len) > length)
                toRead = length - count;
        }
        int actuallyRead = super.read(b, off, toRead);
        return (actuallyRead);
	}

	@Override
	public void close() throws IOException {
		 if (closed)
			 throw new MyAppException("流已经关闭");
	        if (length > 0) {
	            while (count < length) {
	                int b = read();
	                if (b < 0)
	                    break;
	            }
	        }

	        closed = true;
	}
	
	

}
