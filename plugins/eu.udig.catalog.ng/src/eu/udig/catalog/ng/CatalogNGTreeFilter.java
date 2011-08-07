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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.ws.WebFault;

import org.geotools.data.DataStore;
import org.geotools.data.db2.DB2NGDataStoreFactory;
import org.geotools.data.h2.H2DataStoreFactory;
import org.geotools.data.postgis.PostgisDataStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wps.WebProcessingService;

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
    public Object getInputTree(String viewType, Object selection){
        //I'd like to use switch here on the String which apparently is appearing in Java SE7. For now..
        if(viewType.equalsIgnoreCase("servicetypeview")){
            return buildServiceTypeList();
        }
        else if(viewType.equalsIgnoreCase("serviceview")){
            return buildServiceList(selection);
        }
        else if(viewType.equalsIgnoreCase("datatypeview")){
            return null;
        }
        else if(viewType.equalsIgnoreCase("layerview")){
            return null;
        }
        return new Object();
    }
    
    /**
     * Iterate through a set of IResolves and build a service type list 
     *  of string values for the tree viewer
     *  @todo   Standardize names from Messages
     *  @todo   Get total list of classes from geotools
     *  @todo   Fix issues with l10n NLS message missing - ignoring currently
     * @return List ArrayList of String values for ServiceTypes
     */
    public List<String> buildServiceTypeList(){
        //Usage of Set ensures non-duplication of Service Type values
        serviceText = new HashSet<String>();
        for( ISearch searchCatalog : CatalogPlugin.getDefault().getCatalogs()){
            try {
                for( IResolve resolveItem : searchCatalog.members(null)){
                    //using {else-if} instead of {if}. A resource can be categorized under many categories?
                    System.out.println("service "+resolveItem.getTitle());
                    if( resolveItem.canResolve(ShapefileDataStore.class))
                        //serviceText.add(Messages.ServiceType_File);
                      serviceText.add("File"); //$NON-NLS-1$
                    else if( resolveItem.canResolve(WebMapServer.class))
                        //serviceText.add(Messages.ServiceType_Web);
                        serviceText.add("Web");//$NON-NLS-1$
                    else if( resolveItem.canResolve(PostgisDataStore.class))
                        //serviceText.add(Messages.ServiceType_Database);
                        serviceText.add("Database");//$NON-NLS-1$
                    //Location for everything else
                    //Where does Decoration go?
                    else
                        //serviceText.add(Messages.ServiceType_Uncategorized);
                        serviceText.add("Uncategorized");//$NON-NLS-1$
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new ArrayList<String>(serviceText);
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
    public List<String> buildServiceList(Object serviceType){
        //Usage of Set ensures non-duplication of Service Type values
        serviceText = new HashSet<String>();
        String serviceTypeValue = (String)serviceType;
        File thisFile,parentFile;
        for( ISearch searchCatalog : CatalogPlugin.getDefault().getCatalogs()){
            try {
                for( IResolve resolveItem : searchCatalog.members(null)){
                    //using {else-if} instead of {if}. A resource can be categorized under many categories?
                    if(serviceTypeValue.equalsIgnoreCase("file")){
                        //do adhoc directory grouping here as per proposal   
                        if( resolveItem.canResolve(ShapefileDataStore.class)){
                            //Quick hack to store directory. Get better way to do this
                                thisFile = new File(resolveItem.getTitle());
                                parentFile = thisFile.getParentFile();
                                if(parentFile.isDirectory()){
                                    //trust a set to keep this unique in a simple manner
                                    serviceText.add("DIR: "+parentFile.getName());
                                    //serviceText.add(resolveItem.getTitle());
                                }
                           
                              
                        }     
                    }
                    
                    else if(serviceTypeValue.equalsIgnoreCase("web")){
                        if( resolveItem.canResolve(WebMapServer.class) )
                            serviceText.add("WMS: "+resolveItem.getTitle());
                        else if( resolveItem.canResolve(WebProcessingService.class) )
                            serviceText.add("WPS: "+resolveItem.getTitle());   
                        else if( resolveItem.canResolve(WFSDataStore.class) )
                            serviceText.add("WFS: "+resolveItem.getTitle());

                    }
                    else if(serviceTypeValue.equalsIgnoreCase("database")){
                        if( resolveItem.canResolve(PostgisDataStore.class) )
                            serviceText.add("PostGIS: "+resolveItem.getTitle());
                        else if( resolveItem.canResolve(MysqlDataSource.class) )
                            serviceText.add("MySQL: "+resolveItem.getTitle());
                        else if( resolveItem.canResolve(DB2NGDataStoreFactory.class) )
                            serviceText.add("DB2: "+resolveItem.getTitle());
                        else if(resolveItem.canResolve(H2DataStoreFactory.class))
                            serviceText.add("H2: "+resolveItem.getTitle());
                    }
                    
                    else if(serviceTypeValue.equalsIgnoreCase("uncategorized")){
                            serviceText.add(resolveItem.getTitle());
                    }
                    else{
                        //Handle uncategorized
                        //Handle All
                        serviceText.add(resolveItem.getTitle());
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new ArrayList<String>(serviceText);
    }

    
}


