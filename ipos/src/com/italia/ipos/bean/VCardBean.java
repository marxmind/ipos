package com.italia.ipos.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import com.italia.ipos.barcode.Reports;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.reports.ReadXML;
import com.italia.ipos.reports.ReportCompiler;
import com.italia.ipos.reports.ReportTag;
import com.italia.ipos.reports.VCardRpt;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 10/03/2018
 *
 */
@ManagedBean(name="card",eager=true)
@ViewScoped
public class VCardBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 14546774653L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Ipos.REPORT);
	private static final String REPORT_NAME = ReadXML.value(ReportTag.VCARD);
	
	public void printCard() {
		
		List<VCardRpt> reports = Collections.synchronizedList(new ArrayList<VCardRpt>());
		String[] nums = {"1","2","3","4","5","6","7","8","9","0"};
		String[] nums2 = {"12","23","34","45","56","67","78","89","90","01"};
		String[] nums3 = {"123","234","345","456","567","678","789","890","901","012"};
		String[] nums4 = {"2123","3234","4345","5456","6567","7678","8789","9890","0901","1012"};
		String[] nums5 = {"10","30","40","50","60","70","80","90","00","99"};
		
		
		for(int x=1; x<=4;x++) {
			VCardRpt rpt = new VCardRpt();
			String valueCardNumber = nums[(int) (Math.random() * nums.length)] + nums2[(int) (Math.random() * nums2.length)] + nums3[(int) (Math.random() * nums3.length)] + nums4[(int) (Math.random() * nums4.length)] + nums5[(int) (Math.random() * nums5.length)];
			rpt.setF1(valueCardNumber);
			valueCardNumber = nums[(int) (Math.random() * nums.length)] + nums2[(int) (Math.random() * nums2.length)] + nums3[(int) (Math.random() * nums3.length)] + nums4[(int) (Math.random() * nums4.length)] + nums5[(int) (Math.random() * nums5.length)];
			rpt.setF3(valueCardNumber);
			
			String vc = REPORT_PATH + "vc.jpg";
			try{File file = new File(vc);
			FileInputStream file2 = new FileInputStream(file);
			FileInputStream file3 = new FileInputStream(file);
			rpt.setF2(file2);
			rpt.setF4(file3);
			}catch(Exception e){}
			
			reports.add(rpt);
		
		}
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(reports);
  		HashMap param = new HashMap();
  		
  			try{
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
	  		}catch(Exception e){e.printStackTrace();}
		
  			try{
  				System.out.println("REPORT_PATH:" + REPORT_PATH + "REPORT_NAME: " + REPORT_NAME);
		  		 File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
				 FacesContext faces = FacesContext.getCurrentInstance();
				 ExternalContext context = faces.getExternalContext();
				 HttpServletResponse response = (HttpServletResponse)context.getResponse();
					
			     BufferedInputStream input = null;
			     BufferedOutputStream output = null;
			     
			     try{
			    	 
			    	 // Open file.
			            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

			            // Init servlet response.
			            response.reset();
			            response.setHeader("Content-Type", "application/pdf");
			            response.setHeader("Content-Length", String.valueOf(file.length()));
			            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
			            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			            // Write file contents to response.
			            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			            int length;
			            while ((length = input.read(buffer)) > 0) {
			                output.write(buffer, 0, length);
			            }

			            // Finalize task.
			            output.flush();
			    	 
			     }finally{
			    	// Gently close streams.
			            close(output);
			            close(input);
			     }
			     
			     // Inform JSF that it doesn't need to handle response.
			        // This is very important, otherwise you will get the following exception in the logs:
			        // java.lang.IllegalStateException: Cannot forward after response has been committed.
			        faces.responseComplete();
			        
				}catch(Exception ioe){
					ioe.printStackTrace();
				}
	}
	
	private void close(Closeable resource) {
	    if (resource != null) {
	        try {
	            resource.close();
	        } catch (IOException e) {
	            // Do your thing with the exception. Print it, log it or mail it. It may be useful to 
	            // know that this will generally only be thrown when the client aborted the download.
	            e.printStackTrace();
	        }
	    }
	}

}
