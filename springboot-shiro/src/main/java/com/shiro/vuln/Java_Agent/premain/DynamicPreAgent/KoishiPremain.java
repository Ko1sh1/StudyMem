import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;


public class KoishiPremain {
    public static void premain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException {
        // 注册自定义的 Transformer
        inst.addTransformer(new KoishiTransformer());
    }
}
