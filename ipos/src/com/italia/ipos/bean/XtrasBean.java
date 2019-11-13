package com.italia.ipos.bean;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;

import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.CustomerPayment;
import com.italia.ipos.controller.DeliveryItemReceipt;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.MoneyIO;
import com.italia.ipos.controller.Payment;
import com.italia.ipos.controller.Receipt;
import com.italia.ipos.controller.ReceiptInfo;
import com.italia.ipos.controller.UserDtls;
import com.italia.ipos.controller.Xtras;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.enm.MoneyStatus;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.enm.Status;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.reader.ReceiptXML;
import com.italia.ipos.utils.Currency;
import com.italia.ipos.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 01/22/2017
 * @version 1.0
 *
 */
@ManagedBean(name="xtraBean")
@ViewScoped
public class XtrasBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1548934325347L;

	private List<Xtras> xtras = Collections.synchronizedList(new ArrayList<Xtras>());
	private String searchDescription;
	private Date searchDateFrom;
	private Date searchDateTo;
	
	private List customers;
	private long customerId;
	private List transTypes;
	private int transTypeId;
	private Xtras xtrasData;
	private List<Xtras> xtraSelected = Collections.synchronizedList(new ArrayList<Xtras>()); 
	private String xtraDescription;
	private double amount;
	private Date dateTrans;
	private String status;
	private boolean customerEnable;
	
	private String searchName;
	
	@PostConstruct
	public void init(){
		
		String sql = "SELECT * FROM xtras WHERE xDateTrans>=? AND xDateTrans<=? ";
		String[] params = new String[2];
		if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
		   sql += " AND xdescription like '%" + getSearchDescription().replace("--", "") + "%'";
		}
		params[0] = DateUtils.convertDate(getSearchDateFrom(),"yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getSearchDateTo(),"yyyy-MM-dd");
		 
		
		List<Xtras> xtrasTmp = Collections.synchronizedList(new ArrayList<Xtras>());
		xtrasTmp = Xtras.retrieve(sql, params);
		
		if(xtrasTmp.size()==0){
			if(getSearchDescription()!=null && !getSearchDescription().isEmpty()){
				sql = "SELECT * FROM xtras x, customer c WHERE (x.xDateTrans>=? AND x.xDateTrans<=? ) AND c.fullname like '%" + getSearchDescription().replace("--","") + "%'";
				xtrasTmp = Collections.synchronizedList(new ArrayList<Xtras>());
				xtrasTmp = Xtras.retrieve(sql, params);
			}
		}
		xtras = Collections.synchronizedList(new ArrayList<Xtras>());
		for(Xtras x : xtrasTmp){
			Customer c = Customer.retrieve(x.getCustomer().getCustomerid()+"");
			x.setCustomer(c);
			x.setTransactionName(MoneyStatus.typeName(x.getTransType()));
			xtras.add(x);
		}
		
		Collections.reverse(xtras);
		setCustomerEnable(true); 
	}
	
	public void enableCutomerField(){
		if(MoneyStatus.CASH_LOAN.getId()==getTransTypeId() || MoneyStatus.REFUND.getId()==getTransTypeId()){
			setCustomerEnable(false); 
		}else{
			setCustomerEnable(true);
			setCustomerId(0);
			setSearchName(null);
		}
	}
	
	public void newXtra(){
		clearFields();
	}
	
	public void clearFields(){
		setCustomerId(0);
		setTransTypeId(0);
		setXtrasData(null); 
		setXtraDescription(null);
		setAmount(0);
		setDateTrans(null);
		setStatus(null);
		setXtraSelected(null);
	}
	
	public void save(){
		if(Login.checkUserStatus()){
			boolean isOk = false;
			
			Xtras xt = new Xtras();
			xt.setStatus(Status.NEW.getId());
			if(getXtrasData()!=null){
				xt = getXtrasData();
			}
			
			if(Status.POSTED.getId()==xt.getStatus()){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Transaction is already posted. Editing is prohibited", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            isOk = false;
			}else{
			
			if(getAmount()>0){
				isOk = true;
			}else{
				isOk = false;
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide amount.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
				
			if(getXtraDescription()!=null && !getXtraDescription().isEmpty()){
				isOk = true;
			}else{
				isOk = false;
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide description.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			if(getTransTypeId()==0){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide Transaction type.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
	            isOk = false;
			}else{
				
				if(getTransTypeId()==MoneyStatus.CASH_LOAN.getId() && getCustomerId()==0){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cash Loan transaction required to fill-up Customer Name. Please provide customer name.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}else{
					isOk = true;
				}
			}
			
			
			}
			
			System.out.println("Xtra Description " + getXtraDescription());
			
			if(isOk){
				
				xt.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
				xt.setIsActive(1);
				
				xt.setDescription(getXtraDescription());
				xt.setAmount(getAmount());
				xt.setRemarks("Transaction from Xtras");
				Customer cus = new Customer();
				cus.setCustomerid(getCustomerId());
				xt.setCustomer(cus);
				
				xt.setTransType(getTransTypeId());
				xt.setUserDtls(Login.getUserLogin().getUserDtls());
				xt.save();
				
				
				clearFields();
				init();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully saved.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void clickItem(Xtras xtras){
		setCustomerId(xtras.getCustomer().getCustomerid());
		setTransTypeId(xtras.getTransType());
		setXtrasData(xtras); 
		setXtraDescription(xtras.getDescription());
		setAmount(xtras.getAmount());
		setDateTrans(DateUtils.convertDateString(xtras.getDateTrans(),"yyyy-MM-dd"));
		setStatus(Status.typeName(xtras.getStatus()));
		
		if(Status.NEW.getId()==xtras.getStatus()){
			setCustomerEnable(false);
		}
	}
	
	public void deleteItem(Xtras xtras){
		if(Login.checkUserStatus()){
			if(xtras.getStatus()==Status.POSTED.getId()){
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Transaction is already posted. Deletion is not allowed.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}else{
				xtras.delete();
				clearFields();
				init();
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully saved.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void posted(){
		if(Login.checkUserStatus()){
			boolean isOk = true;
			boolean transOk = false;
			if(getXtraDescription()!=null && !getXtraDescription().isEmpty()){
				isOk = false;
			}else{
				isOk = true;
			}
			
			if(getTransTypeId()!=0){
	            isOk = false;
			}else{
				isOk = true;
			}
			
			if(getTransTypeId()==MoneyStatus.CASH_LOAN.getId() && getCustomerId()==0){
				isOk = false;
				transOk = true;
			}
			
			if(isOk){
			
			if(getXtraSelected().size()>0){
				int cnt = 0;
				for(Xtras x : getXtraSelected()){
					
					if(Status.NEW.getId()==x.getStatus()){
						xtraSave(x);
						cnt++;
					}
				}
				
				if(cnt>0){
					clearFields();
					init();
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, (cnt==1? "1 Transaction has " : cnt+" Transacation have ") +"been processed for posting.", "");
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No transaction has been processed for posting.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No transaction has been selected for posting.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			}else{
				
				if(transOk){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cash Loan transaction required to fill-up Customer Name. Please provide customer name.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please save it first before posting.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
			}
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void xtraSave(Xtras xtra){
		UserDtls user = Login.getUserLogin().getUserDtls();
		xtra.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		xtra.setStatus(Status.POSTED.getId());
		xtra.setUserDtls(user);
		xtra.save();
		
		MoneyIO io = new MoneyIO();
		io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		
		Customer customer = new Customer();
		customer = Customer.retrieve(xtra.getCustomer().getCustomerid()+"");
		
		String receiptNo = DeliveryItemReceipt.generateNewReceiptNo();
		/**
		 * Please note that additional changes below
		 * AccountingBean is affected, hence update this class also at method startExpenses() and printSummary()
		 */
		
		if(xtra.getTransType()==MoneyStatus.ALLOWANCES.getId()){
			io.setTransType(MoneyStatus.ALLOWANCES.getId());
			io.setDescripion(xtra.getDescription());
			io.setOutAmount(xtra.getAmount());
			io.setReceiptNo("0000-00-00-0000000000");
		}else if(xtra.getTransType()==MoneyStatus.OTHER_INCOME.getId()){
			io.setTransType(MoneyStatus.OTHER_INCOME.getId());
			io.setDescripion(xtra.getDescription());
			io.setInAmount(xtra.getAmount());
			io.setReceiptNo("0000-00-00-0000000000");
		}else if(xtra.getTransType()==MoneyStatus.ADD_CAPITAL.getId()){
			io.setTransType(MoneyStatus.ADD_CAPITAL.getId());
			io.setDescripion(xtra.getDescription() + " " + MoneyStatus.ADD_CAPITAL.getName());
			io.setInAmount(xtra.getAmount());
			io.setReceiptNo("0000-00-00-0000000000");
		}else if(xtra.getTransType()==MoneyStatus.OTHER_EXP.getId()){
			io.setTransType(MoneyStatus.OTHER_EXP.getId());
			io.setDescripion(xtra.getDescription());
			io.setOutAmount(xtra.getAmount());
			io.setReceiptNo("0000-00-00-0000000000");
		}else if(xtra.getTransType()==MoneyStatus.CASH_LOAN.getId()){
			io.setTransType(MoneyStatus.CASH_LOAN.getId());
			io.setDescripion(xtra.getDescription()+ ". Borrowed by " + customer.getFullname());
			io.setOutAmount(xtra.getAmount());
			io.setReceiptNo(receiptNo);//DeliveryItemReceipt.generateNewReceiptNo());
			Payment.customerPayment(xtra.getCustomer(), xtra.getAmount(), 0.00, PaymentTransactionType.CASH_LOAN, "Client borrowed amount is " + Currency.formatAmount(xtra.getAmount()),receiptNo);
		}else if(xtra.getTransType()==MoneyStatus.REFUND.getId()){
			io.setTransType(MoneyStatus.REFUND.getId());
			io.setDescripion(xtra.getDescription() + "- Refund to " + customer.getFullname());
			io.setOutAmount(xtra.getAmount());
			io.setReceiptNo("0000-00-00-0000000000");//DeliveryItemReceipt.generateNewReceiptNo());
		}else if(xtra.getTransType()==MoneyStatus.SALARY.getId()){
			io.setTransType(MoneyStatus.SALARY.getId());
			io.setDescripion(xtra.getDescription());
			io.setOutAmount(xtra.getAmount());
			io.setReceiptNo("0000-00-00-0000000000");
		}	
		io.setUserDtls(user);
		io.setCustomer(customer);
		io.save(io);
		
		if(xtra.getTransType()==MoneyStatus.CASH_LOAN.getId()){
			String sql = "SELECT * FROM receiptmanagement WHERE isactivated=1 AND recIsActive!=0";
			Receipt rpt = Receipt.retrieve(sql, new String[0]).get(0);
			long newOR = Long.valueOf(receiptNo);
			long prevOR = rpt.getLatestOR();
			rpt.setLatestOR(newOR);
			rpt.setPreviousOR(prevOR);
			rpt.save();
			
			printRpt(customer, receiptNo, user, xtra.getAmount());
		}
	}
	
	private void printRpt(Customer cus, String receiptNo, UserDtls user, double amount) {
		String printerIsOn = ReadConfig.value(Ipos.PRINTER_ISON);
		
			ReceiptInfo rp = ReceiptXML.value();
		
			String receiptView = "";
			if(rp.getPosStatus()!=null && !rp.getPosStatus().isEmpty()){
				receiptView = rp.getPosStatus() +"\n";
			}
			if(rp.getOwner()!=null && !rp.getOwner().isEmpty()){
				receiptView += rp.getOwner() +"\n";
			}
			if(rp.getTitle()!=null && !rp.getTitle().isEmpty()){
				receiptView += rp.getTitle() +"\n";
			}
			if(rp.getAdditionalDetails()!=null && !rp.getAdditionalDetails().isEmpty()){
				receiptView += rp.getAdditionalDetails() +"\n";
			}
			if(rp.getAdditionalDetails2()!=null && !rp.getAdditionalDetails2().isEmpty()){
				receiptView += rp.getAdditionalDetails2() +"\n";
			}
			if(rp.getPosserial()!=null && !rp.getPosserial().isEmpty()){
				receiptView += rp.getPosserial() +"\n";
			}
			if(rp.getTelephoneNumber()!=null && !rp.getTelephoneNumber().isEmpty()){
				receiptView += rp.getTelephoneNumber() +"\n";
			}
			if(rp.getEmail()!=null && !rp.getEmail().isEmpty()){
				receiptView += rp.getEmail() +"\n\n";
			}
			
			receiptView += "Cashier: " + user.getFirstname() +"\n";
			receiptView += "Printed Date: " + DateUtils.getCurrentDateMMDDYYYYTIME() +"\n";
			receiptView += "Receipt No: " + receiptNo +"\n";
			receiptView += "Customer: " + cus.getFullname() + "\n";
			receiptView += "---------------------------------------------\n";
			receiptView += "Amount Loan: " + Currency.formatAmount(amount) +"\n";
			receiptView += "---------------------------------------------\n";
			receiptView += "***THANK YOU***";
			receiptView += "\n\n\n\n\n";
			
			//save copy of receipt in text format file
			ReceiptRecording.saveToFileReceipt(receiptView, receiptNo);
		
			if("1".equalsIgnoreCase(printerIsOn)){//active
				cashdrawerOpen();
				printReceipt(receiptNo); //send to printer
				cuttReceiptPaper();
			}
		
	}
	
	public void printReceipt(String receiptNo){
		
		String receiptLocation = ReadConfig.value(Ipos.RECEIPTS_LOG);
		String receiptFile = receiptLocation + receiptNo + ".txt";
		
		//PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		//DocPrintJob job = service.createPrintJob();
		/*URL url = new URL(
		   "http://www.apress.com/ApressCorporate/supplement/1/421/bcm.gif ");*/
		try{
			
		PrintService[] printServices;
		String printerName = ReadConfig.value(Ipos.PRINTER_NAME);
		
		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		printServiceAttributeSet.add(new PrinterName(printerName, null));
		printServices = PrintServiceLookup.lookupPrintServices(null, printServiceAttributeSet);
		
		
		DocPrintJob job = printServices[0].createPrintJob();
		
		//String FILE_NAME = "C:\\ipos\\receipts\\000000000000026.txt";
		FileInputStream textStream = new FileInputStream(receiptFile);
		
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc doc = new SimpleDoc(textStream, flavor, null);
		PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
		attrs.add(new Copies(1));
		job.print(doc, attrs);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void cashdrawerOpen() {
        
        byte[] open = {27, 112, 48, 55, 121};
        String printer = ReadConfig.value(Ipos.PRINTER_NAME);
        PrintServiceAttributeSet printserviceattributeset = new HashPrintServiceAttributeSet();
        printserviceattributeset.add(new PrinterName(printer,null));
        PrintService[] printservice = PrintServiceLookup.lookupPrintServices(null, printserviceattributeset);
        if(printservice.length!=1){
            System.out.println("Printer not found");
        }
        PrintService pservice = printservice[0];
        DocPrintJob job = pservice.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(open,flavor,null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        try {
            job.print(doc, aset);
        } catch (PrintException ex) {
            System.out.println(ex.getMessage());
        }
    }
	
	public void cuttReceiptPaper() {
        
		byte[] cutter = {29, 86,49};
        String printer = ReadConfig.value(Ipos.PRINTER_NAME);
        PrintServiceAttributeSet printserviceattributeset = new HashPrintServiceAttributeSet();
        printserviceattributeset.add(new PrinterName(printer,null));
        PrintService[] printservice = PrintServiceLookup.lookupPrintServices(null, printserviceattributeset);
        if(printservice.length!=1){
            System.out.println("Printer not found");
        }
        PrintService pservice = printservice[0];
        DocPrintJob job = pservice.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(cutter,flavor,null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        try {
            job.print(doc, aset);
        } catch (PrintException ex) {
            System.out.println(ex.getMessage());
        }
    }
	
	public List<Xtras> getXtras() {
		return xtras;
	}

	public void setXtras(List<Xtras> xtras) {
		this.xtras = xtras;
	}

	public String getSearchDescription() {
		return searchDescription;
	}

	public void setSearchDescription(String searchDescription) {
		this.searchDescription = searchDescription;
	}

	public Date getSearchDateFrom() {
		if(searchDateFrom==null){
			searchDateFrom = DateUtils.getDateToday();
		}
		return searchDateFrom;
	}

	public void setSearchDateFrom(Date searchDateFrom) {
		this.searchDateFrom = searchDateFrom;
	}

	public Date getSearchDateTo() {
		if(searchDateTo==null){
			searchDateTo = DateUtils.getDateToday();
		}
		return searchDateTo;
	}

	public void setSearchDateTo(Date searchDateTo) {
		this.searchDateTo = searchDateTo;
	}

	public List getCustomers() {
		
		customers = new ArrayList<>();
		
		Customer customer = new Customer();
		customer.setIsactive(1);
		
		if(getSearchName()!=null && !getSearchName().isEmpty()){
			customer.setFullname(getSearchName());
		}
		
		for(Customer cus : Customer.retrieve(customer)){
			customers.add(new SelectItem(cus.getCustomerid(), cus.getFullname()));
		}
		
		return customers;
	}

	public void setCustomers(List customers) {
		this.customers = customers;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public List getTransTypes() {
		
		transTypes = new ArrayList<>();
		
		transTypes.add(new SelectItem(MoneyStatus.ALLOWANCES.getId(), MoneyStatus.ALLOWANCES.getName()));
		transTypes.add(new SelectItem(MoneyStatus.CASH_LOAN.getId(), MoneyStatus.CASH_LOAN.getName()));
		transTypes.add(new SelectItem(MoneyStatus.OTHER_INCOME.getId(), MoneyStatus.OTHER_INCOME.getName()));
		transTypes.add(new SelectItem(MoneyStatus.OTHER_EXP.getId(), MoneyStatus.OTHER_EXP.getName()));
		transTypes.add(new SelectItem(MoneyStatus.REFUND.getId(), MoneyStatus.REFUND.getName()));
		transTypes.add(new SelectItem(MoneyStatus.SALARY.getId(), MoneyStatus.SALARY.getName()));
		
		return transTypes;
	}

	public void setTransTypes(List transTypes) {
		this.transTypes = transTypes;
	}

	public int getTransTypeId() {
		return transTypeId;
	}

	public void setTransTypeId(int transTypeId) {
		this.transTypeId = transTypeId;
	}

	public Xtras getXtrasData() {
		return xtrasData;
	}

	public void setXtrasData(Xtras xtrasData) {
		this.xtrasData = xtrasData;
	}

	public List<Xtras> getXtraSelected() {
		return xtraSelected;
	}

	public void setXtraSelected(List<Xtras> xtraSelected) {
		this.xtraSelected = xtraSelected;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getDateTrans() {
		if(dateTrans==null){
			dateTrans = DateUtils.getDateToday();
		}
		return dateTrans;
	}

	public void setDateTrans(Date dateTrans) {
		this.dateTrans = dateTrans;
	}

	public String getStatus() {
		if(status==null){
			status = Status.NEW.getName();
		}
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getXtraDescription() {
		return xtraDescription;
	}

	public void setXtraDescription(String xtraDescription) {
		this.xtraDescription = xtraDescription;
	}

	public boolean isCustomerEnable() {
		return customerEnable;
	}

	public void setCustomerEnable(boolean customerEnable) {
		this.customerEnable = customerEnable;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	
}
