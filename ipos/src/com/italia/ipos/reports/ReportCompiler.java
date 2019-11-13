package com.italia.ipos.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author mark italia
 * @since 02/22/2017
 * @version 1.0
 *
 */
public class ReportCompiler {

	public String compileReport(String rptFileJrxml, String rptFileJasper, String rptLocation){
		String jasperFile="";
		System.out.println("Compiling " + rptFileJrxml + " file.....");
		try{
		JasperCompileManager.compileReportToFile(rptLocation + rptFileJrxml + ".jrxml", rptLocation + rptFileJasper + ".jasper");
		System.out.println(rptFileJrxml +" is successfully compiled...");
		jasperFile = rptLocation + rptFileJasper + ".jasper";
		}catch(JRException jre){
			jre.getMessage();
		}
		return jasperFile;
	}
	public JasperPrint report(String reportLocation, HashMap params){
		JasperPrint jasperPrint = null;
		try{
		jasperPrint = JasperFillManager.fillReport (reportLocation, params, new JREmptyDataSource());
		}catch(JRException jre){
			System.out.println("JasperPrint report()");
			jre.getMessage();
		}
		return jasperPrint;
	}
	public JasperPrint report(String jasperReport, HashMap params,JRBeanCollectionDataSource jrBeanColl){
		JasperPrint jasperPrint = null;
		try{
		jasperPrint = JasperFillManager.fillReport(jasperReport, params,jrBeanColl);
		System.out.println("JasperPrint report()");
		}catch(Exception jre){
			
			jre.getMessage();
		}
		return jasperPrint;
	}
	
	
	
	
}

