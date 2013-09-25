/**
 * 
 */
package simulator;


import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;

import model.Command;
import model.CommandSequence;
import model.CompleteArm;
import model.Consignes;
import viewer.SCompleteArm;

import org.kohsuke.args4j.Option;

import utils.IParameters;
import utils.ParameterFactory;

import Jama.Matrix;

/**
 * @author alain.dutech@loria.fr
 */
public class XP0_TestConsigne extends XPDefault {

	/** Agent */
	Agent _agent = new Agent();
	/** World : CompleteArm */
	public CompleteArm _world;
	/** Observer as String of the World */
	SCompleteArm _armV;

	/** Starting Angle of first joint */
	@Option(name="--ang0",aliases={"-a0"},usage="Angle of first joint, degree")
	double _ang0=0.0;
	/** Starting Angle of second joint */
	@Option(name="--ang1",aliases={"-a1"},usage="Angle of first joint, degree")
	double _ang1=0.0;
	
	int _dimMuscle = 0;
	
	DecimalFormat df1_3 = new DecimalFormat( "0.000" );
	
	/**
	 * Creation of Agent,World, possibility to add to ParameterFactory.
	 */
	public XP0_TestConsigne(ParameterFactory param) {
		super(param);
		
		_world = new CompleteArm();
		_armV = new SCompleteArm(_world);
		_world.addObserver(_armV);
		
		_agent = new Agent();
		_dimMuscle = _world.getArrayNeuroControlers().length;
		
		if (param != null) {
			param.addObjectWithParameters(_agent);
		}
	}
	
	/* (non-Javadoc)
	 * @see simulator.IXP#init()
	 */
	@Override
	public void init() {
		super.init();
		
		_agent._cons = new Consignes(_dimMuscle);
		// Initialize Commandes
		for (int i=0; i < _dimMuscle; i++) {
			CommandSequence cs = _agent._cons.get(i);
			if (i == _agent._indexMuscle) {
				cs.add(new Command( 0, _agent._consVal));
				cs.add(new Command( _agent._consTime, 0));
			}
			else {
				cs.add(new Command(0,0));
			}
		}
	}
	/* (non-Javadoc)
	 * @see simulator.IXP#reset()
	 */
	@Override
	public void reset(int indexXP) {
		super.reset(indexXP);
		
		// Position with no speed.
		_world.setup(Math.toRadians(_ang0), Math.toRadians(_ang1));
		// Set some speed
		_world.getArm().setArmSpeed(new double[] {0,0});	
		
		// Logging
		if (_logScreen ) {
			System.out.println(String.format("%8s", "time")+"\t"+_armV.explainStr);
			System.out.println(df3_5.format(0.0)+"\t"+_armV.viewStr);
		}
		if (_logFilename != "") {
			_logFile.writeLine(String.format("%8s", "time")+"\t"+_armV.explainStr);
			_logFile.write(df3_5.format(0.0)+"\t"+_armV.viewStr);
		}
	}
	/* (non-Javadoc)
	 * @see simulator.IXP#updateAgents(double)
	 */
	@Override
	public void updateAgents( double time ) {
	}
	/* (non-Javadoc)
	 * @see simulator.IXP#updateWorld(double, double)
	 */
	@Override
	public void updateWorld( double time, double deltaT ) {
		// Applique les consignes sur le bras
		Matrix u = new Matrix(1,_dimMuscle, 0);
		for (int i = 0; i < _dimMuscle; i++) {
			CommandSequence cs = _agent._cons.get(i);
			// la valeur de la consigne est copiée dans le vecteur u
			u.set(0,i, cs.getValAtTimeFocussed(time));
		}
		_world.applyCommand(u, deltaT);
		
		if (_logScreen) {
			System.out.println(df3_5.format(time)+"\t"+_armV.viewStr);
		}
		if (_logFile != null) {
			_logFile.write(df3_5.format(time)+"\t"+_armV.viewStr);
		}
	}
	/* (non-Javadoc)
	 * @see simulator.IXP#wrapUp(double)
	 */
	@Override
	public void wrapUp(double time) {
		super.wrapUp(time);
	}
	/* (non-Javadoc)
	 * @see simulator.IXP#end()
	 */
	@Override
	public void end() {
	}
	/* (non-Javadoc)
	 * @see simulator.XPDefault#setParamString()
	 */
	@Override
	void setParamString() {
		// Set up a String for logFile header
		_paramString = "-a0_"+_ang0+"-a1_"+_ang1+
				"-im"+_agent._indexMuscle+
				"-cv"+df1_3.format(_agent._consVal)+
				"-ct"+df1_3.format(_agent._consTime);
	}
	@Override
	public void printValues(OutputStream out) {
		super.printValues(out);
		PrintStream pout = new PrintStream(out);

		pout.println("#     ang0="+_ang0+"  ang1="+_ang1);
	}
	
	class Agent implements IParameters {
		
		/** Index of Muscle for Consigne */
		@Option(name="--indexM",aliases={"-im"},usage="Index of Muscle for Consigne")
		int _indexMuscle=0;
		/** Apply Consigne value */
		@Option(name="--consVal",aliases={"-cv"},usage="Consigne Value to apply")
		double _consVal=0.0;
		/** Apply Consigne for time in s*/
		@Option(name="--consTime",aliases={"-ct"},usage="Consigne applied for Time")
		double _consTime=0.300;
		
		/** The Consigne */
		Consignes _cons;
		
		@Override
		public void printValues(OutputStream out) {
			PrintStream pout = new PrintStream(out);
			pout.println("### "+getClass().getName());
			pout.println("#     indexMuscle="+_indexMuscle);
			pout.println("#     consVal="+_consVal+"  consTime="+_consTime);
		}

	}


	
}
