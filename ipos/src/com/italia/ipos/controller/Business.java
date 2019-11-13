package com.italia.ipos.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.italia.ipos.database.ConnectDB;
import com.italia.ipos.enm.Ipos;
/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */
public class Business {

	private int id;
	private String name;
	private String address;
	private String contactNo;
	private String ownername;
	private String description;
	private String database;
	private String userName;
	private String password;
	
	private String driver;
	private String url;
	private String ssl;
	private String port;
	
	private Timestamp timestamp;
	
	private static final String BUSINESS_FILE = Ipos.PRIMARY_DRIVE.getName() + Ipos.SEPERATOR.getName() + 
			Ipos.APP_FOLDER.getName() + Ipos.SEPERATOR.getName() +
			Ipos.APP_CONFIG_FLDR.getName() + Ipos.SEPERATOR.getName() +
			Ipos.BUSINESS.getName();
	
	private static final String APPLICATION_FILE = Ipos.PRIMARY_DRIVE.getName() + Ipos.SEPERATOR.getName() + 
			Ipos.APP_FOLDER.getName() + Ipos.SEPERATOR.getName() +
			Ipos.APP_CONFIG_FLDR.getName() + Ipos.SEPERATOR.getName() +
			Ipos.APP_CONFIG_FILE.getName();
	
	public Business(){}
	
	public Business(
			int id,
			String companyName,
			String address,
			String contactNo,
			String ownername,
			String description,
			String database,
			String driver,
			String url,
			String ssl,
			String port
			){
		this.id = id;
		this.name = companyName;
		this.address = address;
		this.contactNo = contactNo;
		this.ownername = ownername;
		this.description = description;
		this.database = database;
		this.driver = driver;
		this.url = url;
		this.ssl = ssl;
		this.port = port;
	}

    public static List<Business> retrieveProduct(String sql, String[] params){
		
		List<Business> business = Collections.synchronizedList(new ArrayList<Business>());
		
		
		
		return business;
	}
    
    public static void updateBusiness(Ipos[] tag, String[] value){
		
		try{
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(APPLICATION_FILE);
		
		// Get the root element
		Node license = doc.getFirstChild();
		
		// Get the module element by tag name directly
		Node module = doc.getElementsByTagName("databaseDetails").item(0);
		
		// loop the module child node
		NodeList list = module.getChildNodes();
		
		for (int i = 0; i < list.getLength(); i++) {
	
	            Node node = list.item(i);
			
				   // get the salary element, and update the value
	            //if(i<=tag.length){
		            for(int x=0; x<tag.length; x++){
					   if (tag[x].getName().equals(node.getNodeName())) {
						node.setTextContent(value[x]);
						//System.out.println("setting " + node.getNodeName() + " : " + value[x]);
					   }
		            }   
	            //}
			}
		
		
		// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(APPLICATION_FILE));
				transformer.transform(source, result);

				System.out.println("Done");

		
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		   } catch (TransformerException tfe) {
			tfe.printStackTrace();
		   } catch (IOException ioe) {
			ioe.printStackTrace();
		   } catch (SAXException sae) {
			sae.printStackTrace();
		   }
	}
	
    public static List<Business> readBusinessXML(){
    	List<Business> business = Collections.synchronizedList(new ArrayList<Business>());
    	try {
            File fXmlFile = new File(BUSINESS_FILE);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile); 
            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize(); 
            NodeList nList = doc.getElementsByTagName("line");
            //System.out.println("----------------------------");

                for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
                    //System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            
            Element eElement = (Element) nNode; 
            
            	Business bz = new Business();
            	bz.setId(Integer.valueOf(eElement.getAttribute("id")));
            	bz.setName(eElement.getElementsByTagName("name").item(0).getTextContent());
            	bz.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
            	bz.setAddress(eElement.getElementsByTagName("address").item(0).getTextContent());
            	bz.setOwnername(eElement.getElementsByTagName("owner").item(0).getTextContent());
            	bz.setContactNo(eElement.getElementsByTagName("contact").item(0).getTextContent());
            	bz.setDatabase(eElement.getElementsByTagName("database").item(0).getTextContent());
            	bz.setDriver(eElement.getElementsByTagName("driver").item(0).getTextContent());
            	bz.setUrl(eElement.getElementsByTagName("url").item(0).getTextContent());
            	bz.setPort(eElement.getElementsByTagName("port").item(0).getTextContent());
            	bz.setSsl(eElement.getElementsByTagName("SSL").item(0).getTextContent());
            	bz.setUserName(eElement.getElementsByTagName("username").item(0).getTextContent());
            	bz.setPassword(eElement.getElementsByTagName("password").item(0).getTextContent());
            	business.add(bz);
                }
            }
           } catch (Exception e) {
            e.printStackTrace();
           }
    	return business;
    }
    
    public static void main(String[] args) {
		Business.readBusinessXML();
	}
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSsl() {
		return ssl;
	}

	public void setSsl(String ssl) {
		this.ssl = ssl;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	
	
}
