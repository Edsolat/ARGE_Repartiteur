package m2dl.arge.tp1;


import java.util.ArrayList;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.ActionResponse;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.Server.Status;
import org.openstack4j.model.compute.ServerCreate;

public class ThreadCPU extends Thread{
	//XmlRpcClient client;

	public ThreadCPU(/*XmlRpcClient client*/) {
		//this.client = client;
	}

	public int getChargeMoyenne() {
		int res = 0;
		for (CalculatorInfo c : Repartiteur.calculators) {
			res = res + c.getCharge();
		}
		return res/Repartiteur.calculators.size();
	}

	public int getChargeMoyenneBeforeDel() {
		int res = 0;
		for (CalculatorInfo c : Repartiteur.calculators) {
			res = res + c.getCharge();
		}
		return res/(Repartiteur.calculators.size()-1);
	}

	public void run() {
		Object[] params = null;
		params = new Object[] {  };

		while(true) {
			try {
				for (CalculatorInfo c : Repartiteur.calculators) {

					if (c.getStatus() == Status.ACTIVE) {
						c.setCharge((Integer) c.getClient().execute("Calculator.getProcessCpuLoad", params));
						System.out.println(c.getPort() + " --> " + c.getCharge() + "%\n");
					}
				}

				//System.out.println(Repartiteur.charge);
				Thread.sleep(1000);
			} catch (XmlRpcException e) {
				e.printStackTrace();
				System.out.println("Error while connecting to calculator server...");
				break;
			} catch(InterruptedException e) {
				e.printStackTrace();
				break;
			}

			if (getChargeMoyenne() > 70) {
				if (Repartiteur.calculators.size() < 5) {
					try {
						UtilVM.addVM();
					} catch (WNException e1) {
						e1.printStackTrace();
					}
				}
				else {
					System.out.println("Charge maximale atteinte pour 5 VM...");
				}
			} else if (Repartiteur.calculators.size() > 1 
					&& getChargeMoyenneBeforeDel() < 50) {
				try {
					UtilVM.delVM();
				} catch (WNException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
