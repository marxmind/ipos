package com.italia.ipos.controller;

import java.util.List;

import com.italia.ipos.utils.DateUtils;
import com.italia.ipos.utils.Numbers;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 10/01/2018
 *
 */
public class PointsRule {
	
	private static final int PER_POINTS = 200;
	private static final int REDEEM_POINTS = 1;
	
	public static double pointsAvail(double amount) {
		
		double points = amount / PER_POINTS;
		points = Numbers.formatDouble(points);
		
		return points;
	}
	
	public static double redeemValue(double points) {
		
		double amount = points / REDEEM_POINTS;
		amount = Numbers.formatDouble(amount);
		
		return amount;
	}
	
	/**
	 * 
	 * @param customer
	 * @param receiptNo
	 * @param amount
	 * @param type= 1 = added 0=deducted
	 */
	public static void addPoints(Customer customer, String receiptNo, double amount,int type) {
		if(customer.getCustomerid()!=1) {
			
			String[] params = new String[0];
			String sql = " AND pts.isactivepo=1 AND pts.isactivated=1 AND cuz.customerid=" + customer.getCustomerid()+"";
					List<CustomerPoints> cpts = CustomerPoints.retrieve(sql, params);
					
					double points = PointsRule.pointsAvail(amount);
					
					PointsHistory his = new PointsHistory();
					his.setCustomer(customer);
					his.setReceiptNo(receiptNo);
					his.setIsActive(1);
					his.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
					his.setPoints(points);
					his.setType(type);
					
					if(cpts!=null && cpts.size()>0) {//points is activated-- record points
							
							his.save();//save points history
							
							CustomerPoints pt = cpts.get(0);
							double currentPoints = 0;
							
							if(type==0) {
								pt.setLatestDeductedPoints(points);
								currentPoints = pt.getCurrentPoints() - points;
							}else if(type==1) {
								pt.setLatestAddedPoints(points);
								currentPoints = pt.getCurrentPoints() + points;
							}
							pt.setCurrentPoints(currentPoints);
							pt.save();//save points transactions
					}
					
			
		}
	}
	
	/**
	 * 
	 * @param customer
	 * @param receiptNo
	 * @param points
	 */
	public static void redeemPoints(Customer customer, String receiptNo, double points) {
		if(customer.getCustomerid()!=1) {
			
			String[] params = new String[0];
			String sql = " AND pts.isactivepo=1 AND pts.isactivated=1 AND cuz.customerid=" + customer.getCustomerid()+"";
					List<CustomerPoints> cpts = CustomerPoints.retrieve(sql, params);
					
					//double points = PointsRule.pointsAvail(amount);
					
					PointsHistory his = new PointsHistory();
					his.setCustomer(customer);
					his.setReceiptNo(receiptNo);
					his.setIsActive(1);
					his.setDateTrans(DateUtils.getCurrentDateYYYYMMDD());
					his.setPoints(points);
					his.setType(0);
					
					if(cpts!=null && cpts.size()>0) {//points is activated-- record points
							
							his.save();//save points history
							
							CustomerPoints pt = cpts.get(0);
							double currentPoints = 0;
							
							pt.setLatestDeductedPoints(points);
							currentPoints = pt.getCurrentPoints() - points;
							if(currentPoints<0) {
								currentPoints=0;
							}
							pt.setCurrentPoints(currentPoints);
							pt.save();//save points transactions
					}
					
			
		}
	}
	
}
