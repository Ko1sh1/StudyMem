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
    // ÿ���౻���أ��ͻ���� transform ����
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // ����ʶ��ʽ��ƽ���Ĳ�һ����ƽʱ��. ����/ ���������� replace �������õ�����ת��Ϊ JVM ��ʶ������
        className = className.replace("/", ".");

        // �������Ϊ����ָ������,������д���
        // ������Լ���һЩ�޸��ֽ���Ĳ���
        if(className.equals(aimClassName)){
            try {
                // ���� java
                final CtClass koishiWantClass = ClassPool.getDefault().get(aimClassName);
                CtMethod koishiMethod = koishiWantClass.getDeclaredMethod("say");
                // �����޸ķ������ݣ�����һ���������
                String methodBody = "{System.out.println(\"Koishi is hardworking!\");\n" +
                        "System.out.println(\"I hope Ilyn would be also diligent\");}";
                koishiMethod.setBody(methodBody);

                // �����ֽ��룬����detachCtClass����
                byte[] byteCode = koishiWantClass.toBytecode();
                //detach����˼�ǽ��ڴ���������javassist���ع���Date�����Ƴ�������´�����Ҫ���ڴ����Ҳ�����������javassist����
                koishiWantClass.detach();

                //�����޸ĺ���ֽ��룬ʹ������޸ĺ���ֽ���
                return byteCode;
            }catch (Exception e){
                // do nothing
            }
        }
        // �������null���ֽ��벻�ᱻ�޸ģ�������д�����������䣬������Щ�౻������
        System.out.print("this is not the aimClass :");
        System.out.println(className);
        return null;
    }
}
