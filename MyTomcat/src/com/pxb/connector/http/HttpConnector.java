/**    
 * @Title: HttpConnector.java  
 * @Package com.pxb.connector  
 * @Description: TODO(��һ�仰�������ļ���ʲô)  
 * @author panxiaobo    
 * @date 2017��10��17�� ����11:15:41  
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
 * @Description: http������,û��ʵ��1.1������
 * @author panxiaobo
 * @date 2017��10��17�� ����11:15:41
 * 
 */
public class HttpConnector implements Runnable, Connector, Lifecycle {
	public static Logger log = LoggerFactory.getLogger("httpServer");
	boolean stopped;
	private String scheme = "http";
	/**
	 * �Ƿ��Ѿ���ʼ����
	 */
	private boolean initialized;
	/**
	 * ��Ϣ������
	 */
	private StringManager sm = StringManager.getManager(HttpConstants.Package);

	/**
	 * �����socket��Ϊ�˽��տͻ�������
	 */
	private ServerSocket serverSocket = null;

	/**
	 * ��Ҫ�󶨵ķ������˵�ַ
	 */
	private String address;
	/**
	 * ��������socket����
	 */
	private ServerSocketFactory factory;
	/**
	 * tomcatĬ�϶˿ں�
	 */
	private int port;
	/**
	 * �����������ɽ��ܵ�������
	 */
	private int acceptCount;
	/**
	 * �Ƿ��Ѿ�����
	 */
	private boolean started;
	/**
	 * ע��ĺ�̨�߳���
	 */
	private String threadName;
	/**
	 * ��������֧������������
	 */
	protected LifecycleSupport lifecycle = new LifecycleSupport(this);
	/**
	 * ��̨�߳�
	 */
	private Thread thread;
	/**
	 * ��ǰ��������
	 */
	private int curProcessors;
	/**
	 * ��С��������
	 */
	private int minProcessors;
	/**
	 * ���������
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
     * ͬ������
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
     * ����������һ��������ȥ����HTTP����������еĴ���������ʹ���˾ͷ���null.
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
		// У�鲢���´�������������״̬
		if (started){
			throw new LifecycleException(
					sm.getString("httpConnector.alreadyStarted"));
		}
		threadName = "HttpConnector[" + port + "]";
		lifecycle.fireLifecycleEvent(START_EVENT, null);
		started = true;
		// ������̨�߳�
		threadStart();

		// ������Ӧ�����Ĵ����߳�
		while (curProcessors < minProcessors) {
			if ((maxProcessors > 0) && (curProcessors >= maxProcessors)){
				break;
			}
			HttpProcessor processor = newProcessor();
			recycle(processor);
		}

	}
	
	
	/**
	 * ���մ�����
	 * @param processor
	 */
	public void recycle(HttpProcessor processor) {
		processors.push(processor);
		
	}

	/**
	 * ����������
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
	 * ������̨�����߳�
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
	 * �򿪲����ط����socket
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
		// ��ȡ�����socket����
		ServerSocketFactory factory = getFactory();

		// ���û�����õ�ַ��Ĭ�ϴ����е�ַ
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
