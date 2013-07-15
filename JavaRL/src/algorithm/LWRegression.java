/**
 * 
 */
package algorithm;

import java.util.ArrayList;

import utils.JamaU;

import Jama.Matrix;

/**
 * Locally Weighted Regression
 * 
 * @author alain.dutech@loria.fr (Largely based on code from Thomas MOINEL)
 */
public class LWRegression {
	
	public double _sigma = 1.0;
	public Matrix _beta;
	
	/**
	 * Compute the predicted value at the point 'query' using (Xsamples,YSamples)
	 * as samples to be fitted with a weight 'weightFunction( dist(query,sample[i])'
	 * This is MONO valued.
	 * 
	 * @param query Query point 
	 * @param Xsamples List of X ccordinates of samples
	 * @param Ysamples List of Y value of samples
	 * @return the predicted value at the query point
	 */
	public double predict( double[] query, 
			ArrayList<double[]> Xsamples, ArrayList<Double> Ysamples ) {
		
		int dimX = query.length;
		
		// Matrix xq
		Matrix xq = new Matrix(1,dimX+1, 1.0);
		for (int j = 0; j < query.length; j++) {
			xq.set(0, j, query[j]);
		}
		
		// Matrix X and Y, and W
		Matrix X = new Matrix(Xsamples.size(), dimX+1, 1.0);
		Matrix Y = new Matrix(Ysamples.size(), 1);
		Matrix W = new Matrix(Xsamples.size(), Xsamples.size(), 0.0);
		for (int i = 0; i < Xsamples.size(); i++) {
			double[] xSample = Xsamples.get(i);
			double ySample = Ysamples.get(i);
			for (int j = 0; j < xSample.length; j++) {
				X.set(i, j, xSample[j]);
			}
			Y.set(i, 0, ySample);
			double dist_qX = X.getMatrix(i, i, 0, dimX).minusEquals(xq).normF();
			W.set(i, i, weightFunction( dist_qX ));
		}
		
		// Computation
		Matrix WX = W.times(X);
		Matrix V = W.times(Y);
		_beta = JamaU.pinv2( WX.transpose().times(WX)).times(WX.transpose()).times(V);
		Matrix hatY = xq.times(_beta);
		
		return hatY.get(0,0);
	}
	
	private double weightFunction( double d) {
		return Math.exp( - d*d/_sigma/_sigma);
	}

	/* from Thomas MOINEL */
	public Matrix doRegression(double[] query,
			ArrayList<double[]> Xsamples, ArrayList<Double> Ysamples ) {
		int dim_x = query.length+1;
		//int dim_y = query.getCommand().getColumnDimension();
		int dim_y = 1;
		int nbVector = Xsamples.size();
		// init martix
		Matrix xq_p = new Matrix(query, 1);
		Matrix xq = new Matrix(1, dim_x, 1.0);
		xq.setMatrix(0, 0, 0, dim_x-2, xq_p);
		Matrix X = new Matrix(nbVector, dim_x, 1.0);
		Matrix w = new Matrix(nbVector, dim_x);
		Matrix wX;
		Matrix Y = new Matrix(nbVector, dim_y);

		for (int i = 0; i < nbVector; i++) {
			double[] vec = Xsamples.get(i);
			for (int j = 0; j < dim_x-1; j++) {
				X.set(i, j, vec[j]);
			}
			X.set(i, dim_x-1, 1.0);
			//Y.setMatrix(i, i, 0, dim_y - 1, vicinity[i].getCommand());
			Y.set(i, 0, Ysamples.get(i));
			
			double d_i = X.getMatrix(i, i, 0, dim_x - 1).minusEquals(xq)
					.norm2();
			d_i = f(d_i);
			w.setMatrix(i, i, 0, dim_x - 1, new Matrix(1, dim_x, d_i));
		}
		wX = X.arrayTimes(w).transpose();

		//Matrix Lambda = getLambdaMatrix(dim_x, dim_x, 0.5);
		//Matrix P = (wX.times(X).plus(Lambda)).inverse();
		Matrix P = JamaU.pinv2(wX.times(X));
		_beta = P.times(wX).times(Y);
		Matrix yq = xq.times(_beta);
		
		//checkBounds(yq);

		return yq;
	}

	private void checkBounds(Matrix yq) {
		for (int i = 0; i < yq.getColumnDimension(); i++) {
			yq.set(0, i, Math.min(Math.max(yq.get(0, i), 0.0), 1.0));
		}
	}
	public double f(double d) {
		return 1/(d*d);
	}
}
