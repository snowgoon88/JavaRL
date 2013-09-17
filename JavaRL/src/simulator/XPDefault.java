package simulator;

import java.text.DecimalFormat;

import org.kohsuke.args4j.Option;

import utils.Log;
import utils.ParameterFactory;

public class XPDefault {

	/** log info from XP */
	@Option(name="-ls",aliases={"--logScreen"},usage="log info from XP on Screen")
	public boolean _logScreen;
	/** File for logging */
	@Option(name="-lf",aliases={"--logFile"},usage="File for logging XP")
	public String _logFilename = "";
	String _paramString ="";
	
	Log<String> _logFile = null;
	DecimalFormat df3_5 = new DecimalFormat( "000.00000" );
	
	/**
	 * Creation, this is added to the ParameterFactory (if not null).
	 * @param param
	 */
	public XPDefault(ParameterFactory param) {
		if (param != null) {
			param.addObjectWithParameters(this);
		}
	}
	
	/**
	 * Object that need one initialization.
	 * Call setParamString for initialization.
	 */
	public void init() {
		setParamString();
	}
	/**
	 * Set World in a starting state : position and consigne.
	 * At start of run.
	 * 
	 * if Log : create LogFile _logFilename+_paramString+"_"+indexXP+".data"
	 */
	public void reset(int indexXP) {
		if (_logFilename != "") {
			_logFile = new Log<String>(_logFilename+_paramString+"_"+indexXP+".data");
			_logFile.writeLine("#"+_paramString);
		}
	}
	/**
	 * Perception and Decision
	 * @param time of the Simulator
	 */
	public void updateAgents(double time) {
		
	}
	/**
	 * Use agent decisions to change the state of the World
	 * for the next deltaT simulation time.
	 * @param time of the Simulator
	 * @param deltaT need world Updated at time+deltaT
	 */
	public void updateWorld(double time, double deltaT) {
		
	}
	/**
	 * wrapUp world and agent, at end of one run.
	 * 
	 * if Log, close Log file.
	 */
	public void wrapUp(double time) {
		if (_logFile != null) {
			_logFile.close();
		}
	}
	/**
	 * Called at the end of the XP.
	 */
	public void end() {
		
	}
	
	/**
	 * Set up the parameter string that will name the file
	 * and be printed in log file header.
	 */
	void setParamString() {
		_paramString = "-noParameter";
	};

}