package com.italia.ipos.bean;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartSeries;

import com.italia.ipos.controller.DeliveryItemTrans;
import com.italia.ipos.controller.Product;

@ManagedBean(name="graphlineBean", eager=true)
@ViewScoped
public class GraphLineBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5424647856813632L;

	private LineChartModel lineModel1;
    private LineChartModel lineModel2;
     
    
    @ManagedProperty("#{delprodmonBean}")
	private DeliveryProductMonitoringBean monitoring;
	public void setMonitoring(DeliveryProductMonitoringBean monitoring){
		this.monitoring = monitoring;
	}
	public DeliveryProductMonitoringBean getMonitoring(){
		return monitoring;
	}
    
    @PostConstruct
    public void init() {
        createLineModels();
    }
 
    public LineChartModel getLineModel1() {
        return lineModel1;
    }
 
    public LineChartModel getLineModel2() {
        return lineModel2;
    }
     
    private void createLineModels() {
    	Axis yAxis = null;
    	
        /*lineModel1 = initLinearModel();
        lineModel1.setTitle("Linear Chart");
        lineModel1.setLegendPosition("e");
        yAxis = lineModel1.getAxis(AxisType.Y);
        yAxis.setMin(1);
        yAxis.setMax(250);*/
         
    	String title = "Product Statistic Report";
    	String rangeTitle = "Day";
    	if(1==getMonitoring().getRangeId()){//per day
    		title = "Per Day " + title;
    		rangeTitle = "Day";
    	}else if(2==getMonitoring().getRangeId()){//per month
    		title = "Per Month " + title;
    		rangeTitle = "Month";
    	}else if(3==getMonitoring().getRangeId()){//per year
    		title = "Per Year " + title;
    		rangeTitle = "Year";
    	}
    	
        lineModel2 = initCategoryModel();
        lineModel2.setLegendPlacement(LegendPlacement.OUTSIDE);
        lineModel2.setShowPointLabels(true);
        lineModel2.setZoom(true);
        lineModel2.setTitle(title);
        lineModel2.setLegendPosition("e");
        lineModel2.setShowPointLabels(true);
        lineModel2.getAxes().put(AxisType.X, new CategoryAxis(rangeTitle));
        yAxis = lineModel2.getAxis(AxisType.Y);
        yAxis.setLabel("Quantity");
        yAxis.setMin(0);
        if(getMonitoring().getRangeId()==1){
        	yAxis.setMax(500);
        }else if(getMonitoring().getRangeId()==2){
        	yAxis.setMax(2000);
        }else if(getMonitoring().getRangeId()==3){
        	yAxis.setMax(30000);
        }
        Axis xAxis = lineModel2.getAxis(AxisType.X); 
        //xAxis.setTickFormat("Php%'d");
        xAxis.setTickFormat("%'d");
        
        //yAxis.setTickFormat("Php%'d");
        yAxis.setTickFormat("%'d");
        
    }
     
    private LineChartModel initLinearModel() {
        LineChartModel model = new LineChartModel();
        
       /* System.out.println("monitoring transaction data " + monitoring.getTransactionData().size());
        System.out.println("monitoring product data " + monitoring.getProductData().size());
        if(monitoring.getTransactionData()!=null && monitoring.getTransactionData().size()>0){
        	
        	for(Product prod : getMonitoring().getProductData().values()){
        		
        		LineChartSeries series = new LineChartSeries();
            	series.setLabel(prod.getProductProperties().getProductname());
        		
            	for(String date : monitoring.getTransactionData().keySet()){
            		DeliveryItemTrans tran = monitoring.getTransactionData().get(date).get(prod.getProdid());
            		//System.out.println("date: " + date + "  " + tran.getQuantity());
            		try{
            			int day = Integer.valueOf(date.split("-")[2]);
            			series.set(day, tran.getQuantity());
            			
            		}catch(Exception  e){}
            	}
        		model.addSeries(series);
        	}
        
        }*/
        
         
        return model;
    }
     
    private LineChartModel initCategoryModel() {
        
    	LineChartModel model = new LineChartModel();
    	
        if(monitoring.getTransactionData()!=null && monitoring.getTransactionData().size()>0){
        	Map<String, Map<Long, DeliveryItemTrans>> data = getMonitoring().getTransactionData();
        	
        	for(Product prod : getMonitoring().getProductData().values()){
        		
        		ChartSeries series = new ChartSeries();
        		series.setLabel(prod.getProductProperties().getProductname());
        		
            	for(String date : data.keySet()){
            		DeliveryItemTrans tran = null;
            		tran = data.get(date).get(prod.getProdid());
            		
            		if(getMonitoring().getRangeId()==1){
            		
            			if(tran!=null){
            				series.set(date, tran.getQuantity());
            			}else{
            				series.set(date, 0);
            			}
            		
            		}else if(getMonitoring().getRangeId()==2){
            			if("01".equalsIgnoreCase(date)){
            				date = "January";
            			}else if("02".equalsIgnoreCase(date)){
            				date = "February";
            			}else if("03".equalsIgnoreCase(date)){
            				date = "March";
            			}else if("04".equalsIgnoreCase(date)){
            				date = "April";
            			}else if("05".equalsIgnoreCase(date)){
            				date = "May";
            			}else if("06".equalsIgnoreCase(date)){
            				date = "June";
            			}else if("07".equalsIgnoreCase(date)){
            				date = "July";
            			}else if("08".equalsIgnoreCase(date)){
            				date = "August";
            			}else if("09".equalsIgnoreCase(date)){
            				date = "September";
            			}else if("10".equalsIgnoreCase(date)){
            				date = "October";
            			}else if("11".equalsIgnoreCase(date)){
            				date = "November";
            			}else if("12".equalsIgnoreCase(date)){
            				date = "December";
            			}
            			
            			
            			if(tran!=null){
            				series.set(date, tran.getQuantity());
            			}else{
            				series.set(date, 0);
            			}
            		}else{
            			if(tran!=null){
            				series.set(date, tran.getQuantity());
            			}else{
            				series.set(date, 0);
            			}
            		}
            		
            	}
        		model.addSeries(series);
        	}
        
        }
        
         
        return model;
    }
 
}