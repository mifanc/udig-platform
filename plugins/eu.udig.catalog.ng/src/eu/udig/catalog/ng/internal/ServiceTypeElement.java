/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.catalog.ng.internal;

/**
 * Object to hold ServiceTypeView elements
 * @author  Mifan Careem     mifanc@gmail.com
 * @since   1.2.0
 */
public class ServiceTypeElement {
    
    private String serviceTypeName;
    
    public ServiceTypeElement(){
       
    }
   
    public ServiceTypeElement(String serviceTypeName){
        this.serviceTypeName = serviceTypeName;
    }
    
    public String getServiceTypeName(){
        return this.serviceTypeName;
    }
    
    public void setServiceTypeName(String name){
        this.serviceTypeName = name;
    }
    
    public String toString(){
        return this.serviceTypeName;
    }
    
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 31*serviceTypeName.hashCode();
    }
    
    @Override
    public boolean equals( Object obj ) {
        // TODO Auto-generated method stub
        if ( obj==null)
            return false;
        return this.toString().equalsIgnoreCase(obj.toString());
    }
    
    

}
