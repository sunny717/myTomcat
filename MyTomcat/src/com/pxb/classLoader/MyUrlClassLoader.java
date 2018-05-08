/**    
 * @Title: MyUrlClassLoader.java  
 * @Package com.pxb.classLoader  
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author panxiaobo    
 * @date 2017年9月19日 上午10:37:10  
 * @version V1.0.0    
 */
package com.pxb.classLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import com.pxb.HttpServer;

/**
 * @ClassName: MyUrlClassLoader
 * @Description: myUrlClassLoader
 * @author panxiaobo
 * @date 2017年9月19日 上午10:37:10
 * 
 */
public class MyUrlClassLoader extends URLClassLoader {
	private static MyUrlClassLoader myUrlClassLoader;

	public MyUrlClassLoader(URL[] urls) {
		super(urls);
	}

	public static MyUrlClassLoader getInstance(URL[] urls) {
		if (myUrlClassLoader != null) {
			return myUrlClassLoader;
		} else {
			synchronized (MyUrlClassLoader.class) {
				if (myUrlClassLoader == null) {
					myUrlClassLoader = new MyUrlClassLoader(urls);
				}
				return myUrlClassLoader;
			}
		}
	}

	public MyUrlClassLoader getMyClassLoader() {
		MyUrlClassLoader myUrlClassLoader = null;
		try {
			// create a URLClassLoader
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classPath = new File(System.getProperty("user.dir"));
			// the forming of repository is taken from the
			// createClassLoader method in
			// org.apache.catalina.startup.ClassLoaderFactory
			String repository = (new URL("file", null,
					classPath.getCanonicalPath() + File.separator)).toString();
			// the code for forming the URL is taken from
			// the addRepository method in
			// org.apache.catalina.loader.StandardClassLoader.
			urls[0] = new URL(null, repository, streamHandler);
			myUrlClassLoader = MyUrlClassLoader.getInstance(urls);
		} catch (IOException e) {
			HttpServer.log.error("ServletProcessor.process failed", e);
		}
		return myUrlClassLoader;
	}

}
