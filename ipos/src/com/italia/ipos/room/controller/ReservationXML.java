package com.italia.ipos.room.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.italia.ipos.room.enm.Iroom;
import com.italia.ipos.room.enm.RsvpTag;
/**
 * 
 * @author mark italia
 * @since 05/05/2017
 * @version 1.0
 *
 */
public class ReservationXML {

	private static final String RESERVATION_FILE_XML = Iroom.PRIMARY_DRIVE.getName() + Iroom.SEPERATOR.getName() +
			Iroom.APP_FOLDER.getName() + Iroom.SEPERATOR.getName() +
			Iroom.APP_CONFIG_FLDR.getName() + Iroom.SEPERATOR.getName() +
			Iroom.RESERVATION_FILE_XML.getName();
	
public static void main(String[] args) {
	
	String val[] = new String[4];
	val[0] = (ReservationXML.getLastId() + 1) + "";
	val[1] = "Choco";
	val[2] = "06-May-2017";
	val[3] = "07-May-2017";
	
	//ReservationXML.addElement(val);
	
	/*for(Reservation rs : ReservationXML.readReservationXML()){
		
		System.out.println(rs.getId() + " " +rs.getDescription());
		
	}*/
	
	val = new String[4];
	val[0] = "1";
	val[1] = "Mark Rivera Italia";
	val[2] = "06-Dec-2017";
	val[3] = "07-Dec-2017";
	//ReservationXML.updateElement(val);
	ReservationXML.deleteElement(val);
	
}	
	
public static void addElement(String[] val){
		
		try{
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(RESERVATION_FILE_XML);
		
		// Get the root element
		Node reservation = doc.getFirstChild();
		
		Node rsvp = doc.createElement(RsvpTag.RSVP.getName());
		
		int i=0;
		
		Attr att = doc.createAttribute(RsvpTag.ID.getName());
		att.setValue(val[i++]);
		
		Element root = (Element)rsvp;
		root.setAttributeNode(att);
		
		Node desc = doc.createElement(RsvpTag.DESC.getName());
		rsvp.appendChild(desc);
		desc.setTextContent(val[i++]);
		
		Node startDate = doc.createElement(RsvpTag.START_DATE.getName());
		rsvp.appendChild(startDate);
		startDate.setTextContent(val[i++]);
		
		Node endDate = doc.createElement(RsvpTag.END_DATE.getName());
		rsvp.appendChild(endDate);
		endDate.setTextContent(val[i++]);
		
		reservation.appendChild(rsvp);
		
		
		
		
		// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(RESERVATION_FILE_XML));
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
	
	public static void updateElement(String[] val){
	
	try{
	
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		
		//File file = new File(RESERVATION_FILE_XSD);
		// create schema
       // String constant = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        //SchemaFactory xsdFactory = SchemaFactory.newInstance(constant);
       // Schema schema = xsdFactory.newSchema(file);
        
        // set schema
        //docFactory.setSchema(schema);
		
		
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(RESERVATION_FILE_XML);
		doc.getDocumentElement().normalize();

				// Get the root element
				Node reservation = doc.getFirstChild();
				
				NodeList tags = reservation.getChildNodes();
				
				for(int y=0; y<tags.getLength(); y++){
				// Get the rsvp element , it may not working if tag has spaces, or
				// whatever weird characters in front...it's better to use
				// getElementsByTagName() to get it directly.
				// Node rsvp = reservation.getFirstChild();

				// Get the reservation element by tag name directly
				
				int x=0; // use for array		
				// Get the rsvp element by tag name directly
				Node rsvp = doc.getElementsByTagName(RsvpTag.RSVP.getName()).item(y);
				//Node rsvp = doc.getElementById("1");
				
				//System.out.println("rsvp " + rsvp.getTextContent());
				
				// update staff attribute
				try{
				NamedNodeMap attr = rsvp.getAttributes();
				Node nodeAttr = attr.getNamedItem("id");
				
				System.out.println("ID: " + nodeAttr.getTextContent());
				
				if(val[x++].equalsIgnoreCase(nodeAttr.getTextContent())){
				
				NodeList els = rsvp.getChildNodes();
		
				
				for(int i=0; i<els.getLength(); i++){
					
					Node node = els.item(i);
					if(RsvpTag.DESC.getName().equals(node.getNodeName())){
						node.setTextContent(val[x++]);
					}
					if(RsvpTag.START_DATE.getName().equals(node.getNodeName())){
						node.setTextContent(val[x++]);
					}
					if(RsvpTag.END_DATE.getName().equals(node.getNodeName())){
						node.setTextContent(val[x++]);
					}
				}
				
				}
				
				}catch(Exception e){}
			
				}	
		
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(RESERVATION_FILE_XML));
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

	public static void deleteElement(String[] val){
		
		try{
		
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(RESERVATION_FILE_XML);
			doc.getDocumentElement().normalize();

					// Get the root element
					Node reservation = doc.getFirstChild();
					
					NodeList tags = reservation.getChildNodes();
					
					for(int y=0; y<tags.getLength(); y++){
					// Get the rsvp element , it may not working if tag has spaces, or
					// whatever weird characters in front...it's better to use
					// getElementsByTagName() to get it directly.
					// Node rsvp = reservation.getFirstChild();

					// Get the reservation element by tag name directly
					
					int x=0; // use for array		
					// Get the rsvp element by tag name directly
					Node rsvp = doc.getElementsByTagName(RsvpTag.RSVP.getName()).item(y);
					//Node rsvp = doc.getElementById("1");
					
					//System.out.println("rsvp " + rsvp.getTextContent());
					
					// update staff attribute
					try{
					NamedNodeMap attr = rsvp.getAttributes();
					Node nodeAttr = attr.getNamedItem("id");
					
					System.out.println("ID: " + nodeAttr.getTextContent());
					
					if(val[x++].equalsIgnoreCase(nodeAttr.getTextContent())){
					
					//NodeList els = rsvp.getChildNodes();
			
					reservation.removeChild(rsvp);
					
					/*for(int i=0; i<els.getLength(); i++){
						
						Node node = els.item(i);
						if(RsvpTag.DESC.getName().equals(node.getNodeName())){
							node.setTextContent(val[x++]);
						}
						if(RsvpTag.START_DATE.getName().equals(node.getNodeName())){
							node.setTextContent(val[x++]);
						}
						if(RsvpTag.END_DATE.getName().equals(node.getNodeName())){
							node.setTextContent(val[x++]);
						}
					}*/
					
					}
					
					}catch(Exception e){}
				
					}	
			
					// write the content into xml file
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(doc);
					StreamResult result = new StreamResult(new File(RESERVATION_FILE_XML));
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
	
	public static List<Reservation> readReservationXML(){
	List<Reservation> rsvp = Collections.synchronizedList(new ArrayList<Reservation>());
	try {
        File fXmlFile = new File(RESERVATION_FILE_XML);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile); 
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize(); 
        NodeList nList = doc.getElementsByTagName(RsvpTag.RSVP.getName());
        //System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
        Node nNode = nList.item(temp);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        
        	Element el = (Element) nNode; 
        
        	
        	Reservation rs = new Reservation();
        	rs.setId(el.getAttribute(RsvpTag.ID.getName()));
        	rs.setDescription(el.getElementsByTagName(RsvpTag.DESC.getName()).item(0).getTextContent());
        	rs.setStartDate(el.getElementsByTagName(RsvpTag.START_DATE.getName()).item(0).getTextContent());
        	rs.setEndDate(el.getElementsByTagName(RsvpTag.END_DATE.getName()).item(0).getTextContent());
        	rsvp.add(rs);
            }
        }
       } catch (Exception e) {
        e.printStackTrace();
       }
	return rsvp;
	}
	
	public static int getLastId(){
		int id = 0;
		Map<Integer, Integer> ids = Collections.synchronizedMap(new HashMap<Integer, Integer>());  
		try {
	        File fXmlFile = new File(RESERVATION_FILE_XML);
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(fXmlFile); 
	        //optional, but recommended
	        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	        doc.getDocumentElement().normalize(); 
	        NodeList nList = doc.getElementsByTagName(RsvpTag.RSVP.getName());
	        //System.out.println("----------------------------");

	            for (int temp = 0; temp < nList.getLength(); temp++) {
	        Node nNode = nList.item(temp);
	        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	        
	        	Element el = (Element) nNode; 
	        		
	        		id = Integer.valueOf(el.getAttribute(RsvpTag.ID.getName()));
	        		ids.put(id, id);
	            }
	        }
	            
	        Map<Integer, Integer> orderIds = new TreeMap<Integer, Integer>(ids);    
	        int size = orderIds.size();
	        id = orderIds.get(size).intValue();
	        
	       } catch (Exception e) {
	        e.printStackTrace();
	       }
		return id;
		}

}

