package eu.udig.catalog.ng.internal;

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
