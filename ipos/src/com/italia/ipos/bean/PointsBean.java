package com.italia.ipos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.italia.ipos.application.Application;
import com.italia.ipos.controller.Customer;
import com.italia.ipos.controller.CustomerPoints;
import com.italia.ipos.controller.PointsHistory;

@ManagedBean(name="pointBean", eager=true)
@ViewScoped
public class PointsBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 979769807861L;
	
	private List<CustomerPoints> points = Collections.synchronizedList(new ArrayList<CustomerPoints>());
	private List<CustomerPoints> selectedPoints;
	private CustomerPoints pointsData;
	
	private String searchName;
	
	private Customer customerData;
	
	private List<PointsHistory> poHis = Collections.synchronizedList(new ArrayList<PointsHistory>());
	
	@PostConstruct
	public void init() {
		
		String sql = " AND cus.cusisactive=1 ";
		String[] params = new String[0];
		
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
			sql += " AND ( cus.fullname like '%"+ getSearchName().replace("--", "") +"%' OR cus.cuscardno like '%"+ getSearchName().replace("--", "") +"%')";
		}else {
			sql += " LIMIT 10";
		}
		
		List<Customer> customers = Customer.retrieve(sql, params);
		
		
		if(customers!=null && customers.size()>0) {
			loadPoints(customers);
		}
		
	}
	
	public void loadPoints(List<Customer> customers) {
		points = Collections.synchronizedList(new ArrayList<CustomerPoints>());
		String sql = " AND pts.isactivepo=1 ";
		String[] params = new String[0];
		for(Customer cus : customers) {
				sql = " AND pts.isactivepo=1 ";
				sql += "  AND cuz.customerid=" + cus.getCustomerid()+"";
				List<CustomerPoints> cpts = CustomerPoints.retrieve(sql, params);
				
				CustomerPoints po = new CustomerPoints();
				if(cpts==null || cpts.size()==0) {
					po.setIsActive(1);
					po.setCurrentPoints(0);
					po.setLatestAddedPoints(0);
					po.setLatestDeductedPoints(0);
					po.setCustomer(cus);
					points.add(po);
				}else if(cpts.size()==1) {
					points.add(cpts.get(0));
					clickCustomerPoints(cpts.get(0));
				}
		}
		
	}
	
	public void clickCustomerPoints(CustomerPoints pt) {
		poHis = Collections.synchronizedList(new ArrayList<PointsHistory>());
			String sql = " AND cuz.customerid=? AND pts.isactivepoh=1 ORDER BY pts.pohid DESC";
			String[] params = new String[1];
			params[0] = pt.getCustomer().getCustomerid()+"";
			poHis = PointsHistory.retrieve(sql, params);
	}
	
	public void activatePoints(CustomerPoints pt) {
		boolean isactivate = pt.isChecked();
		
		if(isactivate) {
			pt.setIsActivated(1);
			pt.save();	
			init();
			Application.addMessage(1, "Success", "Successfully activated");
		}else {
			Application.addMessage(1, "Success", "Successfully deactivated");
		}
	}
	
	public List<CustomerPoints> getPoints() {
		return points;
	}
	public void setPoints(List<CustomerPoints> points) {
		this.points = points;
	}
	public List<CustomerPoints> getSelectedPoints() {
		return selectedPoints;
	}
	public void setSelectedPoints(List<CustomerPoints> selectedPoints) {
		this.selectedPoints = selectedPoints;
	}

	public Customer getCustomerData() {
		return customerData;
	}

	public void setCustomerData(Customer customerData) {
		this.customerData = customerData;
	}

	public List<PointsHistory> getPoHis() {
		return poHis;
	}

	public void setPoHis(List<PointsHistory> poHis) {
		this.poHis = poHis;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public CustomerPoints getPointsData() {
		return pointsData;
	}

	public void setPointsData(CustomerPoints pointsData) {
		this.pointsData = pointsData;
	}

}
