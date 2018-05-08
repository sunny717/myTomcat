/**    
 * @Title: Bootstrap.java  
 * @Package com.pxb.startup 
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author panxiaobo    
 * @date 2017年9月18日 下午6:11:51  
 * @version V1.0.0    
 */
package com.pxb.startup;

import org.apache.catalina.LifecycleException;

import com.pxb.connector.http.HttpConnector;

/**
 * @ClassName: Bootstrap
 * @Description: 启动类
 * @author panxiaobo
 * @date 2017年9月18日 下午6:11:51
 * 
 */
public class Bootstrap {
	public static void main(String[] args) {
		HttpConnector httpConnector=new HttpConnector();
		try {
			httpConnector.start();
		} catch (LifecycleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
