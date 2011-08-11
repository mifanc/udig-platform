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
 * Object to hold DataTypeView elements and handle persistence
 * @author  Mifan Careem     mifanc@gmail.com
 * @since   1.2.0
 */
public class DataTypeElement {
    private String serviceTypeName;
    private String serviceName;
    private String dataTypeName;
    
    public DataTypeElement(){}
   
    public DataTypeElement(String serviceTypeName, String serviceName, String dataTypeName){
        this.serviceTypeName = serviceTypeName;
        this.serviceName = serviceName;
        this.dataTypeName = dataTypeName;
    }
    
    public String getServiceTypeName(){
        return this.serviceTypeName;
    }
    
    public void setServiceTypeName(String name){
        this.serviceTypeName = name;
    }
    
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName( String serviceName ) {
        this.serviceName = serviceName;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public void setDataTypeName( String dataTypeName ) {
        this.dataTypeName = dataTypeName;
    }

    public String toString(){
        return dataTypeName;
    }
    
    @Override
    /**
     * @todo Make unique for servicetype and service. Same named service can be within 2 service types
     * However, having serviceName and serviceTypeName contribute to the hashcode doesn't work since serviceTypeName is null at beginning
     */
    public int hashCode() {
        // TODO Auto-generated method stub
            if(dataTypeName == null)
                return 31*"defaultdt".hashCode();
        return 31*dataTypeName.hashCode();
    }
    
    @Override
    public boolean equals( Object obj ) {
        // TODO Auto-generated method stub
        if ( obj==null)
            return false;
        return this.toString().equalsIgnoreCase(obj.toString());
    }

}