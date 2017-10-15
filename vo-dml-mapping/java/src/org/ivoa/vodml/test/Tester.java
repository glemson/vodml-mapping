package org.ivoa.vodml.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;


//import org.ivoa.util.FileUtils;
//import org.ivoa.util.LogUtil;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Tester {

	//~ Inner Classes ----------------------------------------------------------------------------------------------------
	
	  /**
	   * SAX ErrorHandler implementation to add validation exception to the given ValidationResult instance
	   * @see org.xml.sax.ErrorHandler
	   */
	  private final class CustomErrorHandler implements ErrorHandler {
	    //~ Members --------------------------------------------------------------------------------------------------------
	
	    /** validation result */
	    private ValidationResult result;
	
	    //~ Constructors ---------------------------------------------------------------------------------------------------
	    /**
	     * Public constructor with the given validation result
	     * @param pResult validation result to use
	     */
	    public CustomErrorHandler(final ValidationResult pResult) {
	      this.result = pResult;
	    }
	
	    //~ Methods --------------------------------------------------------------------------------------------------------
	    /**
	     * Wrap the SAX exception in an ErrorMessage instance added to the validation result
	     * @param se SAX parse exception 
	     * @see org.xml.sax.ErrorHandler#warning(SAXParseException)
	     */
	    public void warning(final SAXParseException se) {
	      result.getMessages().add(
	          new ErrorMessage(ErrorMessage.SEVERITY.WARNING, se.getLineNumber(), se.getColumnNumber(), se.getMessage()));
	    }
	
	    /**
	     * Wrap the SAX exception in an ErrorMessage instance added to the validation result
	     * @param se SAX parse exception 
	     * @see org.xml.sax.ErrorHandler#error(SAXParseException)
	     */
	    public void error(final SAXParseException se) {
	      result.getMessages().add(
	          new ErrorMessage(ErrorMessage.SEVERITY.ERROR, se.getLineNumber(), se.getColumnNumber(), se.getMessage()));
	    }
	
	    /**
	     * Wrap the SAX exception in an ErrorMessage instance added to the validation result
	     * @param se SAX parse exception 
	     * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
	     */
	    public void fatalError(final SAXParseException se) {
	      result.getMessages().add(
	          new ErrorMessage(ErrorMessage.SEVERITY.FATAL, se.getLineNumber(), se.getColumnNumber(), se.getMessage()));
	    }
	  }

	public static void main(String[] args) throws Exception
	{
		try {

			Tester tester = new Tester();
//			tester.testAllModels();
			
//			tester.testVODML("C:/workspaces/eclipse_VO-URP/vo-dml/models/vo-dml/VO-DML.vo-dml.xml");

//			tester.testCustom("file:///C:/workspaces/eclipse-luna-4.4.1/vo-urp-etc/vo-dml/doc/samples/votable/VOTable-1.3_vodml4a.xsd"
//					,"C:/workspaces/eclipse-luna-4.4.1/vo-urp-etc/vo-dml/doc/samples/votable/VOTable_Prop4a.xml");
//			tester.testCustom("file:///C:/workspaces/eclipse-luna-4.4.1/vo-urp-etc/vo-dml/doc/samples/votable/VOTable-1.3_vodml4b.xsd"
//					,"C:/workspaces/eclipse-luna-4.4.1/vo-urp-etc/vo-dml/doc/samples/votable/VOTable_Prop4b.xml");
			tester.testCustom("file:///C:/workspaces/eclipse-luna-4.4.1/vo-urp-etc/vo-dml/xsd/ext/VOTable-1.3_vodml.xsd"
					,"C:/Users/gerard/Desktop/sdss7_at_ari.xml");
			
			//			String schema="C:\\workspaces\\eclipse_VO-URP\\vo-dml\\doc\\examples\\2mass_concat.votable.DM.xml";
//			String xml="C:\\workspaces\\eclipse_VO-URP\\vo-dml\\models\\source/SourceDM.vo-dml.xml";
//			xml="C:\\workspaces\\eclipse_VO-URP\\vo-dml\\models\\vo-dml/VO-DML.vo-dml.xml";
//			if(tester.testVODML(xml))
//				System.out.printf("%s is a valid Vo-DML/XML model\n",xml);
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void runFile() throws Exception
	{
		SchemaFactory  schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		String surl = "C:/workspaces/eclipse_VO-URP/vo-dml/xsd/vo-dml.xsd";
		surl = "https://volute.googlecode.com/svn/trunk/projects/dm/vo-dml/xsd/vo-dml.xsd";
//		surl="vo-urp.xsd";
        final URL url = new File(surl).toURI().toURL();

		Schema schema = schemaFactory.newSchema(url);
		if(schema == null) {
			System.out.println("Error, schema is NULL");
			return;
		}
		validate(schema, "C:/workspaces/eclipse_VO-URP/vo-dml/models/profile/IVOA_Profile.vo-dml.xml");
		validate(schema, "C:/workspaces/eclipse_VO-URP/vo-dml/models/tap/TAP.vo-dml.xml");
		validate(schema, "C:/workspaces/eclipse_VO-URP/vo-dml/models/stc/STC.vo-dml.xml");
		validate(schema, "C:/workspaces/eclipse_VO-URP/vo-dml/models/characterization/Characterization.vo-dml.xml");
		validate(schema, "C:/workspaces/eclipse_VO-URP/vo-dml/models/spectrum/SpectrumDM.vo-dml.xml");
		validate(schema, "C:/workspaces/eclipse_VO-URP/vo-dml/models/photdm/PhotDM.vo-dml.xml");
		validate(schema, "C:/workspaces/eclipse_VO-URP/vo-dml/models/sample/Sample.vo-dml.xml");
	}

	public boolean testVOTable(URL votableFile) throws Exception
	{
		return validate(getVOTableSchema(), votableFile);
	}
	public boolean testVODML(String vodmlFile) throws Exception
	{
		return validate(getVODMLSchema(), vodmlFile);
	}
	public boolean testCustom(String schemaFile, String xmlDoc)  throws Exception
	{
		boolean isOK = validate(getSchema(schemaFile), xmlDoc);
		if(!isOK)
			System.out.printf("ERROR: %s is not a valid %s model\n",xmlDoc, schemaFile);
		else
			System.out.printf("SUCCES: %s is a valid %s model\n",xmlDoc, schemaFile);
		return isOK;
	}
	public boolean testVODML(URL vodmlFile) throws Exception
	{
		boolean isOK = validate(getVODMLSchema(), vodmlFile);
		if(!isOK)
			System.out.printf("%s is not a valid VO-DML model\n",vodmlFile);
		else
			System.out.printf("%s is a valid VO-DML model\n",vodmlFile);
		return isOK;
	}
	private Schema getVODMLSchema()  throws Exception
	{
		return getSchema("http://volute.googlecode.com/svn/trunk/projects/dm/vo-dml/xsd/vo-dml.xsd");
	}
	private Schema getVOTableSchema()  throws Exception
	{
		URL url = ClassLoader.getSystemResource(".");
		String loc = url.toString()+"/../xsd/ext/VOTable-1.4_vodml.xsd";
		return getSchema(loc);
//		return getSchema("https://volute.googlecode.com/svn/trunk/projects/dm/vo-dml/doc/samples/votable/VOTable-1.3_vodml.xsd");
	}
	private Schema getSchema(String surl)  throws Exception
	{
		SchemaFactory  schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        final URL url = new URL(surl);

		Schema schema = schemaFactory.newSchema(url);
		if(schema == null) {
			throw new IllegalStateException("Error, schema is NULL");
		}
		return schema;
	}
	public void testAllModels() throws Exception
	{
		testVODML(new URL("http://volute.googlecode.com/svn/trunk/projects/dm/vo-dml/models/ivoa/IVOA.vo-dml.xml"));
		testVODML(new URL("http://volute.googlecode.com/svn/trunk/projects/dm/vo-dml/models/photdm-alt/PhotDM-alt.vo-dml.xml"));
		testVODML(new URL("http://volute.googlecode.com/svn/trunk/projects/dm/vo-dml/models/vo-dml/VO-DML.vo-dml.xml"));
		testVODML(new URL("http://volute.googlecode.com/svn/trunk/projects/dm/vo-dml/models/source/SourceDM.vo-dml.xml"));
	}
	
	private boolean validate(Schema schema, String file) throws Exception
	{
		InputStream in = new FileInputStream(file);
		return validate(schema, in);
	}
	private boolean validate(String schemaFile, String file) throws Exception
	{
		System.out.printf("Validating file '%s' vs schema '%s'\n",schemaFile, file);
		return validate(getSchema(schemaFile),file);
	}	
	private boolean validate(Schema schema, URL url) throws Exception
	{
		InputStream in = url.openStream();
		return validate(schema,in);
	}	
	private boolean validate(Schema schema, InputStream in) throws Exception
	{
		Validator val = schema.newValidator();
		ValidationResult result = new ValidationResult();
	    val.setErrorHandler(new CustomErrorHandler(result));
		final Source source = new StreamSource(in);
	    try {
	        // 5. Check the document
			val.validate(source);
	      } catch (final SAXException se) {
	        // intercepted by CustomErrorHandler
	        result.getMessages().add(new ErrorMessage(ErrorMessage.SEVERITY.FATAL, -1, -1, se.getMessage()));
	      } catch (final IOException ioe) {
	        // intercepted by CustomErrorHandler
	        result.getMessages().add(new ErrorMessage(ErrorMessage.SEVERITY.FATAL, -1, -1, ioe.getMessage()));
	      }
	    if(result.getMessages() == null || result.getMessages().size() == 0) {
//            System.out.println("No errors found");
	    	return true;
	    }
	    else
	    {
            for (final ErrorMessage em : result.getMessages()) {
              System.out.println(em.toString());
            }
            return false;
	    }

	}
	public static File getFile(String name) throws FileNotFoundException
	{
		File file = new File(name);
		if(file.exists())
			return file;
		URL url = ClassLoader.getSystemResource(name);
		if(url != null)
		{
			file = new File(url.getFile());
			return file;
		}
		else
			throw new FileNotFoundException("Unable to resolve file name '"+name+"'");
	}
}
