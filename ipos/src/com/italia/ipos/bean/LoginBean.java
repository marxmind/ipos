package com.italia.ipos.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import com.italia.ipos.application.ClientInfo;
import com.italia.ipos.controller.Business;
import com.italia.ipos.controller.Login;
import com.italia.ipos.enm.Ipos;
import com.italia.ipos.enm.UserAccess;
import com.italia.ipos.reader.ReadConfig;
import com.italia.ipos.security.Copyright;
import com.italia.ipos.security.License;
import com.italia.ipos.security.Module;
import com.italia.ipos.session.IBean;
import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.LogU;
import com.italia.ipos.utils.Whitelist;

/**
 * 
 * @author mark italia
 * @since 09/27/2016
 *
 */

@ManagedBean(name="loginBean", eager=true)
@SessionScoped
public class LoginBean implements Serializable{

	private static final long serialVersionUID = 1094801825228386363L;
	
	private String name;
	private String password;
	private String errorMessage;
	private Login login;
	private String keyPress;
	private int businessId;
	private List business;
	private Map<Integer, Business> businessData = Collections.synchronizedMap(new HashMap<Integer, Business>());
	
	private List themes;
	private String idThemes="luna-blue";
	
	public String getCurrentDate(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		Date date_ = new Date();
		String _date = dateFormat.format(date_);
		return _date;
	}
	
	
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@PostConstruct
	public void init(){
		//invalidate session
		IBean.removeBean();
		
		//load business wallpaper
		String wallpaper = ReadConfig.value(Ipos.BUSINESS_WALLPAPER_FILE);
		//System.out.println("wallpaper >>> " + wallpaper);
		copyProductImg(wallpaper);
	}
	
	public void copyProductImg(String wallpaper){
		String pathToSave = FacesContext.getCurrentInstance()
                .getExternalContext().getRealPath(Ipos.SEPERATOR.getName()) + Ipos.SEPERATOR.getName() +
                						Ipos.APP_RESOURCES_LOC.getName() + Ipos.SEPERATOR.getName() + 
                						Ipos.BUSSINES_WALLPAPER_IMG.getName() + Ipos.SEPERATOR.getName();
		//System.out.println("pathToSave " + pathToSave + wallpaper);
		File logdirectory = new File(pathToSave);
		if(logdirectory.isDirectory()){
			//System.out.println("is directory");
		}
		
		
		String productFile = ReadConfig.value(Ipos.APP_IMG_FILE) + wallpaper;
		File file = new File(productFile);
		//if(!file.exists()){
			//System.out.println("copying file.... " + file.getName());
			try{
			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
			        StandardCopyOption.REPLACE_EXISTING);
			}catch(IOException e){}
			
		//}
	}
	
	//validate login
	public String validateUserNamePassword(){
		
		//if(getBusinessId()==0){
		//set database on on business type
		changeDatabaseConnection();
		//}
		
		
		String sql = "SELECT * FROM login WHERE username=? and password=?";
		String[] params = new String[2];
		         params[0] = Whitelist.remove(name);
		         params[1] = Whitelist.remove(password);
		Login in = null;
		try{in = Login.retrieve(sql, params).get(0);}catch(Exception e){}
		
		/*boolean valid = Login.validate(sql, params);
		System.out.println("Valid: " + valid);*/
		
		String result="login";
		LogU.add("Guest with username : " + name + " and password " + password + " is trying to log in the system.");
		if(in!=null){
			
	        HttpSession session = SessionBean.getSession();
	        session.setAttribute("username", name);
			session.setAttribute("userid", in.getLogid());
			session.setAttribute("theme",getIdThemes());
			boolean isExpired = License.checkLicenseExpiration(Module.DELIVERY);
			
	        /*if(in.getAccessLevel().getLevel()==1 || in.getAccessLevel().getLevel()==2){
	        	result = "main";
	        }else{
	        	result = "pos";
	        }*/
			/*if(UserAccess.DEVELOPER.getId() == in.getAccessLevel().getLevel() || 
					UserAccess.OWNER.getId() == in.getAccessLevel().getLevel() || 
					UserAccess.MANAGER.getId() == in.getAccessLevel().getLevel()){
				result = "main";
			}else{
				result = "cashier";
			}*/
			result = "cashier";
			LogU.add("The user has been successfully login to the application with the username : " + name + " and password " + password);
			
			if(isExpired){
				LogU.add("The application is expired. Please contact application owner.");
				result = "expired";
			}else{
				logUserIn(in);
			}
			
			
		}else{
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(
							FacesMessage.SEVERITY_WARN, 
							"Incorrect username and password", 
							"Please enter correct username and password"
							)
					);
//			/setErrorMessage("Incorrect username and password.");
			LogU.add("The user was not successfully login to the application with the username : " + name + " and password " + password);
			setName("");
			setPassword("");
			result= "login";
		}
		System.out.println(getErrorMessage());
		return result;
	}
	
	private void logUserIn(Login in){
		if(in==null) in = new Login();
		ClientInfo cinfo = new ClientInfo();
		in.setLogintime(DateUtils.getCurrentDateMMDDYYYYTIME());
		in.setIsOnline(1);
		in.setClientip(cinfo.getClientIP());
		in.setClientbrowser(cinfo.getBrowserName());
		in.save();
	}
	
	private void logUserOut(){
		String sql = "SELECT * FROM login WHERE username=? and logid=?";
		HttpSession session = SessionBean.getSession();
		String userid = session.getAttribute("userid").toString();
		String username = session.getAttribute("username").toString();
		String[] params = new String[2];
    	params[0] = username;
    	params[1] = userid;
    	Login in = null;
    	try{in = Login.retrieve(sql, params).get(0);}catch(Exception e){}
		ClientInfo cinfo = new ClientInfo();
		if(in!=null){
			in.setLastlogin(DateUtils.getCurrentDateMMDDYYYYTIME());
			in.setIsOnline(0);
			in.setClientip(cinfo.getClientIP());
			in.setClientbrowser(cinfo.getBrowserName());
			in.save();
		}
		LogU.add("The user " + username + " was logging out to the application.");
		
		//Remove registered bean in session
		IBean.removeBean();
		
	}
	
	//logout event, invalidate session
	public String logout(){
		logUserOut();
		setName("");
		setPassword("");
		return "login";
	}


	public void changeDatabaseConnection(){
		System.out.println("changing database....");
		
		int size = 6;
		Ipos[] tag = new Ipos[size];
		String[] value = new String[size];
		int i=0;
		tag[i] = Ipos.DB_NAME; value[i] = getBusinessData().get(getBusinessId()).getDatabase(); i++;
		tag[i] = Ipos.BUSINESS_NAME; value[i] = getBusinessData().get(getBusinessId()).getName(); i++;
		tag[i] = Ipos.DB_DRIVER; value[i] = getBusinessData().get(getBusinessId()).getDriver(); i++;
		tag[i] = Ipos.DB_URL; value[i] = getBusinessData().get(getBusinessId()).getUrl(); i++;
		tag[i] = Ipos.DB_PORT; value[i] = getBusinessData().get(getBusinessId()).getPort(); i++;
		tag[i] = Ipos.DB_SSL; value[i] = getBusinessData().get(getBusinessId()).getSsl(); i++;
		
		Business.updateBusiness(tag, value);
		
		System.out.println("Database has been changed....");
	}

	public Login getLogin() {
		return login;
	}



	public void setLogin(Login login) {
		this.login = login;
	}



	public String getKeyPress() {
		/*if((getName()!=null && !getName().isEmpty()) && (getPassword()!=null && !getPassword().isEmpty())){
			keyPress = "logId";
		}else{
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Please provide information to proceed.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
		}*/
		keyPress = "logId";
		return keyPress;
	}



	public void setKeyPress(String keyPress) {
		this.keyPress = keyPress;
	}



	public int getBusinessId() {
		return businessId;
	}



	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}



	public List getBusiness() {
		businessData = Collections.synchronizedMap(new HashMap<Integer, Business>());
		business = new ArrayList<>();
		
		for(Business bz : Business.readBusinessXML()){
			business.add(new SelectItem(bz.getId(), bz.getName()));
			businessData.put(bz.getId(), bz);
		}
		
		return business;
	}



	public void setBusiness(List business) {
		this.business = business;
	}



	public Map<Integer, Business> getBusinessData() {
		return businessData;
	}



	public void setBusinessData(Map<Integer, Business> businessData) {
		this.businessData = businessData;
	}
	
public List getThemes() {
		
		themes = new ArrayList<>();
		
		themes.add(new SelectItem("luna-amber","LUNA AMBER"));
		themes.add(new SelectItem("luna-blue","LUNA BLUE"));
		themes.add(new SelectItem("luna-green","LUNA GREEN"));
		themes.add(new SelectItem("luna-pink","LUNA PINK"));
		themes.add(new SelectItem("nova-colored","NOVA COLORED"));
		themes.add(new SelectItem("nova-dark","NOVA DARK"));
		themes.add(new SelectItem("nova-light","NOVA LIGHT"));
		themes.add(new SelectItem("omega","OMEGA"));
		
		
		return themes;
	}

	public void setThemes(List themes) {
		this.themes = themes;
	}

	public String getIdThemes() {
		return idThemes;
	}

	public void setIdThemes(String idThemes) {
		this.idThemes = idThemes;
	}
	
}

