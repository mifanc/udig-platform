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
package eu.udig.catalog.ng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.ws.WebFault;

import org.geotools.data.DataStore;
import org.geotools.data.db2.DB2NGDataStoreFactory;
import org.geotools.data.h2.H2DataStoreFactory;
import org.geotools.data.jdbc.JDBCDataStore;
import org.geotools.data.oracle.OracleNGDataStoreFactory;
import org.geotools.data.postgis.PostgisDataStore;
import org.geotools.data.postgis.PostgisDataStoreFactory;
import org.geotools.data.postgis.PostgisFeatureStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wps.WebProcessingService;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.ows.OWS;
import org.geotools.wfs.WFS;

import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IRepository;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.ISearch;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.memory.MemoryCatalog;

import eu.udig.catalog.ng.internal.DataTypeElement;
import eu.udig.catalog.ng.internal.ServiceElement;
import eu.udig.catalog.ng.internal.ServiceTypeElement;
import eu.udig.catalog.ng.ui.internal.Messages;

/**
 * TODO Purpose of 
 * <p>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * @author Mifan Careem mifanc@gmail.com
 * @since 1.2.0
 */

public class CatalogNGTreeFilter {
    
    //Variable to hold new filteredlist that can be passed to the setinput of the Tree
    public CatalogImpl filteredList;
    public Iterator itr;
    public IRepository catalog;
    public MemoryCatalog tempCatalog;
    
    public List<IResolve> foundCatalogs;
    public List<IResolve> foundCatalogItems;
    public Set<String> serviceText;
    
    public Set<ServiceTypeElement> serviceTypeSet;
    public Set<ServiceElement> serviceElementSet;
    public Set<DataTypeElement> dataTypeElementSet;
    public List<String> layers; //convert to IResolve
    
    private boolean DEBUG = false;
    
    MemoryCatalog localCatalog;
    
    
    /**
     * Filter a CatalogImpl List and provide a List of Service Types
     * @todo take a CatalogImpl object, clone it, create  a new set of services as a list, and return it
     * @todo handle listeners.
     * @param serviceList CatalogImpl List to be filtered
     * @return CatalogImpl List that is filtered
     */
    public ICatalog getServiceTypes(CatalogImpl serviceList){
        filteredList = new CatalogImpl();
        
        itr = serviceList.members(null).iterator();
        while(itr.hasNext()){
                filteredList.add((IService)itr.next());
        }
        
     
        return filteredList;
    }
    
    
    /**
     * Return a suitable input object for the Tree View based on the type of view
     * <p>
     * The following types will be returned based on the view
     * <ul>
     * <li>Service Type View - List with String values for Categorizations
     * <li>Service View - List with String values for Categorizations
     * <li>Data Type View - List with String values for Categorizations
     * <li>Layer View - List with String values for Categorizations
     * </ul>
     * </p>
     * 
     * @param viewType  Type of view
     * @param selection selection value
     * @return generic Object
     */
    public Object getInputTree(String viewType, Object selection, Object persSelection, Object dataType){
        //I'd like to use switch here on the String which apparently is appearing in Java SE7. For now..
        if(viewType.equalsIgnoreCase("servicetypeview")){
            return buildServiceTypeList();
        }
        else if(viewType.equalsIgnoreCase("serviceview")){
            return buildServiceList(selection);
        }
        else if(viewType.equalsIgnoreCase("datatypeview")){
            return buildDataTypeList(selection, persSelection);
        }
        else if(viewType.equalsIgnoreCase("layerview")){
            return buildLayerList(selection, persSelection, dataType);
        }
        return new Object();
    }
        
    /**
     * Iterate through a set of IResolves and build a service type list 
     *  of string values for the tree viewer
     *  @todo   Standardize names from Messages
     *  @todo   Get total list of classes from geotools
     *  @todo   Fix issues with l10n NLS message missing - ignoring currently
     *  @see    ServiceTypeElement
     * @return List ArrayList of String values for ServiceTypes
     */
    public List<ServiceTypeElement> buildServiceTypeList(){
        //Usage of Set ensures non-duplication of Service Type values - ensured that hashCode and equals is implemented right!
        //@see ServiceTypeElement
        serviceTypeSet = new HashSet<ServiceTypeElement>();
        for( ISearch searchCatalog : CatalogPlugin.getDefault().getCatalogs()){
            try {
                for( IResolve resolveItem : searchCatalog.members(null)){
                    //using {else-if} instead of {if}. A resource can be categorized under many categories?
                    //System.out.println("service "+resolveItem.getTitle());
                    

                    if( resolveItem.canResolve(ShapefileDataStore.class)        ||
                        resolveItem.canResolve(GeoTiffFormat.class)                            
                    )
                        serviceTypeSet.add(new ServiceTypeElement("File")); //$NON-NLS-1$
                    
                    else if( resolveItem.canResolve(WebMapServer.class)         ||
                             resolveItem.canResolve(WebProcessingService.class) ||
                             resolveItem.canResolve(WFS.class)                  ||
                             resolveItem.canResolve(WFSDataStore.class)         ||
                             resolveItem.canResolve(OWS.class)
                    )
                        serviceTypeSet.add(new ServiceTypeElement("Web")); //$NON-NLS-1$
                    
                    else if( resolveItem.canResolve(PostgisDataStore.class)           ||
                             resolveItem.canResolve(PostgisFeatureStore.class)        ||
                             resolveItem.canResolve(PostgisDataStoreFactory.class)    ||
                             resolveItem.canResolve(MySQLConnection.class)            ||
                             resolveItem.canResolve(H2DataStoreFactory.class)         ||
                             resolveItem.canResolve(OracleNGDataStoreFactory.class)   ||
                             resolveItem.canResolve(DB2NGDataStoreFactory.class)      ||
                             resolveItem.canResolve(Connection.class)
                             
                    )
                        serviceTypeSet.add(new ServiceTypeElement("Database")); //$NON-NLS-1$
                    //Location for everything else
                    //Where does Decoration go?
                    else
                        serviceTypeSet.add(new ServiceTypeElement("Uncategorized")); //$NON-NLS-1$
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new ArrayList<ServiceTypeElement>(serviceTypeSet);
    }

    /**
     * Iterate through a set of IResolves and build a service list 
     *  of string values for the tree viewer based on selected service type
     *  @todo   Standardize names from Messages
     *  @todo   Get total list of classes from geotools
     *  @todo   Fix issues with l10n NLS message missing - ignoring currently
     *  @todo   Handle uncategorized
     * @param serviceType selected service type
     * @return List ArrayList of String values for ServiceTypes
     */
    public List<ServiceElement> buildServiceList(Object serviceType){
        //handle first time null usage
        if(serviceType == null)
            return new ArrayList<ServiceElement>();
        //Usage of Set ensures non-duplication of Service Type values
        serviceElementSet = new HashSet<ServiceElement>();
        String serviceTypeValue = (String)serviceType;
        File thisFile,parentFile;
        for( ISearch searchCatalog : CatalogPlugin.getDefault().getCatalogs()){
            try {
                for( IResolve resolveItem : searchCatalog.members(null)){
                    //using {else-if} instead of {if}. A resource can be categorized under many categories?
                    
                    /**
                     * ServiceType: File
                     * Operation:   Show adhoc parent directories
                     */
                    if(serviceTypeValue.equalsIgnoreCase("file")){
                        //do adhoc directory grouping here as per proposal   
                        if( resolveItem.canResolve(ShapefileDataStore.class)        ||
                            resolveItem.canResolve(GeoTiffFormat.class)                            
                        ){
                            //Quick hack to store directory. Get better way to do this
                                thisFile = new File(resolveItem.getTitle());
                                parentFile = thisFile.getParentFile();
                                if(parentFile.isDirectory()){
                                    //trust a set to keep this unique in a simple manner
                                    serviceElementSet.add(new ServiceElement(serviceTypeValue, parentFile.getName()));
                                }
                        }     
                    }
                    
                    /**
                     * ServiceType: Web
                     * Operation:   Show Service Groupings (WMS, WFS, WPS etc.)
                     */
                    else if(serviceTypeValue.equalsIgnoreCase("web")){
                        if( resolveItem.canResolve(WebMapServer.class) )
                            serviceElementSet.add(new ServiceElement(serviceTypeValue, "WMS"));
                        else if( resolveItem.canResolve(WebProcessingService.class) )
                            serviceElementSet.add(new ServiceElement(serviceTypeValue, "WPS"));
                        else if( resolveItem.canResolve(WFSDataStore.class) )
                            serviceElementSet.add(new ServiceElement(serviceTypeValue, "WFS"));
                    }
                    
                    /**
                     * ServiceType: Database
                     * Operation:   Show database groupings (Oracle, PostGIS, MySQL etc.)
                     */
                    else if(serviceTypeValue.equalsIgnoreCase("database")){
                        if( resolveItem.canResolve(PostgisDataStore.class) )
                            serviceElementSet.add(new ServiceElement(serviceTypeValue, "PostGIS"));
                        else if( resolveItem.canResolve(MysqlDataSource.class) )
                            serviceElementSet.add(new ServiceElement(serviceTypeValue, "MySQL"));
                        else if( resolveItem.canResolve(DB2NGDataStoreFactory.class) )
                            serviceElementSet.add(new ServiceElement(serviceTypeValue, "DB2"));
                        else if(resolveItem.canResolve(H2DataStoreFactory.class))
                            serviceElementSet.add(new ServiceElement(serviceTypeValue, "H2"));
                    }
                    
                    else if(serviceTypeValue.equalsIgnoreCase("uncategorized")){
                        serviceElementSet.add(new ServiceElement(serviceTypeValue, resolveItem.getTitle()));
                        //serviceText.add(resolveItem.getTitle());
                    }
                    else{
                        //Handle uncategorized
                        //Handle All
                        serviceElementSet.add(new ServiceElement(serviceTypeValue, resolveItem.getTitle()));
                        //serviceText.add(resolveItem.getTitle());
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new ArrayList<ServiceElement>(serviceElementSet);
    }
    
    
    
    
    /**
     * Iterate through a set of IResolves and build a data type | feature type list 
     *  of string values for the tree viewer
     *  @todo   Use service type, service, data type as params - accept null values
     *  @todo   Standardize names from Messages
     *  @todo   Get total list of classes from geotools
     *  @todo   Fix issues with l10n NLS message missing - ignoring currently
     * @return List ArrayList of String values for ServiceTypes
     */
    public List<DataTypeElement> buildDataTypeList(Object selectedServiceName, Object currentServiceTypeName){
        //handle first time null
        if(selectedServiceName == null)
            return new ArrayList<DataTypeElement>();
              
        //Usage of Set ensures non-duplication of Service Type values
        dataTypeElementSet = new HashSet<DataTypeElement>();
        String serviceTypeValue = (String)currentServiceTypeName;
        String serviceName = (String)selectedServiceName;
        File thisFile,parentFile;
        for( ISearch searchCatalog : CatalogPlugin.getDefault().getCatalogs()){
            try {
                for( IResolve resolveItem : searchCatalog.members(null)){
                    //using {else-if} instead of {if}. A resource can be categorized under many categories?
                    /**
                     * ServiceType: File
                     * Service:     <name of directory>
                     * Operation:   Show grouping of file type (Shapefile, GeoTIFF)
                     */
                    if(serviceTypeValue.equalsIgnoreCase("file")){
                        if( resolveItem.canResolve(ShapefileDataStore.class) ||
                            resolveItem.canResolve(GeoTiffFormat.class)
                        ){
                            //Quick hack to store directory. Get better way to do this
                                thisFile = new File(resolveItem.getTitle());
                                parentFile = thisFile.getParentFile();
                                if(parentFile.isDirectory() && parentFile.getName().equalsIgnoreCase(serviceName)){
                                    //trust a set to keep this unique in a simple manner
                                    if(resolveItem.canResolve(ShapefileDataStore.class))
                                        dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, "ShapeFile"));
                                    else if(resolveItem.canResolve(GeoTiffFormat.class))
                                        dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, "GeoTIFF"));

                                }
                        }     
                    }
                    
                    /**
                     * ServiceType: Web
                     * Service: WMS, WFS, WPS, ...
                     * Operation: Show Server names
                     */
                    else if(serviceTypeValue.equalsIgnoreCase("web")){
                        if(serviceName.equalsIgnoreCase("wms")){
                            if( resolveItem.canResolve(WebMapServer.class)){
                                dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, resolveItem.getTitle()));
                            }
                        }
                        else if(serviceName.equalsIgnoreCase("wfs")){
                            if(resolveItem.canResolve(WFSDataStore.class)){
                                dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, resolveItem.getTitle()));
                            }
                        }
                        else if(serviceName.equalsIgnoreCase("wps")){
                            if(resolveItem.canResolve(WebProcessingService.class)){
                                dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, resolveItem.getTitle()));
                            }
                        }
                            
                    }
                    
                    /**
                     * ServiceType: Database
                     * Service: Oracle, MySQL, PostGIS, H2, ...
                     * Operation: Show database names
                     */
                    else if(serviceTypeValue.equalsIgnoreCase("database")){
                        if(serviceName.equalsIgnoreCase("postgis")){
                            if( resolveItem.canResolve(PostgisDataStore.class) )
                                dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, resolveItem.getTitle()));
                        }
                        else if(serviceName.equalsIgnoreCase("mysql")){
                            if( resolveItem.canResolve(MysqlDataSource.class) )
                                dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, resolveItem.getTitle()));
                        }
                        else if(serviceName.equalsIgnoreCase("db2")){
                            if( resolveItem.canResolve(DB2NGDataStoreFactory.class) )
                                dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, resolveItem.getTitle()));
                        }
                        else if(serviceName.equalsIgnoreCase("h2")){
                            if(resolveItem.canResolve(H2DataStoreFactory.class))
                                dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, resolveItem.getTitle()));
                        }
                    }
                    
                    else if(serviceTypeValue.equalsIgnoreCase("uncategorized")){
                        dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, resolveItem.getTitle()));
                        //serviceText.add(resolveItem.getTitle());
                    }
                    else{
                        //Handle uncategorized
                        //Handle All
                        dataTypeElementSet.add(new DataTypeElement(serviceTypeValue, serviceName, resolveItem.getTitle()));
                        //serviceText.add(resolveItem.getTitle());
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new ArrayList<DataTypeElement>(dataTypeElementSet);

    }

    
    /**
     * Iterate through a set of IResolves and build a layer list 
     *  of string values for the tree viewer
     *  under construction
     *  @todo   Display actual IResolves
     *  @todo   Use service type, service, data type as params - accept null values
     *  @todo   Standardize names from Messages
     *  @todo   Get total list of classes from geotools
     *  @todo   Fix issues with l10n NLS message missing - ignoring currently
     * @return List ArrayList of String values for ServiceTypes
     */
    public List<String> buildLayerList(Object selectedServiceName, Object currentServiceTypeName, Object selectedDataType){
        //handle first time null
        if(selectedDataType == null)
            return new ArrayList<String>();
              
        //Usage of Set ensures non-duplication of Service Type values
        layers = new ArrayList<String>();
        String serviceTypeValue = (String)currentServiceTypeName;
        String serviceName = (String)selectedServiceName;
        String dataTypeName = (String)selectedDataType;
        File thisFile,parentFile;
        for( ISearch searchCatalog : CatalogPlugin.getDefault().getCatalogs()){
            try {
                for( IResolve resolveItem : searchCatalog.members(null)){
                    //using {else-if} instead of {if}. A resource can be categorized under many categories?
                    if(serviceTypeValue.equalsIgnoreCase("file")){
                        //adhoc grouped directories here as per proposal   
                        //@todo only handling shp here. get all other local file types
                        if( resolveItem.canResolve(ShapefileDataStore.class) ||
                            resolveItem.canResolve(GeoTiffFormat.class)
                        ){
                            //Quick hack to store directory. Get better way to do this
                                thisFile = new File(resolveItem.getTitle());
                                parentFile = thisFile.getParentFile();
                                if(parentFile.isDirectory() && parentFile.getName().equalsIgnoreCase(serviceName)){
                                    if(dataTypeName.equalsIgnoreCase("shapefile")){
                                        if(resolveItem.canResolve(ShapefileDataStore.class))
                                            layers.add(resolveItem.getTitle());
                                    }

                                }
                        }     
                    }
                    
                    else if(serviceTypeValue.equalsIgnoreCase("web")){
                        if( resolveItem.canResolve(WebMapServer.class)  || 
                            resolveItem.canResolve(WebProcessingService.class) ||
                            resolveItem.canResolve(WFSDataStore.class)
                        ){
                            for(IResolve layer : resolveItem.members(null)){
                                layers.add(resolveItem.getTitle());
                            }
                        }
                            
                    }
                    
                    else if(serviceTypeValue.equalsIgnoreCase("database")){
                        if( resolveItem.canResolve(PostgisDataStore.class) )
                            layers.add(resolveItem.getTitle());
                            //serviceText.add("PostGIS: "+resolveItem.getTitle());
                        else if( resolveItem.canResolve(MysqlDataSource.class) )
                            layers.add(resolveItem.getTitle());
                            //serviceText.add("MySQL: "+resolveItem.getTitle());
                        else if( resolveItem.canResolve(DB2NGDataStoreFactory.class) )
                            layers.add(resolveItem.getTitle());
                            //serviceText.add("DB2: "+resolveItem.getTitle());
                        else if(resolveItem.canResolve(H2DataStoreFactory.class))
                            layers.add(resolveItem.getTitle());
                            //serviceText.add("H2: "+resolveItem.getTitle());
                    }
                    
                    else if(serviceTypeValue.equalsIgnoreCase("uncategorized")){
                        layers.add(resolveItem.getTitle());
                        //serviceText.add(resolveItem.getTitle());
                    }
                    else{
                        //Handle uncategorized
                        //Handle All
                        layers.add(resolveItem.getTitle());
                        //serviceText.add(resolveItem.getTitle());
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return layers;
    }
    


    
}



