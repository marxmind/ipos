package com.italia.ipos.room.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.Login;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.reports.ReadXML;
import com.italia.ipos.reports.ReportCompiler;
import com.italia.ipos.reports.ReportTag;
import com.italia.ipos.room.controller.PrintFields;
import com.italia.ipos.room.controller.RoomHistory;
import com.italia.ipos.room.controller.Rooms;
import com.italia.ipos.room.enm.Iroom;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.Whitelist;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
/**
 * 
 * @author mark italia
 * @since 04/11/2017
 * @version 1.0
 *
 */
@ManagedBean(name="mainBean", eager=true)
@ViewScoped
public class MainBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Rooms> rooms = Collections.synchronizedList(new ArrayList<Rooms>());
	private Rooms selectedRoom;
	
	private String searchRoom;
	private Rooms roomSelectedData;
	
	private List<Rooms> availableRooms = Collections.synchronizedList(new ArrayList<Rooms>());
	private String searchAvailableRoom;
	
	private List<Rooms> occupiedRooms = Collections.synchronizedList(new ArrayList<Rooms>());
	private String searchOccupiedRooms;
	
	private String customerName;
	private int countDays;
	private Date dateCheckIn;
	private Date dateCheckOut;
	
	private List<Rooms> outRooms = Collections.synchronizedList(new ArrayList<Rooms>());
	private String searchOutRooms;
	
	private String addOnsDetails;
	private double addPrice;
	
	@PostConstruct
	public void init(){
		
		rooms = Collections.synchronizedList(new ArrayList<Rooms>());
		
		if(Login.checkUserStatus()){
		
		String sql = "SELECT * FROM rooms WHERE roomIsActive=1 ";
		String[] params = new String[0];
		if(getSearchRoom()!=null && !getSearchRoom().isEmpty()){
			String room = Whitelist.remove(getSearchRoom());
			sql +=" AND ( roomNo like '%"+ room +"%' OR roomName like '%"+ room +"%' ) ";
		}
		sql += " ORDER BY roomNo"; 
		rooms = Rooms.retrieve(sql, params);
		
		}
		
	}
	public void showRoom(Rooms room){
		System.out.println("available room " + room.getName());
		setSearchRoom(room.getRoomNumber());
		init();
	}
	
	public void clickDetails(Rooms room){
		setCustomerName(room.getCustomerName());
		setAddOnsDetails(room.getAddOnsDetails());
		setAddPrice(room.getAddPrice());
		if(room.getCountDays()>0){
			setCountDays(room.getCountDays());
		}else{
			setCountDays(1);
		}
		if(room.getCheckInDateTime()==null || room.getCheckInDateTime().isEmpty()){
			room.setCheckInDateTime(DateUtils.getCurrentDateYYYYMMDDTIME());
		}
		
		setRoomSelectedData(room);
	}
	
	public void saveButton(){
		if(Login.checkUserStatus()){
			
			if(getRoomSelectedData()!=null){
				
				if(getCustomerName()!=null && !getCustomerName().isEmpty()){
				Rooms room = getRoomSelectedData();
				room.setAvailable(1);
				room.setCustomerName(getCustomerName());
				
				/*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String checkIn = dateFormat.format(getDateCheckIn());
				String checkOut = dateFormat.format(getDateCheckOut());*/
				String checkinDate = "";
				if(room.getCheckInDateTime()==null || room.getCheckInDateTime().isEmpty()){
					checkinDate = DateUtils.getCurrentDateYYYYMMDDTIME();
					room.setCheckInDateTime(checkinDate);
				}else{
					checkinDate = room.getCheckInDateTime().split(" ")[0];
				}
				
				room.setCountDays(getCountDays());
				room.setCheckOutDateTime(DateUtils.getDateBaseOnCount(getCountDays() + 1, checkinDate, 1) + " 12:00:00 NN");
				
				room.setAddOnsDetails(getAddOnsDetails());
				room.setAddPrice(getAddPrice());
				
				room.save();
				setRoomSelectedData(room);
				setCustomerName(null);
				setAddOnsDetails(null);
				setAddPrice(0.00);
				//clearFields();
				init();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Check In Time has been successfully set", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please specify customer name.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
	            
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select room.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void checkOutButton(Rooms room){
		if(Login.checkUserStatus()){
			
			if(room.getAvailable()==1){
				
				//Rooms room = getRoomSelectedData();
				
				
				String[] val = new String[9];
				val[0] = "Date : "+DateUtils.getCurrentDateYYYYMMDD();
				val[1] = "Customer : "+room.getCustomerName();
				val[2] = "Check In : "+room.getCheckInDateTime();
				val[3] = "Check Out : " + room.getCheckOutDateTime();
				val[4] = "Name : " + room.getName();
				val[5] = "Number : " + room.getRoomNumber();
				val[6] = "Days : "+room.getCountDays()+"";
				
				
				RoomHistory his = new RoomHistory();
				his.setRemarks("COMPLETED");
				his.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
				his.setCheckInDateTime(room.getCheckInDateTime());
				his.setCheckOutDateTime(room.getCheckOutDateTime());
				his.setCountDays(room.getCountDays());
				his.setCustomerName(room.getCustomerName());
				his.setRoomNumber(room.getRoomNumber());
				his.setName(room.getName());
				String addOns = room.getAddOnsDetails()!=null? " Add Ons: "+ room.getAddOnsDetails() + " Php " + room.getAddPrice() : "";
				his.setDescription("Php " + room.getPrice() + " " + room.getDescription() + addOns);
				his.setPrice(Double.valueOf(room.getAmountRendered().replace(",", "")));
				his.setActive(1);
				his.save();
				
				val[7] = "Desc : "+his.getDescription();
				val[8] = "Total : "+his.getPrice()+"";
				
				room.setAvailable(0);
				room.setCountDays(0);
				room.setCustomer(null);
				room.setCustomerName(null);
				room.setCheckInDateTime(null);
				room.setCheckOutDateTime(null);
				room.setAddOnsDetails(null);
				room.setAddPrice(0.00);
				room.save();
				
				loadPrintReceipt(val);// print the check room details
				
				//setRoomSelectedData(room);
				//clearFields();
				init();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Check Out Time has been saved", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Room is not yet occupied.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private static final String REPORT_PATH = ReadConfig.value(Ipos.REPORT);
	private static final String REPORT_NAME = ReadXML.value(ReportTag.CHECK_OUT_RECEIPT);
	
	private void loadPrintReceipt(String[] vals){
		
		
		
		List<PrintFields> pfs = Collections.synchronizedList(new ArrayList<PrintFields>());
		
		for(String val : vals){
			PrintFields pf = new PrintFields();
			pf.setF1(val);
			pfs.add(pf);
		}
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(pfs);
  		HashMap param = new HashMap();
  		param.put("PARAM_BUSINESS_NAME", ReadConfig.value(Ipos.BUSINESS_NAME));
		
  		String preparedBy = "";
  		try{
  			preparedBy = Login.getUserLogin().getUserDtls().getFirstname() + " " + Login.getUserLogin().getUserDtls().getLastname();
  		}catch(Exception e){}
  		param.put("PARAM_PREPAREDBY", "Prepared By: "+preparedBy);
  		param.put("PARAM_DATE", "Printed:"+DateUtils.getCurrentDateMMDDYYYYTIME());
  		
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
	
	public void loadAvailableRooms(){
		availableRooms = Collections.synchronizedList(new ArrayList<Rooms>());
		
		String sql = "SELECT * FROM rooms WHERE roomIsActive=1 AND roomIsAvailable=0 ";
		String[] params = new String[0];
		if(getSearchAvailableRoom()!=null && !getSearchAvailableRoom().isEmpty()){
			String room = Whitelist.remove(getSearchAvailableRoom());
			sql +=" AND ( roomNo like '%"+ room +"%' OR roomName like '%"+ room +"%' ) ";
		}
		sql += " ORDER BY roomNo"; 
		availableRooms = Rooms.retrieve(sql, params);
		
	}
	
	public void loadOccupiedRooms(){
		occupiedRooms = Collections.synchronizedList(new ArrayList<Rooms>());
		
		String sql = "SELECT * FROM rooms WHERE roomIsActive=1 AND roomIsAvailable=1 ";
		String[] params = new String[0];
		if(getSearchOccupiedRooms()!=null && !getSearchOccupiedRooms().isEmpty()){
			String room = Whitelist.remove(getSearchOccupiedRooms());
			sql +=" AND ( roomNo like '%"+ room +"%' OR roomName like '%"+ room +"%' ) ";
		}
		sql += " ORDER BY roomNo"; 
		occupiedRooms = Rooms.retrieve(sql, params);
		
	}
	
	public void loadForOutRooms(){
		outRooms = Collections.synchronizedList(new ArrayList<Rooms>());
		String checkOutDate = DateUtils.getCurrentDateYYYYMMDD();
		String sql = "SELECT * FROM rooms WHERE roomIsActive=1 AND roomIsAvailable=1 AND roomCheckOut=?";
		String[] params = new String[1];
		params[0] = checkOutDate + " 12:00:00 NN";
		if(getSearchOutRooms()!=null && !getSearchOutRooms().isEmpty()){
			String room = Whitelist.remove(getSearchOutRooms());
			sql +=" AND ( roomNo like '%"+ room +"%' OR roomName like '%"+ room +"%' ) ";
		}
		sql += " ORDER BY roomNo"; 
		outRooms = Rooms.retrieve(sql, params);
		
	}
	
	public void clearFields(){
		setRoomSelectedData(null);
	}
	
	public void clickRoom(Rooms room){
		
		setCustomerName(room.getCustomerName());
		setAddOnsDetails(room.getAddOnsDetails());
		setAddPrice(room.getAddPrice());
		if(room.getCountDays()>0){
			setCountDays(room.getCountDays());
		}else{
			setCountDays(1);
		}
		if(room.getCheckInDateTime()==null || room.getCheckInDateTime().isEmpty()){
			room.setCheckInDateTime(DateUtils.getCurrentDateYYYYMMDDTIME());
		}
		
		setRoomSelectedData(room);
	}
	
	public void clickCancelRoom(Rooms room){
		if(room.getCustomerName()!=null && !room.getCustomerName().isEmpty()){
			RoomHistory his = new RoomHistory();
			his.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			his.setRemarks("CANCELLED ROOM");
			his.setActive(1);
			his.setName(room.getName());
			his.setRoomNumber(room.getRoomNumber());
			his.setDescription(room.getDescription());
			his.setCustomerName(room.getCustomerName());
			his.setCheckInDateTime(room.getCheckInDateTime());
			his.setCheckOutDateTime(room.getCheckOutDateTime());
			his.setPrice(0);
			his.setCountDays(room.getCountDays());
			his.save();
			
			room.setAvailable(0);
			room.setCustomerName("");
			room.setCustomer(new Customer());
			room.setCheckInDateTime(null);
			room.setCheckOutDateTime(null);
			room.setAddOnsDetails(null);
			room.setAddPrice(0.00);
			room.setCountDays(0);
			room.save();
			
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Room "+ room.getName() +" has been cancelled successfully.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Room "+ room.getName() +" is not yet occupid. Cancellation of room was not successfully processed.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void changeNumberDays(){
		System.out.println("updating days " + getCountDays());
	}
	
	public List<Rooms> getRooms() {
		return rooms;
	}

	public void setRooms(List<Rooms> rooms) {
		this.rooms = rooms;
	}

	public Rooms getSelectedRoom() {
		return selectedRoom;
	}

	public void setSelectedRoom(Rooms selectedRoom) {
		this.selectedRoom = selectedRoom;
	}

	public String getSearchRoom() {
		return searchRoom;
	}

	public void setSearchRoom(String searchRoom) {
		this.searchRoom = searchRoom;
	}

	public Rooms getRoomSelectedData() {
		return roomSelectedData;
	}

	public void setRoomSelectedData(Rooms roomSelectedData) {
		this.roomSelectedData = roomSelectedData;
	}

	public List<Rooms> getAvailableRooms() {
		return availableRooms;
	}

	public void setAvailableRooms(List<Rooms> availableRooms) {
		this.availableRooms = availableRooms;
	}

	public String getSearchAvailableRoom() {
		return searchAvailableRoom;
	}

	public void setSearchAvailableRoom(String searchAvailableRoom) {
		this.searchAvailableRoom = searchAvailableRoom;
	}

	public List<Rooms> getOccupiedRooms() {
		return occupiedRooms;
	}

	public void setOccupiedRooms(List<Rooms> occupiedRooms) {
		this.occupiedRooms = occupiedRooms;
	}

	public String getSearchOccupiedRooms() {
		return searchOccupiedRooms;
	}

	public void setSearchOccupiedRooms(String searchOccupiedRooms) {
		this.searchOccupiedRooms = searchOccupiedRooms;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public int getCountDays() {
		return countDays;
	}

	public void setCountDays(int countDays) {
		this.countDays = countDays;
	}

	public Date getDateCheckIn() {
		
		if(dateCheckIn==null){
			dateCheckIn = DateUtils.getDateFromString(DateUtils.getCurrentDateYYYYMMDD(), "yyyy-MM-dd");
		}
		
		return dateCheckIn;
	}

	public void setDateCheckIn(Date dateCheckIn) {
		this.dateCheckIn = dateCheckIn;
	}

	public Date getDateCheckOut() {
		if(dateCheckOut==null){
			dateCheckOut = DateUtils.getDateFromString(DateUtils.getCurrentDateYYYYMMDD(), "yyyy-MM-dd");
		}
		return dateCheckOut;
	}

	public void setDateCheckOut(Date dateCheckOut) {
		this.dateCheckOut = dateCheckOut;
	}
	public List<Rooms> getOutRooms() {
		return outRooms;
	}
	public void setOutRooms(List<Rooms> outRooms) {
		this.outRooms = outRooms;
	}
	public String getSearchOutRooms() {
		return searchOutRooms;
	}
	public void setSearchOutRooms(String searchOutRooms) {
		this.searchOutRooms = searchOutRooms;
	}
	public String getAddOnsDetails() {
		return addOnsDetails;
	}
	public void setAddOnsDetails(String addOnsDetails) {
		this.addOnsDetails = addOnsDetails;
	}
	public double getAddPrice() {
		return addPrice;
	}
	public void setAddPrice(double addPrice) {
		this.addPrice = addPrice;
	}
	
}
