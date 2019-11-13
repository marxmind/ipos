package com.italia.ipos.barcode;

import java.io.File;

import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

/**
 * 
 * @author mark italia
 * @version 1.0
 * @since 02/22/2017
 *
 */
public class BarcodeGen {

	public static void main(String[] args) {
		BarcodeGen.createBarcode();
	}
	
	public static void createBarcode(){
		try{
		File barcodeFile = new File("C:\\coca.jpg");
		BarcodeImageHandler.saveJPEG(BarcodeFactory.createEAN13("123456789"), barcodeFile);
		
		}catch(Exception e){e.printStackTrace();}
	}
	
}
