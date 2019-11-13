package com.italia.ipos.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.italia.ipos.controller.UserDtls;
import com.italia.ipos.session.UserTrans;

/**
 * 
 * @author mark italia
 * @since 09/30/2016
 * @version 1.0
 *
 */
public class IObject {

	public static Method[] getAccessibleMethods(Class clazz) {
	    List<Method> result = new ArrayList<Method>();
	    while (clazz != null) {
	        for (Method method : clazz.getDeclaredMethods()) {
	            int modifiers = method.getModifiers();
	            if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
	                result.add(method);
	            }
	        }
	        clazz = clazz.getSuperclass();
	    }
	    return result.toArray(new Method[result.size()]);
	}
	
	public static String getCallerCallerClassName() { 
	    StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
	    String callerClassName = null;
	    for (int i=1; i<stElements.length; i++) {
	        StackTraceElement ste = stElements[i];
	        if (!ste.getClassName().equals(IObject.class.getName())&& ste.getClassName().indexOf("java.lang.Thread")!=0) {
	            if (callerClassName==null) {
	                callerClassName = ste.getClassName();
	            } else if (!callerClassName.equals(ste.getClassName())) {
	                return ste.getClassName();
	            }
	        }
	    }
	    return null;
	 }
	
	/**
     * Get the caller class.
     * @param level The level of the caller class.
     *              For example: If you are calling this class inside a method and you want to get the caller class of that method,
     *                           you would use level 2. If you want the caller of that class, you would use level 3.
     *
     *              Usually level 2 is the one you want.
     * @return The caller class.
     * @throws ClassNotFoundException We failed to find the caller class.
     */
    public static Class getCallerClass(int level) throws ClassNotFoundException {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        String rawFQN = stElements[level+1].toString().split("\\(")[0];
        return Class.forName(rawFQN.substring(0, rawFQN.lastIndexOf('.')));
    }
    
    public static void iterate(Class clazz){
    	
    	for (Class<?> c = clazz; c != null; c = c.getSuperclass())
        {
            Field[] fields = c.getDeclaredFields();
            for (Field classField : fields)
            {
                
            	
            }
        }
    }
    
    public static void main(String[] args) {
    	/*try{
    		Class c = IObject.getCallerClass(2);
    		System.out.println(c.getName());
    	}catch(ClassNotFoundException e){}*/
    	IObject o = new IObject();
    	UserTrans u = new UserTrans();
    	u.setActiondetails("hello");
    	//o.iterateGet(u.getClass());
    	o.iterate(u.getClass());
    	Class<?> c = u.getClass();
    	Field[] fields = c.getDeclaredFields();
    	for(Field field : fields){
    		//o.runGetter(field, u);
    		IObject.convert(field,u);
    	}
    	
    	/*try{
    	Field field = u.getClass().getDeclaredField("actiondetails");    
    	  field.setAccessible(true);
    	  Object value = field.get(u);
    	  System.out.println("value " + value);
    	}catch(Exception e){}*/
    	
    	
    	
	}
    
    public static <E> E convert(E... object){
    	Field field = (Field)object[0];
    	UserDtls user = (UserDtls)object[1];
    	E u = (E)user;
    	
    	for (Method method : u.getClass().getMethods())
        {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3)))
            {
                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase()))
                {
                	try{
                	String fld = field.getName();	
                	//System.out.println("Method " + method.getName() + " field " +fld);
                	Field f = u.getClass().getDeclaredField(fld);
                	f.setAccessible(true);
                	System.out.println("XXX :"+ fld + " = " + (Object)f.get(u));
                	}catch(Exception e){e.getStackTrace();}
                }
            }
        }
    	
    	return u;
    }
    
    public static Object runGetter(Field field, Object object)
    {
    	Class o = object.getClass();
    	Object u;
    	u = (UserDtls)object;
    	
        for (Method method : o.getMethods())
        {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3)))
            {
                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase()))
                {
                	try{
                	String fld = field.getName();	
                	//System.out.println("Method " + method.getName() + " field " +fld);
                	Field f = u.getClass().getDeclaredField(fld);
                	f.setAccessible(true);
                	System.out.println("XXX :"+ fld + " = " + (Object)f.get(u));
                	}catch(Exception e){e.getStackTrace();}
                }
            }
        }


        return null;
    }
    
    
    
    public void iterateGet(Class clazz) {
        StringBuilder sb = new StringBuilder();

        Class<?> thisClass = clazz;
        System.out.println("clazzz : " + thisClass.getName());
        try {
            thisClass = Class.forName(this.getClass().getName());

            Field[] aClassFields = thisClass.getDeclaredFields();
            System.out.println("Fields count " + aClassFields.length);
            sb.append(this.getClass().getSimpleName() + " [ ");
            for(Field f : aClassFields){
                String fName = f.getName();
               // sb.append("(" + f.getType() + ") " + fName + " = " + f.get(this) + ", ");
                System.out.println("(" + f.getType() + ") " + fName + " = " + f.get(this));
                
                
            }
            sb.append("]");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return sb.toString();
    }
    
    
    
}
