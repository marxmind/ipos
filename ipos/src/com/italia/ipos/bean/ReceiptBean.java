package com.italia.ipos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.Receipt;
import com.italia.ipos.enm.UserAccess;
import com.italia.ipos.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 06/23/2017
 * @version 1.0
 *
 */
@ManagedBean(name = "rptBean", eager=true)
@ViewScoped
public class ReceiptBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 57679896575485681L;
	
	private List<Receipt> rpts = Collections.synchronizedList(new ArrayList<Receipt>());
	private Receipt selectedRpt;
	
	private String dateResetted;
	private String description;
	private String licenseNo;
	private String licenseApprovedDate;
	
	
	@PostConstruct
	public void init(){
		Login in = Login.getUserLogin();
		rpts = Collections.synchronizedList(new ArrayList<Receipt>());
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			System.out.println("DELOPER LOGIN");
			String sql = "SELECT * FROM receiptmanagement";
			String[] params = new String[0];
			rpts = Receipt.retrieve(sql, params);
			Collections.reverse(rpts);
		}
	}
	
	public void clickItem(Receipt rpt){
		setSelectedRpt(rpt);
		setDateResetted(rpt.getDateResetted());
		setDescription(rpt.getReasonDescription());
		setLicenseNo(rpt.getLicenseNo());
		setLicenseApprovedDate(rpt.getLicenseDate());
	}
	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	public void changeActivation(Receipt rpt){
		Login in = Login.getUserLogin();
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			if(rpt.isChecked()){
				rpt.setIsActivated(1);
				rpt.save();
				init();
				addMessage("Or has been activated","");
			}else{
				rpt.setIsActivated(0);
				rpt.save();
				init();
				addMessage("Or has been deactivated","");
			}
		}
	}
	
	public void delete(Receipt rpt){
		Login in = Login.getUserLogin();
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			rpt.delete();
			init();
		}
	}
	
	public void saveRpt(){
		Login in = Login.getUserLogin();
		if(UserAccess.DEVELOPER.getId()==in.getAccessLevel().getLevel()){
			Receipt rpt = new Receipt();
			if(getSelectedRpt()!=null){
				rpt = getSelectedRpt();
			}else{
				rpt.setDateResetted(DateUtils.getCurrentDateYYYYMMDD());
				rpt.setIsActual(1); //actual = 1 // test = 2
				rpt.setIsActivated(1); //activate current use
			}
			rpt.setReasonDescription(getDescription());
			rpt.setLicenseDate(getLicenseApprovedDate());
			rpt.setLicenseNo(getLicenseNo());
			rpt.setIsActive(1);
			rpt.save();
			
			clearFields();
			init();
		}
	}
	
	public void clearFields(){
		setSelectedRpt(null);
		setDateResetted(null);
		setDescription(null);
		setLicenseNo(null);
		setLicenseApprovedDate(null);
	}
	
	public List<Receipt> getRpts() {
		return rpts;
	}

	public void setRpts(List<Receipt> rpts) {
		this.rpts = rpts;
	}

	public Receipt getSelectedRpt() {
		return selectedRpt;
	}

	public void setSelectedRpt(Receipt selectedRpt) {
		this.selectedRpt = selectedRpt;
	}

	public String getDateResetted() {
		if(dateResetted==null){
			dateResetted = DateUtils.getCurrentDateYYYYMMDD();
		}
		return dateResetted;
	}

	public void setDateResetted(String dateResetted) {
		this.dateResetted = dateResetted;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public String getLicenseApprovedDate() {
		if(licenseApprovedDate==null){
			licenseApprovedDate = DateUtils.getCurrentDateYYYYMMDD();
		}
		return licenseApprovedDate;
	}

	public void setLicenseApprovedDate(String licenseApprovedDate) {
		this.licenseApprovedDate = licenseApprovedDate;
	}
	
}
