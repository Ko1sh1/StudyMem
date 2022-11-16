package com.tomcat.memshell.GlassFish_Grizzly_Filter;

import org.apache.catalina.core.RequestFacadeHelper;
import org.glassfish.grizzly.Context;
import org.glassfish.grizzly.EmptyCompletionHandler;
import org.glassfish.grizzly.filterchain.*;
import org.glassfish.grizzly.filterchain.Filter;
import org.glassfish.grizzly.filterchain.FilterChainEvent;
import org.glassfish.grizzly.filterchain.ListFacadeFilterChain;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.http.server.AfterServiceListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.nio.transport.TCPNIOConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

public class GGFFilterShell extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 开摆了，这里没弄出来
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null) field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    public static Object getObj(final Class<?> clazz, final String fieldName,Object obj) throws IllegalAccessException {
        Field field = getField(clazz,fieldName);
        Object aim = field.get(obj);
        return aim;
    }
}