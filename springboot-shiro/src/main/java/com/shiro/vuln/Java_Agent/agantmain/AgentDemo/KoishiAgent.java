
import java.lang.instrument.Instrumentation;

public class KoishiAgent {
    public static void agentmain(String agentArgs, Instrumentation ins) {
        ins.addTransformer(new KoishiTransformer(),true);
    }
}
