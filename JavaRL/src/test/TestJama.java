/**
 * 
 */
package test;

import utils.JamaU;
import Jama.Matrix;

/**
 * @author alain.dutech@loria.fr
 *
 */
public class TestJama {

	void testMult() {
		Matrix A = new Matrix(new double [][] {{1,2},{3,4},{5,6}});
		System.out.println("A=\n"+JamaU.matToString(A));
		Matrix b = new Matrix(new double [][] {{0.5}, {2}});
		System.out.println("b=\n"+JamaU.matToString(b));
		
		Matrix C = A.times(b);
		System.out.println("A=\n"+JamaU.matToString(A));
		System.out.println("C=\n"+JamaU.matToString(C));
		
		Matrix d = new Matrix(new double [][] {{0.5},{0.5},{2}});
		Matrix E = A.transpose().times(d);
		System.out.println("A=\n"+JamaU.matToString(A));
		System.out.println("E=\n"+JamaU.matToString(E));
	}
	double testMachineEpsilon() {
		double machEps = 1.0f;

		do
			machEps /= 2.0f;
		while ((double) (1.0 + (machEps / 2.0)) != 1.0);

		return machEps;
	}
	void testPinv() {
		Matrix A = new Matrix(new double [][] {
				{20,5.75,18.125,0,0,0},
				{5.75,113.75,387.5,0,0,0},
				{18.125,387.5,1695.312,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0}} );
		System.out.println("A=\n"+JamaU.matToString(A));
		
		long aTime = System.currentTimeMillis();
		Matrix pinvA = JamaU.pinv(A);
		long bTime = System.currentTimeMillis();
		System.out.println("Tps = "+(bTime-aTime));
		System.out.println("pinvA=\n"+JamaU.matToString(pinvA));
		
		Matrix resL = pinvA.times(A);
		Matrix resR = A.times(pinvA);
		System.out.println("L=\n"+JamaU.matToString(resL));
		System.out.println("R=\n"+JamaU.matToString(resR));
		
		aTime = System.currentTimeMillis();
		Matrix pinv2A = JamaU.pinv2(A);
		bTime = System.currentTimeMillis();
		System.out.println("Tps = "+(bTime-aTime));
		System.out.println("pinv2A=\n"+JamaU.matToString(pinv2A));
		
		Matrix resL2 = pinv2A.times(A);
		Matrix resR2 = A.times(pinv2A);
		System.out.println("L2=\n"+JamaU.matToString(resL2));
		System.out.println("R2=\n"+JamaU.matToString(resR2));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestJama app = new TestJama();
		//app.testMult();
		//System.out.println("eps="+app.testMachineEpsilon());
		app.testPinv();

	}

}
