package m2dl.arge.tp1;

import org.apache.xmlrpc.XmlRpcException;
import org.openstack4j.model.compute.Server.Status;

public class Redirect {
	public int div(int n) {
		Integer result = 0;
		Object[] params = null;

		params = new Object[] { new Integer(n) };

		try {
			//System.out.println(params.toString());
			result = (Integer) getNextCalculatorInfo().getClient().execute("Calculator.div", params);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}

		return result;
	}

	public CalculatorInfo getNextCalculatorInfo() {
		// Chercher le Server suivant actif
		
		if (Repartiteur.calculators.size() == 1) {
			while (Repartiteur.actualCalculator.getStatus() != Status.ACTIVE) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO: handle exception
				}
			}
			return Repartiteur.actualCalculator;
		}

		for (CalculatorInfo c : Repartiteur.calculators) {
			if (c == Repartiteur.actualCalculator) {
				int index = Repartiteur.calculators.indexOf(c);
				if (Repartiteur.calculators.get(index+1) != null && 
						Repartiteur.calculators.get(index+1).getStatus().equals("ACTIVE")) {
					Repartiteur.actualCalculator = Repartiteur.calculators.get(index+1);
					return Repartiteur.actualCalculator;
				} else if (Repartiteur.calculators.get(index+1) == null){
					Repartiteur.actualCalculator = Repartiteur.calculators.get(0);
					return Repartiteur.actualCalculator;
				}
			}
		}
		return Repartiteur.actualCalculator;
	}
}
