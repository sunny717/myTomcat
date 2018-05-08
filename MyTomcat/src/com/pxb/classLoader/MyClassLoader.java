/**    
* @Title: MyClassLoader.java  
* @Package com.pxb.classLoader  
* @Description: TODO(用一句话描述该文件做什么)  
* @author panxiaobo    
* @date 2017年9月19日 上午10:34:17  
* @version V1.0.0    
*/  
package com.pxb.classLoader;

/**  
 * @ClassName: MyClassLoader  
 * @Description:   ClassLoader类加载器
 * @author panxiaobo  
 * @date 2017年9月19日 上午10:34:17  
 *    
 */
public class MyClassLoader extends ClassLoader {
	
	private static MyClassLoader myClassLoader;
	
	public MyClassLoader(ClassLoader parent){
		super(parent);
	}
	
	public static synchronized MyClassLoader getInstance(){
		if(myClassLoader==null){
			myClassLoader=new MyClassLoader(MyClassLoader.class.getClassLoader());
		}
		return myClassLoader;
	}
	

	

}
