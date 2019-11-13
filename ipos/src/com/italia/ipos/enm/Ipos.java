package com.italia.ipos.enm;

import java.io.File;
/**
 * 
 * @author mark italia
 * @since created 09/27/2016
 * @version 1.0
 *
 */
public enum Ipos {

	DB_NAME("databaseName"),
	DB_DRIVER("driver"),
	DB_URL("url"),
	DB_PORT("port"),
	DB_SSL("SSL"),
	USER_NAME("username"),
	USER_PASS("password"),
	APP_EXP("applicationExp"),
	APP_VER("applicationVersion"),
	APP_COPYRIGHT("copyright"),
	APP_OWNER("author"),
	APP_EMAIL("supportEamil"),
	APP_PHONE("supportNo"),
	APP_LOG_PATH("logPath"),
	APP_LOG("includeLog"),
	APP_FOLDER("ipos"),
	APP_CONFIG_FLDR("conf"),
	APP_CONFIG_FILE("application.xml"),
	APP_LICENSE_FILE_NAME("license.xml"),
	APP_IMG_FILE("imgPath"),
	APP_BUSINESS_TYPE("businessType"),
	APP_RESOURCES_LOC("resources"),
	APP_RESOURCES_LOC_IMG("img"),
	SECURITY_ENCRYPTION_FORMAT("utf-8"),
	PRIMARY_DRIVE(System.getenv("SystemDrive")),
	SEPERATOR(File.separator),
	REPORT_CONFIG_FILENAME("reports.xml"),
	REPORT("reports"),
	BUSINESS_NAME("businessName"),
	BUSINESS_WALLPAPER_FILE("businessWallpaperFile"),
	BUSSINES_WALLPAPER_IMG("gif"),
	THEME_STYLE("themeStyle"),
	BUSINESS("business.xml"),
	RECEIPT_INFO("receiptsinfo.xml"),
	RESERVATION_FILE_XML("reservation.xml"),
	RECEIPTS_LOG("receiptLog"),
	CASHIER_SELECTED_ITEM_SAVE("selectedItemSave"),
	DISPENSE_PRODUCT_FOLDER("dispense"),
	OUTSIDE_SAVING_ENABLE("enableoutsidesaving"),
	EXECUTABLE_JAR_CASHIER("cashierjar"),
	PRINTER_NAME("printername"),
	PRINTER_ISON("printerison"),
	MOBILE_THEME("mobileTheme");
	private String name;
	
	
	private Ipos(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
