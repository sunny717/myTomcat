/**    
 * @Title: HttpConnector.java  
 * @Package com.pxb.connector  
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author panxiaobo    
 * @date 2017年10月17日 上午11:15:41  
 * @version V1.0.0    
 */
package com.pxb.connector.http;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Stack;
import java.util.Vector;

import org.apache.catalina.Connector;
import org.apache.catalina.Container;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Service;
import org.apache.catalina.connector.http.Constants;
import org.apache.catalina.net.DefaultServerSocketFactory;
import org.apache.catalina.net.ServerSocketFactory;
import org.apache.catalina.util.LifecycleSupport;
import org.apache.catalina.util.StringManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pxb.processor.HttpProcessor;
import com.pxb.util.HttpConstants;

/**
 * @ClassName: HttpConnector
 * @Description: http连接器,没有实现1.1连接器
 * @author panxiaobo
 * @date 2017年10月17日 上午11:15:41
 * 
 */
public class HttpConnector implements Runnable, Connector, Lifecycle {
	public static Logger log = LoggerFactory.getLogger("httpServer");
	boolean stopped;
	private String scheme = "http";
	/**
	 * 是否已经初始化了
	 */
	private boolean initialized;
	/**
	 * 消息管理器
	 */
	private StringManager sm = StringManager.getManager(HttpConstants.Package);

	/**
	 * 服务端socket，为了接收客户端连接
	 */
	private ServerSocket serverSocket = null;

	/**
	 * 需要绑定的服务器端地址
	 */
	private String address;
	/**
	 * 服务器端socket工程
	 */
	private ServerSocketFactory factory;
	/**
	 * tomcat默认端口号
	 */
	private int port;
	/**
	 * 此连接器最多可接受的连接数
	 */
	private int acceptCount;
	/**
	 * 是否已经启动
	 */
	private boolean started;
	/**
	 * 注册的后台线程名
	 */
	private String threadName;
	/**
	 * 此连接器支持生命周期类
	 */
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	/**
	 * 后台线程
	 */
	private Thread thread;
	/**
	 * 当前处理器数
	 */
	private int curProcessors;
	/**
	 * 最小处理器数
	 */
	private int minProcessors;
	/**
	 * 最大处理器数
	 */
	private int maxProcessors;
	private Vector<HttpProcessor> created=new Vector<HttpProcessor>();
	
	private Stack processors=new Stack();
	private int connectionTimeout=Constants.DEFAULT_CONNECTION_TIMEOUT;
	 /**
     * Use TCP no delay ?
     */
    private boolean tcpNoDelay = true;
    /**
     * 同步对象
     */
    private Object threadSync = new Object();
    
    
    /**
     * The input buffer size we should create on input streams.
     */
    private int bufferSize = 2048;
	public String getScheme() {
		return scheme;
	}

	@Override
	public void run() {


        // Loop until we receive a shutdown command
        while (!stopped) {

            // Accept the next incoming connection from the server socket
            Socket socket = null;
            try {
                //                if (debug >= 3)
                //                    log("run: Waiting on serverSocket.accept()");
                socket = serverSocket.accept();
                //                if (debug >= 3)
                //                    log("run: Returned from serverSocket.accept()");
                if (connectionTimeout > 0)
                    socket.setSoTimeout(connectionTimeout);
                socket.setTcpNoDelay(tcpNoDelay);
            } catch (AccessControlException ace) {
                log.error("socket accept security exception", ace);
                continue;
            } catch (IOException e) {
                //                if (debug >= 3)
                //                    log("run: Accept returned IOException", e);
                try {
                    // If reopening fails, exit
                    synchronized (threadSync) {
                        if (started && !stopped)
                            log.error("accept error: ", e);
                        if (!stopped) {
                            //                    if (debug >= 3)
                            //                        log("run: Closing server socket");
                            serverSocket.close();
                            //                        if (debug >= 3)
                            //                            log("run: Reopening server socket");
                            serverSocket = open();
                        }
                    }
                    //                    if (debug >= 3)
                    //                        log("run: IOException processing completed");
                } catch (IOException ioe) {
                	log.error("socket reopen, io problem: ", ioe);
                    break;
                } catch (KeyStoreException kse) {
                	log.error("socket reopen, keystore problem: ", kse);
                    break;
                } catch (NoSuchAlgorithmException nsae) {
                	log.error("socket reopen, keystore algorithm problem: ", nsae);
                    break;
                } catch (CertificateException ce) {
                	log.error("socket reopen, certificate problem: ", ce);
                    break;
                } catch (UnrecoverableKeyException uke) {
                	log.error("socket reopen, unrecoverable key: ", uke);
                    break;
                } catch (KeyManagementException kme) {
                	log.error("socket reopen, key management problem: ", kme);
                    break;
                }

                continue;
            }

            // Hand this socket off to an appropriate processor
            HttpProcessor processor = createProcessor();
            if (processor == null) {
                try {
                	log.info(sm.getString("httpConnector.noProcessor"));
                    socket.close();
                } catch (IOException e) {
                    ;
                }
                continue;
            }
            //            if (debug >= 3)
            //                log("run: Assigning socket to processor " + processor);
            processor.assign(socket);

            // The processor will recycle itself when it finishes

        }

        // Notify the threadStop() method that we have shut ourselves down
        //        if (debug >= 3)
        //            log("run: Notifying threadStop() that we have shut down");
        synchronized (threadSync) {
            threadSync.notifyAll();
        }
	}
	
	 /**
     * 创建并返回一个处理器去处理HTTP请求，如果所有的处理器都被使用了就返回null.
     */
    private HttpProcessor createProcessor() {

        synchronized (processors) {
            if (processors.size() > 0) {
                // if (debug >= 2)
                // log("createProcessor: Reusing existing processor");
                return ((HttpProcessor) processors.pop());
            }
            if ((maxProcessors > 0) && (curProcessors < maxProcessors)) {
                // if (debug >= 2)
                // log("createProcessor: Creating new processor");
                return (newProcessor());
            } else {
                if (maxProcessors < 0) {
                    // if (debug >= 2)
                    // log("createProcessor: Creating new processor");
                    return (newProcessor());
                } else {
                    // if (debug >= 2)
                    // log("createProcessor: Cannot create new processor");
                    return (null);
                }
            }
        }

    }

	public void start() throws LifecycleException {
		// 校验并更新此连接器的启动状态
		if (started){
			throw new LifecycleException(
					sm.getString("httpConnector.alreadyStarted"));
		}
		threadName = "HttpConnector[" + port + "]";
		lifecycle.fireLifecycleEvent(START_EVENT, null);
		started = true;
		// 启动后台线程
		threadStart();

		// 创建对应数量的处理线程
		while (curProcessors < minProcessors) {
			if ((maxProcessors > 0) && (curProcessors >= maxProcessors)){
				break;
			}
			HttpProcessor processor = newProcessor();
			recycle(processor);
		}

	}
	
	
	/**
	 * 回收处理器
	 * @param processor
	 */
	public void recycle(HttpProcessor processor) {
		processors.push(processor);
		
	}

	/**
	 * 创建处理器
	 * @return
	 */
	private HttpProcessor newProcessor() {
        HttpProcessor processor = new HttpProcessor(this, curProcessors++);
        if (processor instanceof Lifecycle) {
            try {
                ((Lifecycle) processor).start();
            } catch (LifecycleException e) {
                log.error("newProcessor", e);
                return (null);
            }
        }
        created.addElement(processor);
        return (processor);
	}

	/**
	 * 启动后台处理线程
	 */
	private void threadStart() {

		log.info(sm.getString("httpConnector.starting"));

		thread = new Thread(this, threadName);
		thread.setDaemon(true);
		thread.start();

	}

	@Override
	public void addLifecycleListener(LifecycleListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public LifecycleListener[] findLifecycleListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLifecycleListener(LifecycleListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() throws LifecycleException {
		// TODO Auto-generated method stub

	}

	@Override
	public Container getContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContainer(Container container) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getEnableLookups() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEnableLookups(boolean enableLookups) {
		// TODO Auto-generated method stub

	}

	@Override
	public ServerSocketFactory getFactory() {
		if (this.factory == null) {
			synchronized (this) {
				this.factory = new DefaultServerSocketFactory();
			}
		}
		return (this.factory);
	}

	@Override
	public void setFactory(ServerSocketFactory factory) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRedirectPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRedirectPort(int redirectPort) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setScheme(String scheme) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSecure(boolean secure) {
		// TODO Auto-generated method stub

	}

	@Override
	public Service getService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setService(Service service) {
		// TODO Auto-generated method stub

	}

	@Override
	public Request createRequest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response createResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() throws LifecycleException {
		if (initialized)
			throw new LifecycleException(
					sm.getString("httpConnector.alreadyInitialized"));

		this.initialized = true;

	}

	/**
	 * 打开并返回服务端socket
	 * 
	 * @return ServerSocket
	 * @throws IOException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 */
	private ServerSocket open() throws UnrecoverableKeyException,
			KeyManagementException, KeyStoreException,
			NoSuchAlgorithmException, CertificateException, IOException {
		// 获取服务端socket工厂
		ServerSocketFactory factory = getFactory();

		// 如果没有配置地址，默认打开所有地址
		if (address == null) {
			log.info(sm.getString("httpConnector.allAddresses"));
			try {
				return (factory.createSocket(port, acceptCount));
			} catch (BindException be) {
				throw new BindException(be.getMessage() + ":" + port);
			}
		}

		// Open a server socket on the specified address
		try {
			InetAddress is = InetAddress.getByName(address);
			log.info(sm.getString("httpConnector.anAddress", address));
			try {
				return (factory.createSocket(port, acceptCount, is));
			} catch (BindException be) {
				throw new BindException(be.getMessage() + ":" + address + ":"
						+ port);
			}
		} catch (Exception e) {
			log.info(sm.getString("httpConnector.noAddress", address));
			try {
				return (factory.createSocket(port, acceptCount));
			} catch (BindException be) {
				throw new BindException(be.getMessage() + ":" + port);
			}
		}
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	
	

}
