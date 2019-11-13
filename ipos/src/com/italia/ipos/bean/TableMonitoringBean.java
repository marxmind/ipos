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

import org.primefaces.event.DragDropEvent;

import com.italia.ipos.controller.Login;
import com.italia.ipos.controller.TableMonitoring;
import com.italia.ipos.enm.TableStatus;
import com.italia.ipos.utils.DateUtils;

/**
 * 
 * @author mark italia
 * @since 05/28/2017
 * @version 1.0
 *
 */

@ManagedBean(name="tableBean", eager=true)
@ViewScoped
public class TableMonitoringBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 132687543586756L;

	private List<TableMonitoring> tables = Collections.synchronizedList(new ArrayList<TableMonitoring>());
	private List<TableMonitoring> tableOccupieds = Collections.synchronizedList(new ArrayList<TableMonitoring>());
	private TableMonitoring selectedTable;
	
	private List<TableMonitoring> maintainTables = Collections.synchronizedList(new ArrayList<TableMonitoring>());
	private String tableNumber;
	private String tableName;
	
	@PostConstruct
	public void init(){
		tables = Collections.synchronizedList(new ArrayList<TableMonitoring>());
		tableOccupieds = Collections.synchronizedList(new ArrayList<TableMonitoring>());
		
		
		/**
		 *  status = 1 = available
		 *  status = 2 = occupied
		 */
		
		TableMonitoring mon = new TableMonitoring();
		mon.setIsActive(1);
		
		mon.setStatus(TableStatus.AVAILABLE.getId());
		tables = TableMonitoring.retrieve(mon);
		
		mon.setStatus(TableStatus.OCCUPIED.getId());
		tableOccupieds = TableMonitoring.retrieve(mon);
		
	}
	
	public void initMaintenance(){
		maintainTables = Collections.synchronizedList(new ArrayList<TableMonitoring>());
		TableMonitoring mon = new TableMonitoring();
		mon.setIsActive(1);
		maintainTables = TableMonitoring.retrieve(mon);
	}
	
	public void saveTable(){
		if(Login.checkUserStatus()){
			TableMonitoring mon = new TableMonitoring();
			if(getSelectedTable()!=null){
				mon = getSelectedTable();
			}else{
				mon.setStatus(TableStatus.AVAILABLE.getId());
			}
			
			mon.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
			mon.setIsActive(1);
			mon.setTableNumber(getTableNumber());
			mon.setTableName(getTableName());
			mon.save();
			clearFields();
			initMaintenance();
			addMessage("Data has been successfully saved.","");
		}
	}
	
	public void clearFields(){
		setSelectedTable(null);
		setTableNumber(null);
		setTableName(null);
	}
	
	public void clickChange(TableMonitoring mon){
		setTableNumber(mon.getTableNumber());
		setTableName(mon.getTableName());
		setSelectedTable(mon);
	}
	
	public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	public void onTableDrop(DragDropEvent ddEvent) {
        TableMonitoring tbl = ((TableMonitoring) ddEvent.getData());
  
        //tableOccupieds.add(tbl);
       // tables.remove(tbl);
        
        tbl.setStatus(TableStatus.OCCUPIED.getId());
        tbl.save();
        init();
        
    }
	
	public void onTableReturnDrop(DragDropEvent ddEvent) {
        TableMonitoring tbl = ((TableMonitoring) ddEvent.getData());
  
        //tableOccupieds.add(tbl);
       // tables.remove(tbl);
        
        tbl.setStatus(TableStatus.AVAILABLE.getId());
        tbl.save();
        init();
        
    }
	
	public List<TableMonitoring> getTables() {
		return tables;
	}
	public void setTables(List<TableMonitoring> tables) {
		this.tables = tables;
	}
	public List<TableMonitoring> getTableOccupieds() {
		return tableOccupieds;
	}
	public void setTableOccupieds(List<TableMonitoring> tableOccupieds) {
		this.tableOccupieds = tableOccupieds;
	}

	public TableMonitoring getSelectedTable() {
		return selectedTable;
	}

	public void setSelectedTable(TableMonitoring selectedTable) {
		this.selectedTable = selectedTable;
	}

	public List<TableMonitoring> getMaintainTables() {
		return maintainTables;
	}

	public void setMaintainTables(List<TableMonitoring> maintainTables) {
		this.maintainTables = maintainTables;
	}

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}
