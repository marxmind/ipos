package com.italia.ipos.application;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.UserDtls;

public class Application {

	/**
	 * 
	 * @param severityLevel 1-INFO 2-WARN 3-ERROR 4-FATAL
	 * @param summary
	 * @param detail
	 */
	public static void addMessage(int severityLevel,String summary, String detail) {
		FacesMessage message = null;
		detail = addUserName(severityLevel, detail);
		switch(severityLevel){
		case 1:
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;
		case 2:
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;
		case 3:
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;
		case 4:
			message = new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;		
		}
    }
	
	private static String addUserName(int severityLevel, String detail){
		String msg = detail;
		UserDtls user = null;
		try{
			user = Login.getUserLogin().getUserDtls();
			String name = user.getFirstname();
			
			String[] info = {
					"Great work " + name + "!",
					"You did great " + name,
					name + " it seems that you are now doing good.",
					"Keep going " + name,
					"Well done " + name,
					name + " that was awesome :)",
					name + ", it seems that you feel good"
					};
			
			String[] warn = {
					name + ", FYI.",
					"For your reference " + name
				};
			
			String[] error = {
				"Ouch! " + name,
				"Oh no " + name,
				"You have to focus " + name,
				name + ", I think you need to check your data",
				name + ", I know you are tired. Sorry for being strict",
				"Hmmm " + name
			};
			
			String[] fatal = {
				"This is bad " + name,
				"What a bad day " + name
			};
			
			String spc = ". ";
			if(detail==null || detail.isEmpty()){
				spc = " ";
			}
			
			switch(severityLevel){
			case 1:
				msg = detail + spc + info[(int)(Math.random() * info.length)];
				break;
			case 2:
				msg = detail + spc + warn[(int)(Math.random() * warn.length)];
				break;
			case 3:
				msg = detail + spc + error[(int)(Math.random() * error.length)];
				break;
			case 4:
				msg = detail + spc + fatal[(int)(Math.random() * fatal.length)];
				break;		
			}
			
			return msg;
		}catch(Exception e){}
		
		return msg;
	}
	
}
