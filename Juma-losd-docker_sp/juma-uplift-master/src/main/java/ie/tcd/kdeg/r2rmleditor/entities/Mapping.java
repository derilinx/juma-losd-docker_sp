package ie.tcd.kdeg.r2rmleditor.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.apache.tapestry5.beaneditor.Validate;
import org.hibernate.annotations.Type;

@Entity
public class Mapping extends BaseEntity {

	@Validate("required")
	@Type(type="text")
	private String description;

	@Validate("required")
	private String title;
	
	private String connectionURL = null;
	private String user = null;
	private String password = null;
	private String mappingFile = null;
	private String outputFile = null;
	private String format = null;
	private String baseIRI = null;
	private String fileURL = null;
	private boolean filePerGraph = false;
	private Date lastSave = null;
	
	@NonVisual
	@Column(columnDefinition="text")
	@Type(type="text")
	private String XML = null;
	
	@Transient
	private String r2rmlMapping = null;

	@Transient
	private String output = null;
	
	@Transient
	private String downloadLink = null;
	
	//@Transient
	//private Date lastSave = null;
	
	public String getDownloadLink() {
		return downloadLink;
	}
	
	public void setDownloadLink(String dl) {
		this.downloadLink = dl;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConnectionURL() {
		return connectionURL;
	}

	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}
	
	public String getFileURL() {
		return fileURL;
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMappingFile() {
		return mappingFile;
	}

	public void setMappingFile(String mappingFile) {
		this.mappingFile = mappingFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getBaseIRI() {
		return baseIRI;
	}

	public void setBaseIRI(String baseIRI) {
		this.baseIRI = baseIRI;
	}

	public boolean isFilePerGraph() {
		return filePerGraph;
	}

	public void setFilePerGraph(boolean filePerGraph) {
		this.filePerGraph = filePerGraph;
	}

	public String getXML() {
		return XML;
	}

	public void setXML(String xML) {
		XML = xML;
	}

	public String getR2rmlMapping() {
		return r2rmlMapping;
	}

	public void setR2rmlMapping(String r2rmlMapping) {
		this.r2rmlMapping = r2rmlMapping;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
	
	public Date getLastSave() {
		return lastSave;
	}

	public void setLastSave() {
		this.lastSave = new Date();
	}

}
