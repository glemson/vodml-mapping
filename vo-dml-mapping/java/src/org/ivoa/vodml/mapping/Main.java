package org.ivoa.vodml.mapping;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;

import org.ivoa.vodml.mapping.Main;
import org.ivoa.vodml.MongoDBRegistry;
import org.ivoa.vodml.RemoteVODMLRegistry;
import org.ivoa.vodml.VODMLRegistry;
import org.ivoa.vodml.VODML_JAXBHelper;
import org.ivoa.vodml.mapping.vodmli.VODMLIManager;
import org.ivoa.vodml.xsd.jaxb.Model;


public class Main {

	private VODMLRegistry vodmlReg = null;

	public static void main(String[] args) throws Exception
		{
		if(args.length < 3 || args.length > 5)
		{
			printUsage();
			return;
		}
			VODMLRegistry vodmlReg = null;
			if("M".equals(args[1]))
				vodmlReg = new MongoDBRegistry("localhost", 27017);
			else // if("R".equals(args[1]))
				vodmlReg = new RemoteVODMLRegistry();
			Main main = new Main();
			main.vodmlReg = vodmlReg;
			if("I".equals(args[0])){
				if(args.length != 4){
					printUsage();
					return;
				}
				main.interpretVOTable(args[2], args[3]);
			}
			else if("V".equals(args[0]))
				main.validateVOTable(args[2]);
			else
				printUsage();
		}

	private static void printUsage()
	{
		System.out.println("usage: java org.ivoa.vodml.mapping.Main <mode=I|V> <vodml-reg=M|R><votable-file> [<mapping-file> <output-file>] ");
		System.out.println("\tmode = V: validate");
		System.out.println("\tmode = I: interpret");
		System.out.println("\tmapping file and output-file only required for mode=I");
	}

	public void testVODML(String f) throws Exception
	{
		VODML_JAXBHelper jaxb = VODML_JAXBHelper.jaxb;
		Model m = jaxb.parseVODML(new File(f));
		System.out.println(m.getName());
	}

	
	public void interpretVOTable(String filename, String mappingFile) throws Exception
	{
		File f_vot = new File(filename);
		URL url = f_vot.toURI().toURL();
		File f_mf = new File(mappingFile);
		URL url_m = f_mf.toURI().toURL();
		
		VODMLIManager vodmli = JAXBVOTableInterpreter.interpret(url, url_m, vodmlReg);
//		VODMLIManager vodmli = VOTableInterpreter.interpret(url, url_m);
		// report on what was discovered so far (using only directly represented groups)

		String outFile = filename+".vo-dml.xml";
		FileWriter out = new FileWriter(outFile);
		out.write(vodmli.serialize1());
		out.flush();
		out.close();
		System.out.printf("Success! Wrote result to '%s'\n",outFile);
	}
	public void validateVOTable(String filename) throws Exception
	{
		File f_vot = new File(filename);
		URL url = f_vot.toURI().toURL();
		
		try {
			if (JAXBVOTableInterpreter.validate(url, vodmlReg))
				System.out.printf("SUCCES! VOTable in '%s' is valid wrt VO-DML mapping\n",filename);
			else
				System.out.printf("ERRORS were found while validating VOTable in '%s'\n",filename);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
