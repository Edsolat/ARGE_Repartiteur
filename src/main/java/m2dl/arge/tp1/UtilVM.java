package m2dl.arge.tp1;

import java.util.ArrayList;
import java.util.List;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.ActionResponse;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;

public class UtilVM {

	private static void listfloatingIps(final OSClient os,Server server) {
		final List<? extends FloatingIP> ips = os.compute().floatingIps().list();
		for (final FloatingIP ip : ips) {
			System.out.println(ip);
			if (ip.getInstanceId() == null) {
				final String address = ip.getFloatingIpAddress();
				assignFloatingIp(os, address, server);
			}
		}
	}

	private static void assignFloatingIp(final OSClient os, final String IpId,Server server) {
		System.out.println("Assigning floating Ip");
		//final Server server = os.compute().servers().get("a382b060-c7a4-4107-ada9-b31be67e7f2a");
		final ActionResponse ar = os.compute().floatingIps().addFloatingIP(server, IpId);
		System.out.println("Assigned");
		System.out.println(ar.isSuccess());
	}

	/*
	 * - Détruit une VM via nova delete
	 * - Enleve le calculatorInfo associé
	 */
	public static void delVM() throws WNException {
		Repartiteur.os.compute().servers().delete(Repartiteur.actualCalculator.getServer().getId());

		Repartiteur.calculators.remove(Repartiteur.actualCalculator);

		Repartiteur.actualCalculator = Repartiteur.calculators.get(0);
	}

	/*
	 * - Lance une VM
	 * - Créer un calculatorInfo associé et l'ajoute dans la liste
	 */
	public static void addVM() throws WNException{
		CalculatorInfo c =null;
		Flavor myFlavor = null;

		List<? extends Flavor> flavors = Repartiteur.os.compute().flavors().list();
		for (Flavor flavor : flavors) {
			if (flavor.getName().equals("m1.small"))
			{
				myFlavor=flavor;
			}
		}
		//Image image = os.images().get("imageId");
		List<String> network = new ArrayList<String>();
		network.add("c1445469-4640-4c5a-ad86-9c0cb6650cca");

		// Create a Server Model Object
		ServerCreate sc = Builders.server()
				.name("WN_ESANDA"+System.currentTimeMillis())
				.flavor(myFlavor.getId())
				.image("28489c78-bd56-465f-86a3-4fa84f29f9a5")
				//.image("206b4860-25db-4f77-aed3-ba01cefb8874")
				.networks(network) //arrayList
				.build(); 

		Server server = Repartiteur.os.compute().servers().bootAndWaitActive(sc,15000);
		listfloatingIps(Repartiteur.os,server);

		c = new CalculatorInfo(server, 2000);
		Repartiteur.calculators.add(c);

		//return s;
	}

}
