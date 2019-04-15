package ie.tcd.kdeg.r2rmleditor.pages;

import java.nio.file.Files;
import java.io.File;

import java.io.FileWriter;
import java.io.FileReader;
import java.util.Scanner;
import java.lang.StringBuilder;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import ie.tcd.kdeg.r2rmleditor.entities.Mapping;

public class Index extends BasePage {

	@Property
	private Mapping newMapping;

	@Property
	private Mapping mapping;

	@Inject
	private Session session;
	
	@CommitAfter
	public String onActivate() {
		if(!securityService.isAuthenticated()) {
			return "login";
		}
		return null;
	}
	
	@CommitAfter
	public void onSuccess() { /**********/
		newMapping.setCreator(getUsername());
		
		String outString = "";
		
		/*/Getting DB username and password and setting it for the mapping
		try {
			Scanner in = new Scanner(new FileReader("src/main/resources/hibernate.cfg.xml"));
			StringBuilder sb = new StringBuilder();
			while(in.hasNext()) {
				sb.append(in.next());
			}
			in.close();
		
			outString = sb.toString();
		}
		catch (Exception e) {}
		
		//User name from config file
		int find1 = outString.indexOf(".username\">");
		int find2 = outString.indexOf("<", find1);
		String uname = outString.substring(find1+11, find2);
		
		//Password from config file
		int find3 = outString.indexOf(".password\">");
		int find4 = outString.indexOf("<", find3);
		String pw = outString.substring(find3+11, find4);
		
		newMapping.setUser(uname);
		newMapping.setPassword(pw);
		*/
		session.persist(newMapping);
	}

	@SuppressWarnings("unchecked")
	public List<Mapping> getMappings() {
		return session.createCriteria(Mapping.class)
				.add(Restrictions.eq("creator", getUsername())) //Comment out to allow all mapping visable by all
				.list();
	}
	
	//Delete Mapping
	@CommitAfter
	public void onActionFromDeleteMap(Mapping mapping) {
		session.delete(mapping);
		
		File f1 = new File("userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/"+mapping.getId()+"_mapping.ttl");
		if( f1.exists() ){
			f1.delete();
		}
		
		File f2 = new File("userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/sourcedata.csv");
		if( f2.exists() ){
			f2.delete();
		}
		
		File f3 = new File("userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/config.properties");
		if( f3.exists() ){
			f3.delete();
		}
		
		File f4 = new File("src/main/webapp/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/RENAME_ME_T.ttl");
		if( f4.exists() ){
			f4.delete();
		}	
		
		File f5 = new File("src/main/webapp/userfiles/"+mapping.getCreator()+"/"+mapping.getId()+"/RENAME_ME.ttl");	
		if( f5.exists() ){
			f5.delete();
		}
		
		File d1 = new File("userfiles/"+mapping.getCreator()+"/"+mapping.getId());
		if( d1.exists() ){
			d1.delete();
		}
		
		File d2 = new File("src/main/webapp/userfiles/"+mapping.getCreator()+"/"+mapping.getId());
		if(d2.exists() ){
			d2.delete();
		}
	}
	
	// DUPLICATE MAPPING
	@CommitAfter
	public void onActionFromDuplicateMap(Mapping mapping) {
		Mapping copiedMapping = new Mapping();
		copiedMapping.setTitle("Copy of " + mapping.getTitle());
		copiedMapping.setXML(mapping.getXML());
		copiedMapping.setCreator(mapping.getCreator());
		copiedMapping.setUser(mapping.getUser());
		copiedMapping.setPassword(mapping.getPassword());
		session.persist(copiedMapping);
	}
	
}
