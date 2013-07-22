/**
 * 
 */
package simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Paremeters for the Simulator application
 * 
 * @author alain.dutech@loria.fr
 *
 */
public class Parameters {

	/** Max simulation time allowed */
	@Option(name="-mt",aliases={"--maxTime"},usage="Max simulation time allowed")
	public double maxTime = 5.0;
	/** Update interval for Simulation */
	@Option(name="-dt",aliases={"--deltaTime"},usage="Update interval for Simulation")
	public double deltaTime = 0.010;
	
	/**
	 * Parse from the Command Line.
	 * @param args
	 * @return true if all Parameters set.
	 */
	public boolean parseFromCLI(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
        try {
                parser.parseArgument(args);
        } catch (CmdLineException e) {
            // handling of wrong arguments
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
        return true;
	}
	/**
	 * Parse from a given File
	 * @param filename
	 * @return true if all Parameters set
	 */
	public boolean parseFromFile(String filename) {
		// Open file, use Tokenizer to build a String[] then parse
		// Open file for reading
		FileReader aFile;
		try {
			aFile = new FileReader( filename );
		} catch (FileNotFoundException e) {
			System.err.println("Parameters.parseFromFile : "+filename+" NOT FOUND");
			return true; // using default
		}
		BufferedReader reader = new BufferedReader( aFile );
		
		// Read all the Token
		ArrayList<String> args = new ArrayList<String>();
		
		String lineRead;
		try {
			lineRead = reader.readLine();
			while (lineRead != null) {
				if (lineRead.startsWith("#") == false) {
					StringTokenizer st = new StringTokenizer(lineRead);
					while (st.hasMoreElements()) {
						String tok = (String) st.nextElement();
						args.add(tok);
					}
				}
				lineRead = reader.readLine();
			}
			reader.close();
	        aFile.close();
		} catch (IOException e) {
			System.err.println("Parameters.parseFromFile : IOException in "+filename);
			return true; // using default
		}
		
		CmdLineParser parser = new CmdLineParser(this);
        try {
                parser.parseArgument(args);
        } catch (CmdLineException e) {
            // handling of wrong arguments
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
        
        return true;	
	}
	
}
