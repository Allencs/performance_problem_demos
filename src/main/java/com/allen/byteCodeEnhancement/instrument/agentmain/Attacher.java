package com.allen.byteCodeEnhancement.instrument.agentmain;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

/**
 * @Author: allen
 * @Date: 2022/4/23 16:18
 * @Description:
 **/
public class Attacher {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        VirtualMachine vm = VirtualMachine.attach("38892");
        vm.loadAgent("libs/myAgentMainAgent.jar");
    }
}
