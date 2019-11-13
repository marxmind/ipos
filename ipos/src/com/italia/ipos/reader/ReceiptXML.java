package com.italia.ipos.reader;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.italia.ipos.controller.ReceiptInfo;
import com.italia.ipos.enm.Ipos;

/**
 * 
 * @author mark italia
 * @since 05/22/2017
 * @version 1.0
 *
 */
public class ReceiptXML {

	private static final String APPLICATION_FILE = Ipos.PRIMARY_DRIVE.getName() + 
			   Ipos.SEPERATOR.getName() + 
			   Ipos.APP_FOLDER.getName() + 
			   Ipos.SEPERATOR.getName() + 
			   Ipos.APP_CONFIG_FLDR.getName() + 
			   Ipos.SEPERATOR.getName() +
			   Ipos.RECEIPT_INFO.getName();
	
	public static ReceiptInfo value(){
		try{
			File xmlFile = new File(APPLICATION_FILE);
			ReceiptInfo pt = new ReceiptInfo();
			if(xmlFile.exists()){
				
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
				Document doc = documentBuilder.parse(xmlFile);
				
				/////////////normalize
				doc.getDocumentElement().normalize();
				//System.out.println("Reading conf......");
				
				NodeList ls = doc.getElementsByTagName("rcpt");
				int size=ls.getLength();
				
				for(int i=0; i<size; i++){
					Node n = ls.item(i);
					//System.out.println("Current Node: "+ n.getNodeName());
					
					if(n.getNodeType() == Node.ELEMENT_NODE){
						Element e = (Element)n;
						
						pt.setOwner(e.getElementsByTagName("owner").item(0).getTextContent());
						pt.setTitle(e.getElementsByTagName("title").item(0).getTextContent());
						pt.setAdditionalDetails(e.getElementsByTagName("additionalDetails").item(0).getTextContent());
						pt.setAdditionalDetails2(e.getElementsByTagName("additionalDetails2").item(0).getTextContent());
						pt.setPosserial(e.getElementsByTagName("posserial").item(0).getTextContent());
						pt.setTelephoneNumber(e.getElementsByTagName("telephoneNumber").item(0).getTextContent());
						pt.setEmail(e.getElementsByTagName("email").item(0).getTextContent());
						pt.setPosStatus(e.getElementsByTagName("posStatus").item(0).getTextContent());
						pt.setPosDistributor(e.getElementsByTagName("posDistributor").item(0).getTextContent());
						pt.setPosDistributorAddress(e.getElementsByTagName("posDistributorAddress").item(0).getTextContent());
						pt.setPosDisTinNumber(e.getElementsByTagName("posDisTinNumber").item(0).getTextContent());
						pt.setPosCreditedNumber(e.getElementsByTagName("posCreditedNumber").item(0).getTextContent());
						pt.setPosDateRegistered(e.getElementsByTagName("posDateRegistered").item(0).getTextContent());
						pt.setPosDetails1(e.getElementsByTagName("posDetails1").item(0).getTextContent());
						pt.setPosDetails2(e.getElementsByTagName("posDetails2").item(0).getTextContent());
						pt.setPosDetails3(e.getElementsByTagName("posDetails3").item(0).getTextContent());
						pt.setPosDetails4(e.getElementsByTagName("posDetails4").item(0).getTextContent());
						pt.setPosDetails5(e.getElementsByTagName("posDetails5").item(0).getTextContent());
						
					}
					
				}
				//System.out.println("completed reading conf......");
			}else{
				System.out.println("File is not exist...");
			}
			return pt;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		
		ReceiptInfo rp = ReceiptXML.value();
		
		System.out.println(rp.getPosDistributor());
		
	}
	
}
