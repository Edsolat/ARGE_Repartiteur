package m2dl.arge.tp1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.openstack4j.model.compute.Address;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.Server.Status;

public class CalculatorInfo {
	private String ip;
	private String name;
	private int port;
	private XmlRpcClient client;
	private int charge;
	private Server server;
	private Status status;

	public CalculatorInfo(Server s, int port) {
		while(Repartiteur.os.compute().servers().get(s.getId()).getStatus() != Status.ACTIVE) {
			try {
				Thread.sleep(1000);
				System.out.println("VM en cours de lancement \n");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.server = Repartiteur.os.compute().servers().get(s.getId());
		this.status = this.server.getStatus();
		
		this.ip = this.server.getAddresses().getAddresses("private").get(0).getAddr();
		this.name = this.server.getName();
		this.port = port;
		this.charge = 0;

		config("localhost", port);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server s) {
		this.server = s;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private void config(String ip, int port) {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

		try {
			config.setServerURL(new URL("http://" + ip + ":" + port + "/xmlrpc"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		config.setEnabledForExtensions(true);  
		config.setConnectionTimeout(60 * 1000);
		config.setReplyTimeout(60 * 1000);

		this.client = new XmlRpcClient();

		// use Commons HttpClient as transport
		this.client.setTransportFactory(
				new XmlRpcCommonsTransportFactory(this.client));
		// set configuration
		this.client.setConfig(config);
	}

	public XmlRpcClient getClient() {
		return client;
	}

	public void setClient(XmlRpcClient client) {
		this.client = client;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public String[] extractMetadata(Server server) {
		String privateIp = null;
		String floatingIp = null;
		Map<String, List<? extends Address>> adrMap = server.getAddresses().getAddresses();
		for (String key : adrMap.keySet()) {
			List<? extends Address> adrList = adrMap.get(key);
			for (Address adr : adrList) {
				switch (adr.getType()) {
				case "fixed":
					privateIp = adr.getAddr();
					//System.out.println("PrivateIp of instance: "+server.getName()+" is "+privateIp);	                    
					break;
				case "floating":
					floatingIp = adr.getAddr();
					//System.out.println("FloatingIp of instance: "+server.getName()+" is "+floatingIp);	                    
					break;
				default:
					System.out.println("aucune");	            
				}
			}   
		}

		String[] a = {privateIp,floatingIp};
		return a;
	}

}
