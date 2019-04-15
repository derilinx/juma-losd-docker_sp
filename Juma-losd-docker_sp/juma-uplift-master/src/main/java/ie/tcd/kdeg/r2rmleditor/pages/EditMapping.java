package ie.tcd.kdeg.r2rmleditor.pages;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.FileWriter;
import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.lang.StringBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.hibernate.Session;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;

import ie.tcd.kdeg.r2rmleditor.entities.Mapping;
import ie.tcd.kdeg.r2rmleditor.entities.MappingExecution;

public class EditMapping extends BasePage {

	@Property
	private Mapping mapping;

	@Inject
	private Session session;

	// fields for changing the information
	@Component private Zone informationZone;

	// fields for changing the configuration
	@Component private Zone configurationZone;
	
	// fields for changing the mapping
	@Component private Zone editorZone;
	
	// fields for adding source file
	@Component private Zone sourceFileZone;

	@Environmental
  private JavaScriptSupport javaScriptSupport;

	@Inject
	private AlertManager alertManager;

	private boolean run;

	void onActivate(long id) {
		// Get the mapping when page loads...
		mapping = (Mapping) session.get(Mapping.class, id);
	}
	
	Object[] onPassivate() {
        return new Object[] { mapping.getId() };
    }

	@CommitAfter
	public Object onSuccessFromEditInformation(Mapping mapping) {
		session.update(mapping);
		return informationZone.getBody();
	}
	
	@CommitAfter
	public Object onSuccessFromEditConfiguration(long id) {
		session.update(mapping);
		return configurationZone.getBody();
	}
	
	// Source File handler
	@CommitAfter
	public String onSuccessFromEditSourceFile(Mapping mapping) {
		
		String outString = "";
		boolean sourceFile = false;
		Scanner in2 = null;
		
		// Get CSV data from file on the web
		try {
			String url = mapping.getFileURL();
			String encodedURL = url.replace(" ", "%20");

			URL fileURL = new URL(encodedURL);
			Scanner in = new Scanner(fileURL.openStream(), "UTF-8");
			StringBuilder sb = new StringBuilder();
			while(in.hasNextLine()) {
				sb.append(in.nextLine());
				sb.append("\n");
			}
			in.close();
			outString = sb.toString();
		}
		catch (Exception e) {}
		
		
		//Make directory for user
		File directory = new File("userfiles/"+mapping.getCreator());
		if( !directory.exists() ){
			directory.mkdirs();
		}
		
		//Make directory for mapping
		File map_directory = new File("userfiles/"+mapping.getCreator()+"/"+mapping.getId());
		if( !map_directory.exists() ){
			map_directory.mkdirs();
		}
		
		try {
			FileWriter writer = new FileWriter("userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/sourcedata.csv");
			writer.write(outString);
			writer.close();
		}
		catch (Exception e) {}
		
		try {
			in2 = new Scanner(new FileReader("userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/sourcedata.csv"));

			if(in2.hasNextLine() ){
				if( !in2.nextLine().equals("")){
					alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "File successfully downloaded");
					sourceFile = true;
				}
				else {
					alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Error: File did not successfully download, ensure URL is correct and the data at the URL is well formed CSV data");
					sourceFile = false;
				}
			}
			else {
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Error: File did not successfully download, ensure URL is correct and the data at the URL is well formed CSV data");
				sourceFile = false;
			}
			in2.close();
		}
		catch (Exception e) {
			if(in2!=null)
				in2.close();				
		}
		
		return null;
	}
	
	@CommitAfter
	public Object onSuccessFromSaveMapping(long id) {
		mapping.setLastSave();
		session.update(mapping);
		if(run) {
			// Log execution						
			String output = saveMappingExecution(mapping).getOutput();
			mapping.setOutput(StringEscapeUtils.escapeHtml(output));
		}
		alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "Mapping Successfully Saved");
		
		return editorZone.getBody();
	}
	
	private MappingExecution saveMappingExecution(Mapping mapping) {
		String output = null;
		String sample_output = "";
		Scanner in = null;
		MappingExecution mappingExecution = new MappingExecution();
		
		File map2_directory = new File("userfiles/"+mapping.getCreator()+"/"+mapping.getId());
		if( !map2_directory.exists() ){
			map2_directory.mkdirs();
		}
		
		File f = new File("src/main/webapp/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/RENAME_ME_T.ttl");//////////
		File f2 = new File("src/main/webapp/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/RENAME_ME.ttl");//////////
		
		try {
			if( f.exists() ){
				f.delete();
			}
			if( f2.exists() ){
				f2.delete();
			}
		}
		catch (Exception e) {}
		
		
		try {
			
			//Make directory for user that will hold RDF data
			File directory = new File("userfiles/"+mapping.getCreator());
			if( !directory.exists() ){
				directory.mkdirs();
			}
			
			File map3_directory = new File("userfiles/"+mapping.getCreator()+"/"+mapping.getId());
			if( !map3_directory.exists() ){
				map3_directory.mkdirs();
			}

			// write mapping that can be used in R2RML processor
			FileUtils.writeStringToFile(new File("userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/"+mapping.getId()+"_mapping.ttl"), mapping.getR2rmlMapping());
			
			//Make directory for outputfile to be downloaded
			File directory2 = new File("src/main/webapp/userfiles/"+mapping.getCreator()+"/"+mapping.getId());
			if( !directory2.exists() ){
				directory2.mkdirs();
			}
			
			String dir_path = System.getProperty("user.dir");
			dir_path = dir_path.replace("\\", "/");
			
			
			// Create Config File for R2RML Engine
			try {
				FileWriter writer2 = new FileWriter("userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/config.properties");
				writer2.write("mappingFile = "+dir_path+"/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/"+mapping.getId()+"_mapping.ttl\n");
				writer2.write("CSVFiles = "+dir_path+"/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/sourcedata.csv\n");
				writer2.write("outputFile = "+dir_path+"/src/main/webapp/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/RENAME_ME_T.ttl\n");
				writer2.write("format = TURTLE\n");
				writer2.close();
			}
			catch (Exception e) {}
			
			//Execute Mapping
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec("java -Xmx4G -jar "+dir_path+"/r2rml_updated/target/r2rml.jar "+dir_path+"/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/config.properties");
			
			pr.waitFor();
	
			try {
				if( f.exists() ){
					in = new Scanner(new FileReader("src/main/webapp/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/RENAME_ME_T.ttl"));
				
					StringBuilder sb = new StringBuilder();
					String temp = "";
				
					FileWriter writer_out = new FileWriter(dir_path+"/src/main/webapp/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/RENAME_ME.ttl");
				
					int i = 0;
					while( in.hasNextLine() ) {
						temp = in.nextLine();
						temp = temp.replaceAll("%20", "_");
						sb.append(temp);
						sb.append("\n");
					
						if( i <= 500 ){
							sample_output = sb.toString();
						}
						
						i++;
					}
					in.close();
				
					writer_out.write( sb.toString() );
					writer_out.close();
				}
			}
			catch (Exception e) {
				if(in!=null)
					in.close();				
			}
			
		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			output = errors.toString();
		}
		
		if( f.exists() ){
			in.close();
			f.delete();
		}
	
		if ( sample_output.isEmpty() ){
			mapping.setDownloadLink("ERROR");
			sample_output = "An Error occurred during the conversion process, please ensure:\n\n1) That your CSV file is correctly formed.\n\n2) You have input the correct URL under the \"Source Data\" tab and have\n   clicked the \"Load File\" button.\n\n3) The \"table\" block of the Mapping contains \"SOURCEDATA\".\n\n4) Check for any syntax errors in the mapping. \n\n5) If a column heading (from the source CSV file) contains any spaces in it,\n then ensure that any reference to the column heading in the mapping appears\n exactly how it is written in the CSV source file.\n\n6) If a column heading (from the source CSV file) contains no spaces in it,\n then ensure that any reference to the column heading in the mapping appears\n in UPPERCASE";
			mappingExecution.setOutput(sample_output);
		}
	  else {
			mapping.setDownloadLink("<a href=\"/juma-editor/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/RENAME_ME.ttl\">Download RDF as file</a>");
			mappingExecution.setOutput(sample_output);
		}

		return mappingExecution;
	}

	void onSelectedFromRun() {
	     run = true;
	}
	
	void onShowModal(){
		if(run)
			javaScriptSupport.addScript("$('#showModal').click();"); 
	}
	
}
