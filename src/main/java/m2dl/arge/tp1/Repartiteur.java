package m2dl.arge.tp1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.client.Client;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.Server.Status;
import org.openstack4j.openstack.OSFactory;

public class Repartiteur {
	private int port; 
	private WebServer webServer;
	protected static XmlRpcClient client;
	protected static int charge;
	protected static List<CalculatorInfo> calculators;
	protected static CalculatorInfo actualCalculator;
	protected static OSClient os;


	public Repartiteur(int port) throws XmlRpcException, IOException  {
		this.port = port;
		this.webServer = new WebServer(port);
		Repartiteur.calculators = new ArrayList<CalculatorInfo>();

		XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

		PropertyHandlerMapping phm = new PropertyHandlerMapping();

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		phm.load(classLoader, getClass().getClassLoader().getResource("XmlRpcServlet.properties"));

		phm.addHandler("Redirect",Redirect.class);

		xmlRpcServer.setHandlerMapping(phm);

		XmlRpcServerConfigImpl serverConfig =
				(XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
		serverConfig.setEnabledForExtensions(true);
		serverConfig.setContentLengthOptional(false);

		ResourceBundle bundle = ResourceBundle.getBundle("config");
		

		
		os = OSFactory.builder()
				.endpoint("http://195.220.53.61:5000/v2.0")
				.credentials("ens6","FS0EJP")
				.tenantName("service")
				.authenticate();
		
		// Lance une première VM avec un calculateur
		try {
			UtilVM.addVM();
			System.out.println("VM lancée \n");
		} catch (WNException e) {
			e.printStackTrace();
		}

		/*Repartiteur.actualCalculator = new CalculatorInfo(s ,Integer.parseInt(bundle.getString("defaultPort")));
		Repartiteur.actualCalculator.setStatus(Status.ACTIVE);
		calculators.add(Repartiteur.actualCalculator);
		Repartiteur.actualCalculator = new CalculatorInfo(s ,2001);
		Repartiteur.actualCalculator.setStatus(Status.ACTIVE);
		calculators.add(Repartiteur.actualCalculator);*/
		
		
		webServer.start();

	}

	public void stop() {
		this.webServer.shutdown();
	}

	public static void main(String[] args) {
		Repartiteur repart = null;
		int port = 0;
		if (args.length != 1) {
			throw new RuntimeException("Bad number of arguments...");
		}
		port = Integer.parseInt(args[0]);

		try {
			repart = new Repartiteur(port);
			new ThreadCPU().start();
			System.out.println("Repartiteur on port " + port + " started successfuly !");
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}


}
