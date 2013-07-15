/**
 * 
 */
package test;

import java.io.IOException;
import java.util.ArrayList;

import algorithm.LWRegression;

import Jama.Matrix;

import utils.JamaU;
import utils.RLink;

/**
 * @author dutech
 *
 */
public class TestLWR {

	/**
	 * Creation and all the tests
	 */
	public TestLWR() {
		System.out.println("***** TestLWR *****");
	}
//		try {
//			FileWriter aFile = new FileWriter( "testWrite.txt");
//			aFile.write("Ipsum locust");
//			aFile.flush();
//			aFile.close();
//		} catch (IOException e) { 
//			e.printStackTrace();
//		}
	
	public void run() {	
		boolean res;
		int nbTest = 0;
		int nbPassed = 0;
		try {
			nbTest ++;
			res = testLWRSinglePoint();
			if (res) {
				System.out.println("testLWRSinglePoint >> "+res);
				nbPassed ++;
			}
			else {
				System.err.println("testLWRSinglePoint >> "+res);
			}
			nbTest ++;
			res = testLWRPredict();
			if (res) {
				System.out.println("testLWRPredict >> "+res);
				nbPassed ++;
			}
			else {
				System.err.println("testLWRPredict >> "+res);
			}
			
			if (nbTest > nbPassed) {
				System.err.println("FAILURE : only "+nbPassed+" success out of "+nbTest);
				System.exit(1);
			}
			else {
				System.out.println("SUCCESS : "+nbPassed+" success out of "+nbTest);
				System.exit(0);
			}
		} catch (IOException e) {
			System.err.println("Some test failed badly !!!");
			e.printStackTrace();
		}
	}

	/**
	 * Read data from R and compute beta and predict in 0.0.
	 * 
	 * @param filename
	 * @return true if test OK
	 * @throws IOException 
	 */
	public boolean testLWRSinglePoint()
			throws IOException {
		
//		Matrix tmp = new Matrix(2, 3, 0.0);
//		System.out.println(tmp.get(0,0));
//		System.out.println(tmp.getColumnDimension());
		
		// Read data
		Matrix Xs = RLink.readRDataFile("src/test/matX.Rdata");
		Matrix Ys = RLink.readRDataFile("src/test/matY.Rdata");
		
		// Prepare data
		ArrayList<double[]> xdata = new ArrayList<double[]>();
		ArrayList<Double> ydata = new ArrayList<Double>();
		for (int i = 0; i < Xs.getRowDimension(); i++) {
			xdata.add( Xs.getMatrix(i, i, 0, Xs.getColumnDimension()-1).getColumnPackedCopy() );
			ydata.add( Ys.get(i, 0));
		}
		double[] query = {0.0};
		
		// Prediction en 0.0 avec sigma=1.0
		LWRegression regLWR = new LWRegression();
		double pred = regLWR.predict(query, xdata, ydata );
		System.out.println("pred="+pred);
		// Selon T. Moinel
		System.out.println("pred="+JamaU.matToString(regLWR.doRegression(query, xdata, ydata )));
		
		// avec Sigma = 0.1;
		regLWR._sigma = 0.1;
		System.out.println("pred="+regLWR.predict(query, xdata, ydata ));
	
		return true; // it compiles
	}
	
	/**
	 * Test si les valeurs retournées par LWR.predict sont les même que R.
	 * @return
	 * @throws IOException 
	 */
	public boolean testLWRPredict()
			throws IOException {
		// Read data
		Matrix Xs = RLink.readRDataFile("src/test/matX.Rdata");
		Matrix Ys = RLink.readRDataFile("src/test/matY.Rdata");
		Matrix Xf = RLink.readRDataFile("src/test/matXfit.Rdata");
		Matrix Yf = RLink.readRDataFile("src/test/matYfit.Rdata");
		
		// Prepare data
		ArrayList<double[]> xdata = new ArrayList<double[]>();
		ArrayList<Double> ydata = new ArrayList<Double>();
		for (int i = 0; i < Xs.getRowDimension(); i++) {
			xdata.add( Xs.getMatrix(i, i, 0, Xs.getColumnDimension()-1).getColumnPackedCopy() );
			ydata.add( Ys.get(i, 0));
		}
		
		// Prepare output
		Matrix Ylwr = new Matrix(Xf.getRowDimension(),3); // Yfit, beta (2 coefs)
		Matrix Ytho = new Matrix(Xf.getRowDimension(),3); // Yfit, beta (2 coefs)
		
		LWRegression regLWR = new LWRegression();
		double[] query;
		for (int i = 0; i < Xf.getRowDimension(); i++) {
			query = Xf.getMatrix(i, i, 0, Xf.getColumnDimension()-1).getColumnPackedCopy();
			Ylwr.set(i, 0, regLWR.predict(query, xdata, ydata ));
			//System.out.println(JamaU.matToString(regLWR._beta));
			Ylwr.setMatrix(i, i, 1, 2, regLWR._beta.transpose());
			Ytho.set(i, 0, regLWR.doRegression(query, xdata, ydata ).get(0, 0) );
		}
		RLink.writeRDataFile(Ylwr, "src/test/matYlwr.RData");
		RLink.writeRDataFile(Ytho, "src/test/matYtho.RData");
		
		// Check that expected results are obtained
		boolean res=JamaU.isNearZero(Yf.minus(Ylwr), 0.000001);
		if (res == false ) {
			System.err.println("testLWRPredict bad predict");
			System.err.println("target MATRIX="+JamaU.matToString(Yf));
			System.err.println("read   MATRIX="+JamaU.matToString(Ylwr));
		}
		return res;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestLWR app = new TestLWR();
		app.run();
	}

}
