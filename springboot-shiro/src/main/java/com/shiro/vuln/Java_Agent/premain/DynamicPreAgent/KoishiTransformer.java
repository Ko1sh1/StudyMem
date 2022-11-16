package com.shiro.vuln.Java_Agent.premain.DynamicPreAgent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;



public class KoishiTransformer implements ClassFileTransformer {

    public static final String aimClassName = "com.shiro.vuln.Java_Agent.premain.DynamicPreAgent.KoishiSay";
    @Override
    // 每当类被加载，就会调用 transform 函数
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // 它的识别方式和平常的不一样，平时用. 他用/ ，这里来个 replace ，将常用的类名转换为 JVM 认识的类名
        className = className.replace("/", ".");

        // 如果类名为我们指定的类,将其进行处理
        // 这里可以加上一些修改字节码的操作
        if(className.equals(aimClassName)){
            try {
                // 利用 java
                final CtClass koishiWantClass = ClassPool.getDefault().get(aimClassName);
                CtMethod koishiMethod = koishiWantClass.getDeclaredMethod("say");
                // 这里修改方法内容，增加一个输出内容
                String methodBody = "{System.out.println(\"Koishi is hardworking!\");\n" +
                        "System.out.println(\"I hope Ilyn would be also diligent\");}";
                koishiMethod.setBody(methodBody);

                // 返回字节码，并且detachCtClass对象
                byte[] byteCode = koishiWantClass.toBytecode();
                //detach的意思是将内存中曾经被javassist加载过的Date对象移除，如果下次有需要在内存中找不到会重新走javassist加载
                koishiWantClass.detach();

                //返回修改后的字节码，使其加载修改后的字节码
                return byteCode;
            }catch (Exception e){
                // do nothing
            }
        }
        // 如果返回null则字节码不会被修改，这里再写两个输出的语句，看看哪些类被加载了
        System.out.print("this is not the aimClass :");
        System.out.println(className);
        return null;
    }
}
