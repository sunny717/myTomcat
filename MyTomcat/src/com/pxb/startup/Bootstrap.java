/**    
 * @Title: Bootstrap.java  
 * @Package com.pxb.startup 
 * @Description: TODO(��һ�仰�������ļ���ʲô)  
 * @author panxiaobo    
 * @date 2017��9��18�� ����6:11:51  
 * @version V1.0.0    
 */
package com.pxb.startup;

import org.apache.catalina.LifecycleException;

import com.pxb.connector.http.HttpConnector;

/**
 * @ClassName: Bootstrap
 * @Description: ������
 * @author panxiaobo
 * @date 2017��9��18�� ����6:11:51
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
