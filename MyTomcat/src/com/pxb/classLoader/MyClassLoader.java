/**    
* @Title: MyClassLoader.java  
* @Package com.pxb.classLoader  
* @Description: TODO(��һ�仰�������ļ���ʲô)  
* @author panxiaobo    
* @date 2017��9��19�� ����10:34:17  
* @version V1.0.0    
*/  
package com.pxb.classLoader;

/**  
 * @ClassName: MyClassLoader  
 * @Description:   ClassLoader�������
 * @author panxiaobo  
 * @date 2017��9��19�� ����10:34:17  
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
