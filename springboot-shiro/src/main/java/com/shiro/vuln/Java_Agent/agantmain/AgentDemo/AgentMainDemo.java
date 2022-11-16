package com.shiro.vuln.Java_Agent.agantmain.AgentDemo;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.List;

public class AgentMainDemo {
    public static void main(String[] args) throws Exception{
        // 这里的path 修改为你的 AgentMain.jar 的路径
        System.out.println("running JVM start ");
        String path = "R:\\languages\\Java\\study\\JavaMemShells\\springboot-shiro\\src\\main\\java\\com\\shiro\\vuln\\Java_Agent\\agantmain\\AgentDemo\\AgentMain.jar";
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor v:list){
            System.out.println(v.displayName());
            if (v.displayName().contains("AgentMainDemo")){
                // 将 jvm 虚拟机的 pid 号传入 attach 来进行远程连接
                VirtualMachine vm = VirtualMachine.attach(v.id());
                System.out.println("id >>> "+v.id());
                // 将我们的 agent.jar 发送给虚拟机
                vm.loadAgent(path);
                //从jvm进程中分离
                vm.detach();
            }
        }
    }
}
