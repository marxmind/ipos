package com.italia.ipos.bean;

import java.io.FileInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;

import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.CustomerPayment;
import com.italia.ipos.controller.CustomerPaymentTrans;
import com.italia.ipos.controller.DeliveryItemReceipt;
import com.italia.ipos.controller.DeliveryItemTrans;
import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.MoneyIO;
import com.italia.ipos.controller.Payment;
import com.italia.ipos.controller.Product;
import com.italia.ipos.controller.ProductProperties;
import com.italia.ipos.controller.PurchasedItem;
import com.italia.ipos.controller.Receipt;
import com.italia.ipos.controller.ReceiptInfo;
import com.italia.ipos.controller.RentedBottle;
import com.italia.ipos.controller.RentedBottleTrans;
import com.italia.ipos.controller.ReturnRentedItems;
import com.italia.ipos.controller.Transactions;
import com.italia.ipos.controller.UOM;
import com.italia.ipos.controller.UserDtls;
import com.italia.ipos.enm.DateFormatter;
import com.italia.ipos.enm.HistoryReceiptStatus;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.enm.MoneyStatus;
import com.italia.ipos.enm.PaymentTransactionType;
import com.italia.ipos.enm.ReceiptStatus;
import com.italia.ipos.enm.ReturnCondition;
import com.italia.ipos.enm.ReturnStatus;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.reader.ReceiptXML;
import com.italia.ipos.utils.Currency;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.Whitelist;

/**
 * 
 * @author mark italia
 * @since 11/05/2016
 * @version 1.0
 */
@ManagedBean(name="clientsBean", eager=true)
@ViewScoped
public class ClientsBean implements Serializable {

	private static final long serialVersionUID = 1094811425227384673L;
	
	private List<CustomerPayment> customers = Collections.synchronizedList(new ArrayList<CustomerPayment>());
	private String searchClients;
	private Customer customer; 
	private CustomerPayment customerPayment;
	private String inputamounttmp;
	private String inputamount;
	
	private List<CustomerPayment> customerPays = Collections.synchronizedList(new ArrayList<CustomerPayment>());
	private String hisDateFrom;
	private String hisDateTo;
	private String hisName;
	
	private List<CustomerPaymentTrans> historys = Collections.synchronizedList(new ArrayList<CustomerPaymentTrans>());
	private String hisTotal;
	private CustomerPayment customerPaymentData;
	
	private List<Customer> rents = Collections.synchronizedList(new ArrayList<Customer>());
	private String searchClientsRented;
	
	private List<Customer> rentHistory = Collections.synchronizedList(new ArrayList<Customer>());
	private String searchClientsRentedHistory;
	
	private List<Customer> returns = Collections.synchronizedList(new ArrayList<Customer>());
	private String searchClientsReturn;
	
	private List<ReturnRentedItems> returnHistory = Collections.synchronizedList(new ArrayList<ReturnRentedItems>());
	private String searchClientsReturnHistory;
	private Date returnHistoryDateFrom;
	private Date returnHistoryDateTo;
	
	private List<Customer> customerReceipts = Collections.synchronizedList(new ArrayList<Customer>());
	private String searchBought;
	private Date receiptDateFrom;
	private Date receiptDateTo;
	
	private List<DeliveryItemTrans> itemSolds = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
	
	private RentedBottle returnBottle;
	private ReturnRentedItems returnData;
	private String returnDate;
	private String returnProductName;
	private String returnUom;
	private double returnRemainingQty;
	private double returnQty;
	private double returnCharges;
	private int itemConditionId;
	private List itemCondition;
	private String returnRemarks;
	private String returnStatus;
	
	private List<CustomerPayment> paymentData;
	
	private List<Transactions> transReceipt = Collections.synchronizedList(new ArrayList<Transactions>());
	private Transactions transSelected;
	private String transReceiptNo;
	private String transReceiptAmount;
	private int receiptStatusId;
	private List receiptStatus;
	
	public List<CustomerPayment> getPaymentData() {
		return paymentData;
	}

	public void setPaymentData(List<CustomerPayment> paymentData) {
		this.paymentData = paymentData;
	}

	public void onTabChangeView(TabChangeEvent event){
		
		if("Payments".equalsIgnoreCase(event.getTab().getTitle())){
			startPaymnets();
		}else if("Product Receipts".equalsIgnoreCase(event.getTab().getTitle())){
			startBought();
		}else if("Rented Items Balances".equalsIgnoreCase(event.getTab().getTitle())){
			startRented();
		}else if("Collectible Items".equalsIgnoreCase(event.getTab().getTitle())){
			startReturn();
		}
		
	}
	
	public void onTabChangeRented(TabChangeEvent event){
		
		if("Balances".equalsIgnoreCase(event.getTab().getTitle())){
			startRented();
		}else if("History".equalsIgnoreCase(event.getTab().getTitle())){
			startRentedHistory();
		}
		
	}
	
	public void onTabChangeReturn(TabChangeEvent event){
		
		if("Items".equalsIgnoreCase(event.getTab().getTitle())){
			startReturn();
		}else if("History".equalsIgnoreCase(event.getTab().getTitle())){
			startReturnHistory();
		}
		
	}
	
	public void onTabChange(TabChangeEvent event) {
        /*FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: " + event.getTab().getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);*/
		
		if("Payments".equalsIgnoreCase(event.getTab().getTitle())){
			startPaymnets();
		}else if("History".equalsIgnoreCase(event.getTab().getTitle())){
			startHistory();
		}
		
    }
         
    public void onTabClose(TabCloseEvent event) {
        /*FacesMessage msg = new FacesMessage("Tab Closed", "Closed tab: " + event.getTab().getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);*/
    }
	
	@PostConstruct
	public void init(){
		startPaymnets();
	}
	
	public void startPaymnets(){
		customers = Collections.synchronizedList(new ArrayList<CustomerPayment>());
		
		CustomerPayment pay = new CustomerPayment();
		pay.setAmountbalance(new BigDecimal("1")); //greater than or equal 1
		pay.setPayisactive(1);
		Customer customer = new Customer();
		customer.setIsactive(1);
		if(getSearchClients()!=null && !getSearchClients().isEmpty()){
			customer.setFullname(Whitelist.remove(getSearchClients()));
		}
		customers = CustomerPayment.retrieve(pay,customer);
		Collections.reverse(customers);
	}
	
	public void printAll(){
		
	}
	
	public void supplyAmount(){
		System.out.println("Amount inputed : " + getInputamounttmp());
		setInputamount(getInputamounttmp());
	}
	
	public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        System.out.println("Old Value   "+ event.getOldValue()); 
        System.out.println("New Value   "+ event.getNewValue());
	}   
	
	public void updateBalance(){
		if(Login.getUserLogin().checkUserStatus()){
			
			if(getPaymentData()!=null && getPaymentData().size()>0){
				int cnt = 0;
				
				for(CustomerPayment pay : getPaymentData()){
					System.out.println("input amount >>: " + pay.getInputAmount());
					
					double amnt = pay.getInputAmount();
					double bal = pay.getAmountbalance().doubleValue();
					//String receiptNo =  "0000-00-00-0000000000";//DeliveryItemReceipt.generateNewReceiptNo();
					String receiptNo = DeliveryItemReceipt.generateNewReceiptNo();
					if(amnt>bal){
						amnt = bal;
					}
					
					if(amnt>0){
						setInputamount(amnt+"");
						updateBal(pay,receiptNo);
						cnt++;
					}
				}
				
				if(cnt>0){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Amount has been successfully updated.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "No balance has been updated.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please check the item to update.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void updateBal(CustomerPayment payment, String receiptNo){
			
			System.out.println("Input amnt : " + getInputamount());
			
			/*if(getInputamounttmp()!=null && !getInputamounttmp().isEmpty()){
				setInputamount(getInputamounttmp());
			}*/
			UserDtls user = Login.getUserLogin().getUserDtls();
			
			if(getInputamount()!=null && !getInputamount().isEmpty()){
			BigDecimal amntpaid = new BigDecimal(getInputamount().replace(",", ""));
			
			double remAmount = 0d;
			
			remAmount = rentedPayments(payment, amntpaid.doubleValue(), receiptNo);
			
			if(remAmount!=0){
			
			amntpaid = new BigDecimal(remAmount);	
				
			CustomerPaymentTrans trans = new CustomerPaymentTrans();
			trans.setPaymentdate(DateUtils.getCurrentDateYYYYMMDD());
			trans.setAmountpay(amntpaid);
			trans.setIspaid(1);
			trans.setPaytransisactive(1);
			trans.setCustomerPayment(payment);
			trans.setUserDtls(user);
			trans.setPaymentType(PaymentTransactionType.OTC.getId());
			trans.setReceiptNo(receiptNo);
			
			if(amntpaid.doubleValue()<0){
				trans.setRemarks("Wrong amount inputed");
			}else{
				trans.setRemarks("Over the Counter");
			}
			
			trans.save();
			
			//set temp data
			BigDecimal balamnt = payment.getAmountbalance();
			BigDecimal amntpaidorig = payment.getAmountpaid();
			String paiddate = payment.getAmountpaiddate();
			
			BigDecimal newBalamnt =  balamnt.subtract(amntpaid);
			
			//Update customerpayment table
			payment.setAmountpaid(amntpaid);
			payment.setAmountpaiddate(DateUtils.getCurrentDateYYYYMMDD());
			
			payment.setAmountprevpaid(amntpaidorig);
			payment.setAmountprevpaiddate(paiddate);
			
			payment.setAmountbalance(newBalamnt);
			payment.setAmountprevbalance(balamnt);
			payment.save();
			
			MoneyIO io = new MoneyIO();
			io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			io.setDescripion("Over the Counter paid by " + payment.getCustomer().getFullname());
			io.setTransType(MoneyStatus.OTC.getId());
			io.setInAmount(payment.getAmountpaid().doubleValue());
			io.setUserDtls(user);
			io.setReceiptNo(receiptNo);
			io.setCustomer(payment.getCustomer());
			
			io.save(io);
			
			DeliveryItemReceipt rec = new DeliveryItemReceipt();
			rec.setReceiptNo(receiptNo);
			rec.setRemarks("Over The Counter");
			rec.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			rec.setStatus(ReceiptStatus.POSTED.getId());
			rec.setIsActive(1);
			rec.setDeliveryChargeAmount(0);
			rec.setTotalAmount(0);
			rec.setBalanceAmount(0);
			rec.setDiscountAmount(0);
			rec.setQuantity(1);
			rec.setDownPayment(amntpaid.doubleValue());
			rec.setPaymentStatus(ReceiptStatus.FULL.getId());
			rec.setCustomer(payment.getCustomer());
			rec.setUserDtls(user);
			
			rec.save();
			
			
			String sql = "SELECT * FROM receiptmanagement WHERE isactivated=1 AND recIsActive!=0";
			Receipt rpt = Receipt.retrieve(sql, new String[0]).get(0);
			long newOR = Long.valueOf(receiptNo);
			long prevOR = rpt.getLatestOR();
			rpt.setLatestOR(newOR);
			rpt.setPreviousOR(prevOR);
			rpt.save();
			
			printRpt(payment, receiptNo, user);
			
			clearFields();
			init();
			
			//FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Amount has been successfully updated.", "");
            //FacesContext.getCurrentInstance().addMessage(null, msg);
            
			}else{
				clearFields();
				init();
				//FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Amount has been successfully updated.", "");
	            //FacesContext.getCurrentInstance().addMessage(null, msg);
			}
            
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong. Please login again.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		
	}
	
	private void printRpt(CustomerPayment payment, String receiptNo, UserDtls user) {
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
			receiptView += "Customer: " + payment.getCustomer().getFullname() + "\n";
			receiptView += "---------------------------------------------\n";
			receiptView += "Amount Paid: " + Currency.formatAmount(payment.getAmountpaid()) +"\n";
			receiptView += "Previous Paid Amount: " + Currency.formatAmount(payment.getAmountprevpaid()) + "\n";
			receiptView += "Previous Paid Date: " + payment.getAmountprevpaiddate()+ "\n";
			receiptView += "---------------------------------------------\n";
			receiptView += "Latest Balance Amount: " + Currency.formatAmount(payment.getAmountbalance())+ "\n";
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
	
	public String save(){
		
		if(Login.getUserLogin().checkUserStatus()){
			
			CustomerPaymentTrans trans = new CustomerPaymentTrans();
			
			
			trans.setUserDtls(Login.getUserLogin().getUserDtls());
			
			trans.save();
			clearFields();
			init();
		}
		
		return "save";
	}
	
	public String close(){
		clearFields();
		return "close";
	}
	
	public void print(){
		
	}
	
	public void clickItem(CustomerPayment cus){
		clearReceiptsTrans();
		Customer customer = Customer.retrieve(cus.getCustomer().getCustomerid()+"");
		setCustomer(customer);
		setCustomerPayment(cus);
		loadCustomerReceipt();
	}
	
	public void loadCustomerReceipt() {
		transReceipt = Collections.synchronizedList(new ArrayList<Transactions>());
		String sql = " AND tran.amountpurchased>0 AND tran.isvoidtrans=1 AND cus.customerid=? AND (tran.paytype=? OR tran.paytype=?) ";
		String[] params = new String[3];
		params[0] = getCustomer().getCustomerid()+"";
		params[1] = HistoryReceiptStatus.UNPAID.getId()+"";
		params[2] = HistoryReceiptStatus.PARTIALPAID.getId()+"";
		transReceipt = Transactions.retrieve(sql, params);
		
	}
	
	public void clickReciept(Transactions tran) {
		clearReceiptsTrans();
		setTransSelected(tran);
		setTransReceiptNo(tran.getReceipts());
		setTransReceiptAmount(Currency.formatAmount(tran.getAmountbal()));
		setReceiptStatusId(tran.getPaymentType());
	}
	
	public void savePaymentType() {
		if(getTransSelected()!=null) {
			
			Transactions tran = getTransSelected();
			tran.setPaymentType(getReceiptStatusId());
			tran.save();
			clearReceiptsTrans();
			loadCustomerReceipt();
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully saved.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}else {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Please select receipt.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void clearReceiptsTrans() {
		setTransSelected(null);
		setTransReceiptNo(null);
		setTransReceiptAmount(null);
		setReceiptStatusId(0);
	}
	
	public void clearFields(){
		setCustomer(null);
		setCustomerPayment(null);
		setInputamount(null);
		setInputamounttmp(null);
	}
	
	public void deleteRow(CustomerPayment  cus){
		if(Login.getUserLogin().checkUserStatus()){
			cus.setUserDtls(Login.getUserLogin().getUserDtls());
			cus.delete();
			init();
		}
	}
	
	public void startHistory(){
		customerPays = Collections.synchronizedList(new ArrayList<CustomerPayment>());
		
		CustomerPayment pay = new CustomerPayment();
		pay.setPayisactive(1);
		
		Customer cuz = new Customer();
		cuz.setIsactive(1);
		
		if(getHisName()!=null && !getHisName().isEmpty()){
			cuz.setFullname(getHisName().replace("--", ""));
		}
		
		customerPays = CustomerPayment.retrieve(pay, cuz);
		Collections.reverse(customerPays);
		
		
	}
	
	public void clickPaymentDetails(CustomerPayment pay){
		setCustomerPaymentData(pay);
		loadPaymentHistory();
	}
	
	public void loadPaymentHistory(){
		historys = Collections.synchronizedList(new ArrayList<CustomerPaymentTrans>());
		if(getCustomerPaymentData()!=null){
			CustomerPayment pay = new CustomerPayment();
			pay.setPayisactive(1);
			pay.setCpayid(getCustomerPaymentData().getCpayid());
			
			CustomerPaymentTrans tn = new CustomerPaymentTrans();
			tn.setPaytransisactive(1);
			tn.setIspaid(1);
			
			double amount = 0d;
			for(CustomerPaymentTrans tran : CustomerPaymentTrans.retrieve(tn,pay)){
				amount += tran.getAmountpay().doubleValue();
				historys.add(tran);
			}
			setHisTotal(Currency.formatAmount(amount));
			Collections.reverse(historys);
		}
	}
	
	public double rentedPayments(CustomerPayment payment, double amountPaid, String receiptNo){
		Customer cus = new Customer();
		cus.setIsactive(1);
		cus.setCustomerid(payment.getCustomer().getCustomerid());
		double remAmountPaid =  amountPaid;
		for(RentedBottle bot : RentedBottle.retrieve(cus)){
			double remAmount = 0d;	
			if(bot.getCurrentBalance()>0){
				
				double oldBalance = bot.getCurrentBalance();
				double oldPaidAmount = bot.getCurrentPaidAmount();
				String oldDatePaid = bot.getCurrentDateTrans();
				
				/**
				 * same amount input
				 */
				if(oldBalance==amountPaid && remAmountPaid>0){
					bot.setCurrentBalance(0.00);
					bot.setCurrentPaidAmount(amountPaid);
					bot.setCurrentDateTrans(DateUtils.getCurrentDateYYYYMMDD());
					
					bot.setPrevBalance(oldBalance);
					bot.setPrevPaidAmount(oldPaidAmount);
					bot.setPrevDateTrans(oldDatePaid);
					
					bot.setUserDtls(Login.getUserLogin().getUserDtls());
					bot.save();
					remAmountPaid = 0;
					rentedPayment(payment, new BigDecimal(amountPaid), receiptNo);
					break;
				}
				
				if(oldBalance>amountPaid && remAmountPaid>0){
				
				remAmount = oldBalance - amountPaid;
				
				bot.setCurrentBalance(remAmount);
				bot.setCurrentPaidAmount(amountPaid);
				bot.setCurrentDateTrans(DateUtils.getCurrentDateYYYYMMDD());
				
				bot.setPrevBalance(oldBalance);
				bot.setPrevPaidAmount(oldPaidAmount);
				bot.setPrevDateTrans(oldDatePaid);
				
				bot.setUserDtls(Login.getUserLogin().getUserDtls());
				bot.save();
				remAmountPaid = 0;
				rentedPayment(payment, new BigDecimal(amountPaid), receiptNo);
				break;
				
				}
				
				if(oldBalance<amountPaid && remAmountPaid>0){
					
					remAmountPaid = amountPaid - oldBalance;
					
					amountPaid = remAmountPaid; //next iteration remaining amount to be use for paying
					
					bot.setCurrentBalance(0.00);
					bot.setCurrentPaidAmount(oldBalance);
					bot.setCurrentDateTrans(DateUtils.getCurrentDateYYYYMMDD());
					
					bot.setPrevBalance(oldBalance);
					bot.setPrevPaidAmount(oldPaidAmount);
					bot.setPrevDateTrans(oldDatePaid);
					
					bot.setUserDtls(Login.getUserLogin().getUserDtls());
					bot.save();
					
					rentedPayment(payment, new BigDecimal(oldBalance), receiptNo);
				}
			}
		}
		return remAmountPaid;
	}
	
	public void rentedPayment(CustomerPayment payment, BigDecimal amntpaid, String receiptNo){
		
		CustomerPaymentTrans trans = new CustomerPaymentTrans();
		trans.setPaymentdate(DateUtils.getCurrentDateYYYYMMDD());
		trans.setAmountpay(amntpaid);
		trans.setIspaid(1);
		trans.setPaytransisactive(1);
		trans.setCustomerPayment(payment);
		trans.setUserDtls(Login.getUserLogin().getUserDtls());
		trans.setPaymentType(PaymentTransactionType.RENTED.getId());
		
		if(amntpaid.doubleValue()<0){
			trans.setRemarks("Wrong amount inputed");
		}else{
			trans.setRemarks("Rented Items Payment");
		}
		
		trans.save();
		
		//set temp data
		BigDecimal balamnt = payment.getAmountbalance();
		BigDecimal amntpaidorig = payment.getAmountpaid();
		String paiddate = payment.getAmountpaiddate();
		
		BigDecimal newBalamnt =  balamnt.subtract(amntpaid);
		
		//Update customerpayment table
		payment.setAmountpaid(amntpaid);
		payment.setAmountpaiddate(DateUtils.getCurrentDateYYYYMMDD());
		
		payment.setAmountprevpaid(amntpaidorig);
		payment.setAmountprevpaiddate(paiddate);
		
		payment.setAmountbalance(newBalamnt);
		payment.setAmountprevbalance(balamnt);
		payment.save();
		
		MoneyIO io = new MoneyIO();
		io.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
		io.setDescripion("Rented Item paid by " + payment.getCustomer().getFullname());
		io.setTransType(MoneyStatus.RENTED.getId());
		io.setInAmount(payment.getAmountpaid().doubleValue());
		io.setUserDtls(Login.getUserLogin().getUserDtls());
		io.setReceiptNo(receiptNo);
		io.setCustomer(payment.getCustomer());
		
		io.save(io);
		
	}
	
	public void startBought(){
		
		customerReceipts = Collections.synchronizedList(new ArrayList<Customer>());
		List<DeliveryItemReceipt> receipts = Collections.synchronizedList(new ArrayList<DeliveryItemReceipt>());
		
		Customer cus = new Customer();
		cus.setIsactive(1);
		
		if(getSearchBought()!=null && !getSearchBought().isEmpty()){
			cus.setFullname(getSearchBought().replace("--", ""	));
		}
		
		DeliveryItemReceipt rc = new DeliveryItemReceipt();
		rc.setIsActive(1);
		rc.setBetween(true);
		rc.setDateFrom(DateUtils.convertDate(getReceiptDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
		rc.setDateTo(DateUtils.convertDate(getReceiptDateTo(),DateFormatter.YYYY_MM_DD.getName()));
		
		List<Customer> searchCustomer = Collections.synchronizedList(new ArrayList<Customer>());
		for(DeliveryItemReceipt rpt : DeliveryItemReceipt.retrieve(rc, cus)){
			searchCustomer.add(rpt.getCustomer());
		}
		Map<Long, Customer> customers = Collections.synchronizedMap(new HashMap<Long, Customer>());
		for(Customer customer : searchCustomer){
			
			Customer cz = new Customer();
			cz.setCustomerid(customer.getCustomerid());
			cz.setIsactive(1);
			
			DeliveryItemReceipt rec = new DeliveryItemReceipt();
			rec.setIsActive(1);
			rec.setBetween(true);
			rec.setDateFrom(DateUtils.convertDate(getReceiptDateFrom(),DateFormatter.YYYY_MM_DD.getName()));
			rec.setDateTo(DateUtils.convertDate(getReceiptDateTo(),DateFormatter.YYYY_MM_DD.getName()));
			receipts = Collections.synchronizedList(new ArrayList<DeliveryItemReceipt>());
			boolean isExist = false;
			for(DeliveryItemReceipt rpt : DeliveryItemReceipt.retrieve(rec, cz)){
				isExist = true;
				receipts.add(rpt);
			}
			
			
			
			/*if(isExist){
				customer.setReceipts(receipts);
				customerReceipts.add(customer);
			}*/
			
			if(customers!=null){
				if(customers.containsKey(customer.getCustomerid())){
					customers.get(customer.getCustomerid()).setReceipts(receipts);
				}else{
					customer.setReceipts(receipts);
					customers.put(customer.getCustomerid(), customer);
				}
			}else{
				customer.setReceipts(receipts);
				customers.put(customer.getCustomerid(), customer);
			}
			
			
			
		}
		for(Customer cuz : customers.values()){
			customerReceipts.add(cuz);
		}
		
		Collections.reverse(customerReceipts);
		
	}
	
	public void clickItemOrder(DeliveryItemReceipt rpt){
		itemSolds = Collections.synchronizedList(new ArrayList<DeliveryItemTrans>());
		
		DeliveryItemTrans tran = new DeliveryItemTrans();
		tran.setIsActive(1);
		DeliveryItemReceipt recpt = new DeliveryItemReceipt();
		recpt.setId(rpt.getId());
		recpt.setIsActive(1);
		
		for(DeliveryItemTrans trn :  DeliveryItemTrans.retrieve(tran,recpt)){
			
			/*Product prod = new Product();
			prod = Product.retrieve(trn.getProduct().getProdid()+"");*/
			ProductProperties prop = new ProductProperties();
			prop = ProductProperties.properties(trn.getProduct().getProductProperties().getPropid()+"");
			trn.getProduct().setProductProperties(prop);
			itemSolds.add(trn);
			
		}
		
		if(itemSolds.size()==0){
			Transactions trn = new Transactions();
			trn.setIsvoidtrans(1);
			trn.setReceipts(rpt.getReceiptNo());
			
			PurchasedItem it = new PurchasedItem();
			it.setIsactiveitem(1);
			
			for(PurchasedItem item : PurchasedItem.retrieve(it,trn)){
				DeliveryItemTrans tn = new DeliveryItemTrans();
				tn.setDateTrans(item.getDatesold());
				tn.setStatus(2);
				Product prod = Product.retrieve(item.getProduct().getProdid()+"");
				ProductProperties prop = new ProductProperties();
				prop = ProductProperties.properties(prod.getProductProperties().getPropid()+"");
				prod.setProductProperties(prop);
				tn.setProduct(prod);
				tn.setQuantity(item.getQty());
				double totalPrice = item.getSellingPrice().doubleValue() * item.getQty();
				tn.setSellingPrice(totalPrice);
				itemSolds.add(tn);
			}
			
		}
		
		Collections.reverse(itemSolds);
		
	}
	
	public void startRented(){
		rents = Collections.synchronizedList(new ArrayList<Customer>());
		List<RentedBottle>	bottles = Collections.synchronizedList(new ArrayList<RentedBottle>());
		
		String sql = " AND rent.currentBalance!=0"; 
		if(getSearchClientsRented()!=null && !getSearchClientsRented().isEmpty()){
			sql += " AND cus.fullname like '%"+ getSearchClientsRented().replace("--", ""	) +"%'";
		}
		List<Customer> searchCustomer = Collections.synchronizedList(new ArrayList<Customer>());
		String[] params = new String[0]; 
		for(RentedBottle bot : RentedBottle.retrieve(sql, params)){
			searchCustomer.add(bot.getCustomer());
		}
		
		Map<Long, Customer> customers = Collections.synchronizedMap(new HashMap<Long, Customer>());
		for(Customer customer : searchCustomer){
			
			Customer cz = new Customer();
			cz.setIsactive(1);
			cz.setCustomerid(customer.getCustomerid());
			bottles = Collections.synchronizedList(new ArrayList<RentedBottle>());
			boolean isExist = false;
			sql =" AND rent.currentBalance!=0 AND cus.customerid=?";
			params = new String[1];
			params[0] = customer.getCustomerid()+"";
			/*for(RentedBottle bot : RentedBottle.retrieve(cz)){
			if(bot.getCurrentBalance()>0){
				bottles.add(bot);
				isExist = true;
				}
			}*/
			for(RentedBottle bot : RentedBottle.retrieve(sql, params)){
					bottles.add(bot);
					isExist = true;
			}
		
			/*if(isExist){
				customer.setRentedBottle(bottles);
				rents.add(customer);
			}*/
			if(customers!=null){
				if(customers.containsKey(customer.getCustomerid())){
					customers.get(customer.getCustomerid()).setRentedBottle(bottles);
				}else{
					customer.setRentedBottle(bottles);
					customers.put(customer.getCustomerid(), customer);
				}
			}else{
				customer.setRentedBottle(bottles);
				customers.put(customer.getCustomerid(), customer);
			}
			
		}
		for(Customer cuz : customers.values()){
			rents.add(cuz);
		}
		Collections.reverse(rents);
	}
	
	public void startRentedHistory(){
		rentHistory = Collections.synchronizedList(new ArrayList<Customer>());
		List<RentedBottle>	bottles = Collections.synchronizedList(new ArrayList<RentedBottle>());
		
		/*Customer cus = new Customer();
		cus.setIsactive(1);
		
		if(getSearchClientsRentedHistory()!=null && !getSearchClientsRentedHistory().isEmpty()){
			cus.setFullname(getSearchClientsRentedHistory().replace("--", ""	));
		}*/
		String sql = ""; 
		if(getSearchClientsRentedHistory()!=null && !getSearchClientsRentedHistory().isEmpty()){
			sql += " AND cus.fullname like '%"+ getSearchClientsRentedHistory().replace("--", ""	) +"%'";
		}else{
			sql +=" limit 10";
		}
		
		List<Customer> searchCustomer = Collections.synchronizedList(new ArrayList<Customer>());
		String[] params = new String[0]; 
		for(RentedBottle bot : RentedBottle.retrieve(sql, params)){
			searchCustomer.add(bot.getCustomer());
		}
		Map<Long, Customer> customers = Collections.synchronizedMap(new HashMap<Long, Customer>());
		for(Customer customer : searchCustomer){
			
			Customer cz = new Customer();
			cz.setIsactive(1);
			cz.setCustomerid(customer.getCustomerid());
			bottles = Collections.synchronizedList(new ArrayList<RentedBottle>());
			boolean isExist = false;
			for(RentedBottle bot : RentedBottle.retrieve(cz)){
				bottles.add(bot);
				isExist = true;
			}
			
			/*if(isExist){
				customer.setRentedBottle(bottles);
				rentHistory.add(customer);
			}*/
			if(customers!=null){
				if(customers.containsKey(customer.getCustomerid())){
					customers.get(customer.getCustomerid()).setRentedBottle(bottles);
				}else{
					customer.setRentedBottle(bottles);
					customers.put(customer.getCustomerid(), customer);
				}
			}else{
				customer.setRentedBottle(bottles);
				customers.put(customer.getCustomerid(), customer);
			}
			
		}
		for(Customer cuz : customers.values()){
			rentHistory.add(cuz);
		}
		Collections.reverse(rentHistory);
	}
	
	public void startReturn(){
	
	returns = Collections.synchronizedList(new ArrayList<Customer>());
	List<RentedBottle>	bottles = Collections.synchronizedList(new ArrayList<RentedBottle>());
		
		/*Customer cus = new Customer();
		cus.setIsactive(1);
		
		if(getSearchClientsReturn()!=null && !getSearchClientsReturn().isEmpty()){
			cus.setFullname(getSearchClientsReturn().replace("--", ""	));
		}*/
	
		String sql = " AND rent.currentQuantity!=0"; 
		if(getSearchClientsReturn()!=null && !getSearchClientsReturn().isEmpty()){
			sql += " AND cus.fullname like '%"+ getSearchClientsReturn().replace("--", "") +"%'";
		}
		List<Customer> searchCustomer = Collections.synchronizedList(new ArrayList<Customer>());
		String[] params = new String[0]; 
		for(RentedBottle bot : RentedBottle.retrieve(sql, params)){
			searchCustomer.add(bot.getCustomer());
		}
		
		Map<Long, Customer> customers = Collections.synchronizedMap(new HashMap<Long, Customer>());
		for(Customer customer : searchCustomer){
			
			Customer cz = new Customer();
			cz.setIsactive(1);
			cz.setCustomerid(customer.getCustomerid());
			
			bottles = Collections.synchronizedList(new ArrayList<RentedBottle>());
			boolean isExist = false;
			sql =" AND rent.currentQuantity!=0 AND cus.customerid=?";
			params = new String[1];
			params[0] = customer.getCustomerid()+"";
			for(RentedBottle bot : RentedBottle.retrieve(sql, params)){
					bottles.add(bot);
					isExist=true;
			}
			
			/*if(isExist){
				customer.setRentedBottle(bottles);
				returns.add(customer);
			}*/
			
			
			if(customers!=null){
				if(customers.containsKey(customer.getCustomerid())){
					customers.get(customer.getCustomerid()).setRentedBottle(bottles);
				}else{
					customer.setRentedBottle(bottles);
					customers.put(customer.getCustomerid(), customer);
				}
			}else{
				customer.setRentedBottle(bottles);
				customers.put(customer.getCustomerid(), customer);
			}
			
		}
		for(Customer cuz : customers.values()){
			returns.add(cuz);
		}
		Collections.reverse(returns);
	}
	
	public void startReturnHistory(){
		returnHistory = Collections.synchronizedList(new ArrayList<ReturnRentedItems>());
		
		String sql = "SELECT * FROM returnrenteditems WHERE  retIsActive=1 AND (retDateTrans>=? AND retDateTrans<=? )";
		String[] params = new String[2];
		
		if(getSearchClientsReturnHistory()!=null && !getSearchClientsReturnHistory().isEmpty()){
			sql = "SELECT * FROM returnrenteditems ret, productproperties prop, customer cus WHERE ret.propid=prop.propid AND ret.customerid=cus.customerid AND  ret.retIsActive=1 AND cus.cusisactive=1 AND (ret.retDateTrans>=? AND ret.retDateTrans<=? ) "
					+ "AND ( cus.fullname like '%" + getSearchClientsReturnHistory().replace("--", "") + "%' OR "
							+ " prop. productname like '%" + getSearchClientsReturnHistory().replace("--", "") + "%')";
		}else{
			sql += " LIMIT 10";
		}
		params[0] = DateUtils.convertDate(getReturnHistoryDateFrom(),DateFormatter.YYYY_MM_DD.getName());
		params[1] = DateUtils.convertDate(getReturnHistoryDateTo(),DateFormatter.YYYY_MM_DD.getName());
		
		
		for(ReturnRentedItems rts : ReturnRentedItems.retrieve(sql, params)){
			Customer cus = Customer.retrieve(rts.getCustomer().getCustomerid()+"");
			ProductProperties prop = ProductProperties.properties(rts.getProductProperties().getPropid()+"");
			UOM uom = UOM.uom(rts.getUom().getUomid()+"");
			rts.setCustomer(cus);
			rts.setProductProperties(prop);
			rts.setUom(uom);
			returnHistory.add(rts);
		}
		
		
		Collections.reverse(returnHistory);
	}
	
	public void clickReturnHis(ReturnRentedItems item){
		clearReturn();
		Customer customer = new Customer();
		customer.setCustomerid(item.getCustomer().getCustomerid());
		customer.setIsactive(1);
		
		ProductProperties prop = new ProductProperties();
		prop.setPropid(item.getProductProperties().getPropid());
		prop.setIsactive(1);
		
		UOM uom = new UOM();
		uom.setUomid(item.getUom().getUomid());
		uom.setIsactive(1);
		
		ReturnRentedItems itm = new ReturnRentedItems();
		itm.setIsActive(1);
		itm.setStatus(item.getStatus());
		
		RentedBottle trans = RentedBottle.retrieve(customer, prop, uom).get(0);
		returnItemData(customer, prop, uom, itm, trans);
		
	}
	
	public void clickReturnView(RentedBottle trans){
		clearReturn();
		Customer customer = new Customer();
		customer.setCustomerid(trans.getCustomer().getCustomerid());
		customer.setIsactive(1);
		
		ProductProperties prop = new ProductProperties();
		prop.setPropid(trans.getProductProperties().getPropid());
		prop.setIsactive(1);
		
		UOM uom = new UOM();
		uom.setUomid(trans.getUom().getUomid());
		uom.setIsactive(1);
		
		ReturnRentedItems item = new ReturnRentedItems();
		item.setIsActive(1);
		item.setStatus(ReturnStatus.NEW.getId());
		
		returnItemData(customer, prop, uom, item, trans);
		
	}
	
	public void returnItemData(Customer customer,ProductProperties prop,UOM uom,ReturnRentedItems item, RentedBottle trans){
		
		ReturnRentedItems rentedItem = null;
		
		try{rentedItem = ReturnRentedItems.retrieve(item, customer , prop , uom).get(0);}catch(IndexOutOfBoundsException e){}
		
		if(rentedItem==null){
			
			rentedItem = new ReturnRentedItems();
			setReturnDate(DateUtils.getCurrentDateYYYYMMDD());
			rentedItem.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			setReturnProductName(trans.getProductProperties().getProductname());;
			rentedItem.setProductProperties(trans.getProductProperties());
			setReturnUom(trans.getUom().getSymbol());
			rentedItem.setUom(trans.getUom());
			setReturnRemainingQty(trans.getCurrentQty());
			setReturnQty(0);
			setReturnCharges(0);
			setItemConditionId(ReturnCondition.GOOD.getId());
			setReturnRemarks("NO CHARGES TO CLIENT");
			setReturnStatus(ReturnStatus.NEW.getName());
			rentedItem.setStatus(ReturnStatus.NEW.getId());
			rentedItem.setCustomer(trans.getCustomer());
			
		}else{
			
			setReturnDate(DateUtils.getCurrentDateYYYYMMDD());
			setReturnProductName(rentedItem.getProductProperties().getProductname());;
			setReturnUom(rentedItem.getUom().getSymbol());
			setReturnRemainingQty(trans.getCurrentQty());
			setReturnQty(rentedItem.getQuantity());
			setReturnCharges(rentedItem.getCharges());
			setItemConditionId(rentedItem.getItemCondition());
			setReturnRemarks(rentedItem.getRemarks());
			setReturnStatus(ReturnStatus.typeName(rentedItem.getStatus()));
			
		}
		setReturnBottle(trans);
		setReturnData(rentedItem);
	}
	
	
	
	public void saveReturn(){
		if(Login.checkUserStatus()){
			
			if(getReturnData()!=null){
				
				ReturnRentedItems rentedItem = getReturnData();
				
				boolean isOk = true;
				
				if(rentedItem.getStatus()==ReturnStatus.POSTED.getId()){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Editing posted transaction is not allowed.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				
				if(getReturnQty()==0 && isOk){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide return quantiy.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				if(getReturnQty()<0 && isOk){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide positve number.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				if(getReturnQty()>getReturnRemainingQty() && isOk){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide quantity below or equal to the remaining quantity for return.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				
				if(getItemConditionId()==0){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Please select condition.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				
				if(isOk){
					
					rentedItem.setQuantity(getReturnQty());
					rentedItem.setItemCondition(getItemConditionId());
					rentedItem.setCharges(getReturnCharges());
					if(getReturnCharges()>0){
						rentedItem.setRemarks("ADDITIONAL CHARGES ADDED TO CLIENT");
					}else{
						rentedItem.setRemarks("NO ADDITIONAL CHARGES ADDED TO CLIENT");
					}
					
					rentedItem.setUserDtls(Login.getUserLogin().getUserDtls());
					rentedItem.setIsActive(1);
					rentedItem = rentedItem.save(rentedItem);
					setReturnData(rentedItem);
					startReturn();
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully saved.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong. Please logout and login again.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void postedReturn(){
		if(Login.checkUserStatus()){
			
			if(getReturnData()!=null){
				
				ReturnRentedItems rentedItem = getReturnData();
				
				boolean isOk = true;
				if(rentedItem.getStatus()==ReturnStatus.POSTED.getId()){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Reposting transaction is not allowed.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				
				if(getReturnQty()==0 && isOk){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide return quantiy.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				if(getReturnQty()<0 && isOk){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide positve number.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				if(getReturnQty()>getReturnRemainingQty() && isOk){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please provide quantity below or equal to the remaining quantity for return.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				
				if(getItemConditionId()==0){
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: Please select condition.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
		            isOk = false;
				}
				
				
				if(isOk){
					
					
					
					rentedItem.setQuantity(getReturnQty());
					rentedItem.setItemCondition(getItemConditionId());
					rentedItem.setCharges(getReturnCharges());
					if(getReturnCharges()>0){
						rentedItem.setRemarks("ADDITIONAL CHARGES ADDED TO CLIENT");
					}else{
						rentedItem.setRemarks("NO ADDITIONAL CHARGES ADDED TO CLIENT");
					}
					rentedItem.setUserDtls(Login.getUserLogin().getUserDtls());
					rentedItem.setIsActive(1);
					rentedItem.setStatus(ReturnStatus.POSTED.getId());
					setReturnStatus(ReturnStatus.POSTED.getName());
					rentedItem = rentedItem.save(rentedItem);
					setReturnData(rentedItem);
					
					//subtract quantity in table ReturnBottle
					returningItem(getReturnBottle(), getReturnQty());
					
					//add client balances
					if(getReturnCharges()>0){
						//customerpaybal(rentedItem.getCustomer(), getReturnCharges(), 0, PaymentTransactionType.RETURN, "Return items charges");
						Payment.customerPayment(rentedItem.getCustomer(), getReturnCharges(), 0, PaymentTransactionType.RETURN, "Return items charges", "");
					}
					startReturn();
					FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully saved.", "");
		            FacesContext.getCurrentInstance().addMessage(null, msg);
				}
				
			}else{
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Something went wrong. Please logout and login again.", "");
	            FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your login session has been expired. Please login again.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void clearReturn(){
		setReturnBottle(null);
		setReturnData(null);
		setReturnDate(null);
		setReturnProductName(null);
		setReturnUom(null);
		setReturnRemainingQty(0);
		setReturnQty(0);
		setReturnCharges(0);
		setItemConditionId(0);
		setReturnRemarks(null);
		setReturnStatus(null);
	}
	
	public void returningItem(RentedBottle trans, double returnQty){
		double oldQty = trans.getCurrentQty();
		double newQty = 0d;
		newQty = oldQty - returnQty;
		trans.setCurrentQty(newQty);
		trans.setPrevQty(oldQty);
		trans.save();
	}
	
	/**
	 * 
	 * This method has been moved to Payment.java
	 */
	@Deprecated
	private void customerpaybal(Customer customer, double balance, double paidAmount, PaymentTransactionType type, String remarks){
		Customer cus = new Customer();
		cus.setCustomerid(customer.getCustomerid());
		cus.setIsactive(1);
		CustomerPayment py = new CustomerPayment();
		py.setPayisactive(1);
		List<CustomerPayment> payments = CustomerPayment.retrieve(py,cus);
		CustomerPayment payment = new CustomerPayment();
		
		//double bal = Double.valueOf(getBalanceamnt()+"");
		BigDecimal baltmp = new BigDecimal(Math.abs(balance));
		
		/**
		 * check if there are history for this customer
		 */
		if(payments.size()>0){
			payment = payments.get(0);
		CustomerPaymentTrans trans = new CustomerPaymentTrans();
		trans.setPaymentdate(DateUtils.getCurrentDateYYYYMMDD());
		trans.setAmountpay(new BigDecimal(paidAmount+""));
		trans.setIspaid(1);
		trans.setPaytransisactive(1);
		trans.setCustomerPayment(payment);
		trans.setUserDtls(Login.getUserLogin().getUserDtls());
		trans.setPaymentType(type.getId());
		trans.setRemarks(remarks);
		trans.save();
		
		//set temp data
		BigDecimal balamnt = payment.getAmountbalance();
		BigDecimal amntpaidorig = payment.getAmountpaid();
		String paiddate = payment.getAmountpaiddate();
		
		
		//BigDecimal addBalamnt =  balamnt.add(baltmp);
		BigDecimal newBalamnt =  balamnt.add(baltmp);  //addBalamnt.subtract(new BigDecimal(Currency.removeComma(getAmountin())));
		
		//Update customerpayment table
		payment.setAmountpaid(new BigDecimal(paidAmount+""));
		payment.setAmountpaiddate(DateUtils.getCurrentDateYYYYMMDD());
		
		payment.setAmountprevpaid(amntpaidorig);
		payment.setAmountprevpaiddate(paiddate);
		
		payment.setAmountbalance(newBalamnt);
		payment.setAmountprevbalance(balamnt);
		payment.save();
		
		}else{ 
		
			/**
			 * Customer's first data 
			 */
			
			payment = new CustomerPayment();
			payment.setAmountpaid(new BigDecimal(paidAmount+""));
			payment.setAmountpaiddate(DateUtils.getCurrentDateYYYYMMDD());
			payment.setAmountprevpaid(new BigDecimal("0"));
			payment.setAmountprevpaiddate(DateUtils.getCurrentDateYYYYMMDD());
			payment.setAmountbalance(baltmp);
			payment.setAmountprevbalance(new BigDecimal("0"));
			payment.setCustomer(customer);
			payment.setPayisactive(1);
			payment.setUserDtls(Login.getUserLogin().getUserDtls());
			payment = CustomerPayment.save(payment);
			
			
			CustomerPaymentTrans trans = new CustomerPaymentTrans();
			trans.setPaymentdate(DateUtils.getCurrentDateYYYYMMDD());
			trans.setAmountpay(new BigDecimal(paidAmount+""));
			trans.setIspaid(1);
			trans.setPaytransisactive(1);
			trans.setCustomerPayment(payment);
			trans.setUserDtls(Login.getUserLogin().getUserDtls());
			trans.setPaymentType(type.getId());
			trans.setRemarks(remarks);
			trans.save();
			
		}
		
	}
	
	
	public List<CustomerPayment> getCustomers() {
		return customers;
	}
	public void setCustomers(List<CustomerPayment> customers) {
		this.customers = customers;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	public String getSearchClients() {
		return searchClients;
	}

	public void setSearchClients(String searchClients) {
		this.searchClients = searchClients;
	}

	public CustomerPayment getCustomerPayment() {
		return customerPayment;
	}

	public void setCustomerPayment(CustomerPayment customerPayment) {
		this.customerPayment = customerPayment;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getInputamount() {
		return inputamount;
	}

	public void setInputamount(String inputamount) {
		this.inputamount = inputamount;
	}

	public String getInputamounttmp() {
		return inputamounttmp;
	}

	public void setInputamounttmp(String inputamounttmp) {
		this.inputamounttmp = inputamounttmp;
	}

	public List<CustomerPayment> getCustomerPays() {
		return customerPays;
	}

	public void setCustomerPays(List<CustomerPayment> customerPays) {
		this.customerPays = customerPays;
	}

	public String getHisDateFrom() {
		return hisDateFrom;
	}

	public void setHisDateFrom(String hisDateFrom) {
		this.hisDateFrom = hisDateFrom;
	}

	public String getHisDateTo() {
		return hisDateTo;
	}

	public void setHisDateTo(String hisDateTo) {
		this.hisDateTo = hisDateTo;
	}

	public String getHisName() {
		return hisName;
	}

	public void setHisName(String hisName) {
		this.hisName = hisName;
	}

	public List<CustomerPaymentTrans> getHistorys() {
		return historys;
	}

	public void setHistorys(List<CustomerPaymentTrans> historys) {
		this.historys = historys;
	}

	public String getHisTotal() {
		return hisTotal;
	}

	public void setHisTotal(String hisTotal) {
		this.hisTotal = hisTotal;
	}

	public CustomerPayment getCustomerPaymentData() {
		return customerPaymentData;
	}

	public void setCustomerPaymentData(CustomerPayment customerPaymentData) {
		this.customerPaymentData = customerPaymentData;
	}

	public List<Customer> getRents() {
		return rents;
	}

	public void setRents(List<Customer> rents) {
		this.rents = rents;
	}

	public String getSearchClientsRented() {
		return searchClientsRented;
	}

	public void setSearchClientsRented(String searchClientsRented) {
		this.searchClientsRented = searchClientsRented;
	}

	public List<Customer> getRentHistory() {
		return rentHistory;
	}

	public void setRentHistory(List<Customer> rentHistory) {
		this.rentHistory = rentHistory;
	}

	public String getSearchClientsRentedHistory() {
		return searchClientsRentedHistory;
	}

	public void setSearchClientsRentedHistory(String searchClientsRentedHistory) {
		this.searchClientsRentedHistory = searchClientsRentedHistory;
	}

	public List<Customer> getReturns() {
		return returns;
	}

	public void setReturns(List<Customer> returns) {
		this.returns = returns;
	}

	public String getSearchClientsReturn() {
		return searchClientsReturn;
	}

	public void setSearchClientsReturn(String searchClientsReturn) {
		this.searchClientsReturn = searchClientsReturn;
	}

	public List<ReturnRentedItems> getReturnHistory() {
		return returnHistory;
	}

	public void setReturnHistory(List<ReturnRentedItems> returnHistory) {
		this.returnHistory = returnHistory;
	}

	public String getSearchClientsReturnHistory() {
		return searchClientsReturnHistory;
	}

	public void setSearchClientsReturnHistory(String searchClientsReturnHistory) {
		this.searchClientsReturnHistory = searchClientsReturnHistory;
	}

	public String getSearchBought() {
		return searchBought;
	}

	public void setSearchBought(String searchBought) {
		this.searchBought = searchBought;
	}

	public Date getReceiptDateFrom() {
		if(receiptDateFrom==null){
			receiptDateFrom = DateUtils.getDateToday();
		}
		return receiptDateFrom;
	}

	public void setReceiptDateFrom(Date receiptDateFrom) {
		this.receiptDateFrom = receiptDateFrom;
	}

	public Date getReceiptDateTo() {
		if(receiptDateTo==null){
			receiptDateTo = DateUtils.getDateToday();
		}
		return receiptDateTo;
	}

	public void setReceiptDateTo(Date receiptDateTo) {
		this.receiptDateTo = receiptDateTo;
	}

	public List<Customer> getCustomerReceipts() {
		return customerReceipts;
	}

	public void setCustomerReceipts(List<Customer> customerReceipts) {
		this.customerReceipts = customerReceipts;
	}

	public List<DeliveryItemTrans> getItemSolds() {
		return itemSolds;
	}

	public void setItemSolds(List<DeliveryItemTrans> itemSolds) {
		this.itemSolds = itemSolds;
	}

	public String getReturnDate() {
		if(returnDate==null){
			returnDate = DateUtils.getCurrentDateYYYYMMDD();
		}
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	public String getReturnProductName() {
		return returnProductName;
	}

	public void setReturnProductName(String returnProductName) {
		this.returnProductName = returnProductName;
	}

	public String getReturnUom() {
		return returnUom;
	}

	public void setReturnUom(String returnUom) {
		this.returnUom = returnUom;
	}

	public double getReturnRemainingQty() {
		return returnRemainingQty;
	}

	public void setReturnRemainingQty(double returnRemainingQty) {
		this.returnRemainingQty = returnRemainingQty;
	}

	public double getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(double returnQty) {
		this.returnQty = returnQty;
	}

	public double getReturnCharges() {
		return returnCharges;
	}

	public void setReturnCharges(double returnCharges) {
		this.returnCharges = returnCharges;
	}

	public int getItemConditionId() {
		if(itemConditionId==0){
			itemConditionId = 1;
		}
		return itemConditionId;
	}

	public void setItemConditionId(int itemConditionId) {
		this.itemConditionId = itemConditionId;
	}

	public List getItemCondition() {
		itemCondition = new ArrayList<>();
		itemCondition.add(new SelectItem(1, "GOOD CONDITION"));
		itemCondition.add(new SelectItem(2, "DAMAGED ITEM"));
		return itemCondition;
	}

	public void setItemCondition(List itemCondition) {
		this.itemCondition = itemCondition;
	}

	public String getReturnRemarks() {
		return returnRemarks;
	}

	public void setReturnRemarks(String returnRemarks) {
		this.returnRemarks = returnRemarks;
	}

	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	public ReturnRentedItems getReturnData() {
		return returnData;
	}

	public void setReturnData(ReturnRentedItems returnData) {
		this.returnData = returnData;
	}

	public RentedBottle getReturnBottle() {
		return returnBottle;
	}

	public void setReturnBottle(RentedBottle returnBottle) {
		this.returnBottle = returnBottle;
	}

	public Date getReturnHistoryDateFrom() {
		if(returnHistoryDateFrom==null){
			returnHistoryDateFrom = DateUtils.getDateToday();
		}
		return returnHistoryDateFrom;
	}

	public void setReturnHistoryDateFrom(Date returnHistoryDateFrom) {
		this.returnHistoryDateFrom = returnHistoryDateFrom;
	}

	public Date getReturnHistoryDateTo() {
		if(returnHistoryDateTo==null){
			returnHistoryDateTo = DateUtils.getDateToday();
		}
		return returnHistoryDateTo;
	}

	public void setReturnHistoryDateTo(Date returnHistoryDateTo) {
		this.returnHistoryDateTo = returnHistoryDateTo;
	}

	public List<Transactions> getTransReceipt() {
		return transReceipt;
	}

	public void setTransReceipt(List<Transactions> transReceipt) {
		this.transReceipt = transReceipt;
	}

	public Transactions getTransSelected() {
		return transSelected;
	}

	public void setTransSelected(Transactions transSelected) {
		this.transSelected = transSelected;
	}

	public String getTransReceiptNo() {
		return transReceiptNo;
	}

	public void setTransReceiptNo(String transReceiptNo) {
		this.transReceiptNo = transReceiptNo;
	}

	public int getReceiptStatusId() {
		if(receiptStatusId==0) {
			receiptStatusId = 1;
		}
		return receiptStatusId;
	}

	public void setReceiptStatusId(int receiptStatusId) {
		this.receiptStatusId = receiptStatusId;
	}

	public List getReceiptStatus() {
		receiptStatus = new ArrayList<>();
		
		receiptStatus.add(new SelectItem(HistoryReceiptStatus.UNPAID.getId(), HistoryReceiptStatus.UNPAID.getName()));
		receiptStatus.add(new SelectItem(HistoryReceiptStatus.PARTIALPAID.getId(), HistoryReceiptStatus.PARTIALPAID.getName()));
		receiptStatus.add(new SelectItem(HistoryReceiptStatus.FULLPAID.getId(), HistoryReceiptStatus.FULLPAID.getName()));
		
		return receiptStatus;
	}

	public void setReceiptStatus(List receiptStatus) {
		this.receiptStatus = receiptStatus;
	}

	public String getTransReceiptAmount() {
		return transReceiptAmount;
	}

	public void setTransReceiptAmount(String transReceiptAmount) {
		this.transReceiptAmount = transReceiptAmount;
	}
	
	
}
