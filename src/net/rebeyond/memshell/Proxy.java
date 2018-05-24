package net.rebeyond.memshell;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import net.rebeyond.memshell.redefine.MyRequest;
import net.rebeyond.memshell.redefine.MyResponse;
import net.rebeyond.memshell.redefine.MyServletInputStream;
import net.rebeyond.memshell.redefine.MyServletOutputStream;
import net.rebeyond.memshell.redefine.MySession;

public class Proxy {
	public void doProxy(Object request, Object response) throws Exception {
		Object httpSession = MyRequest.getSession(request);
		String cmd = MyRequest.getHeader(request, "X-CMD");
		if (cmd != null) {
			MyResponse.setHeader(response, "X-STATUS", "OK");
			if (cmd.compareTo("CONNECT") == 0) {
				try {
					String target = MyRequest.getHeader(request, "X-TARGET");
					int port = Integer.parseInt(MyRequest.getHeader(request, "X-PORT"));
					SocketChannel socketChannel = SocketChannel.open();
					socketChannel.connect(new InetSocketAddress(target, port));
					socketChannel.configureBlocking(false);
					MySession.setAttribute(httpSession, "socket", socketChannel);
					MyResponse.setHeader(response, "X-STATUS", "OK");
				} catch (UnknownHostException e) {
					System.out.println(e.getMessage());
					MyResponse.setHeader(response, "X-ERROR", e.getMessage());
					MyResponse.setHeader(response, "X-STATUS", "FAIL");
				} catch (IOException e) {
					System.out.println(e.getMessage());
					MyResponse.setHeader(response, "X-ERROR", e.getMessage());
					MyResponse.setHeader(response, "X-STATUS", "FAIL");

				}
			} else if (cmd.compareTo("DISCONNECT") == 0) {
				SocketChannel socketChannel = (SocketChannel) MySession.getAttribute(httpSession, "socket");
				try {
					socketChannel.socket().close();
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
				MySession.invalidate(httpSession);
			} else if (cmd.compareTo("READ") == 0) {
				SocketChannel socketChannel = (SocketChannel) MySession.getAttribute(httpSession, "socket");
				try {
					ByteBuffer buf = ByteBuffer.allocate(512);
					int bytesRead = socketChannel.read(buf);
					// ServletOutputStream so = response.getOutputStream();
					Object so = MyResponse.getOutputStream(response);
					while (bytesRead > 0) {
						// so.write(buf.array(),0,bytesRead);
						// so.flush();
						MyServletOutputStream.write(so, buf.array(), 0, bytesRead);
						MyServletOutputStream.flush(so);
						buf.clear();
						bytesRead = socketChannel.read(buf);
					}
					// response.setHeader("X-STATUS", "OK");
					MyResponse.setHeader(response, "X-STATUS", "OK");
					// so.flush();
					// so.close();
					MyServletOutputStream.flush(so);
					MyServletOutputStream.close(so);

				} catch (Exception e) {
					System.out.println(e.getMessage());
					MyResponse.setHeader(response, "X-ERROR", e.getMessage());
					MyResponse.setHeader(response, "X-STATUS", "FAIL");
					// socketChannel.socket().close();
				}

			} else if (cmd.compareTo("FORWARD") == 0) {
				SocketChannel socketChannel = (SocketChannel) MySession.getAttribute(httpSession, "socket");
				try {

					int readlen = MyRequest.getContentLength(request);
					byte[] buff = new byte[readlen];
					// request.getInputStream().read(buff, 0, readlen);
					Object ins = MyRequest.getInputStream(request);
					MyServletInputStream.read(ins, buff, 0, readlen);
					ByteBuffer buf = ByteBuffer.allocate(readlen);
					buf.clear();
					buf.put(buff);
					buf.flip();

					while (buf.hasRemaining()) {
						socketChannel.write(buf);
					}
					MyResponse.setHeader(response, "X-STATUS", "OK");
					// response.getOutputStream().close();

				} catch (Exception e) {
					System.out.println(e.getMessage());
					MyResponse.setHeader(response, "X-ERROR", e.getMessage());
					MyResponse.setHeader(response, "X-STATUS", "FAIL");
					socketChannel.socket().close();
				}
			}
		} else {
			// PrintWriter o = response.getWriter();
			// out.print("Georg says, 'All seems fine'");
			MyResponse.getWriter(response).print("Georg says, 'All seems fine'");
		}
	}
}
