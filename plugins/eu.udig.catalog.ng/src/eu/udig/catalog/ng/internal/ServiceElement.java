package eu.udig.catalog.ng.internal;

public class ServiceElement {
    private String serviceTypeName;
    private String serviceName;
    
    public ServiceElement(){
       
    }
   
    public ServiceElement(String serviceTypeName, String serviceName){
        this.serviceTypeName = serviceTypeName;
        this.serviceName = serviceName;
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

    public String toString(){
        return serviceName;
    }
    
    @Override
    /**
     * @todo Make unique for servicetype and service. Same named service can be within 2 service types
     * However, having serviceName and serviceTypeName contribute to the hashcode doesn't work since serviceTypeName is null at beginning
     */
    public int hashCode() {
        // TODO Auto-generated method stub
            if(serviceName == null)
                return 31*"default".hashCode();
        return 31*serviceName.hashCode();
    }
    
    @Override
    public boolean equals( Object obj ) {
        // TODO Auto-generated method stub
        if ( obj==null)
            return false;
        return this.toString().equalsIgnoreCase(obj.toString());
    }


}
