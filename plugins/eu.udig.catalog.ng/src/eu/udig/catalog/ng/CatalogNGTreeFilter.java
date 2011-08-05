/**
 * 
 */
package eu.udig.catalog.ng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.ws.WebFault;

import org.geotools.data.postgis.PostgisDataStore;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.wms.WebMapServer;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IRepository;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.ISearch;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.memory.MemoryCatalog;

/**
 * TODO Purpose of 
 * <p>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * @author nazgul
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
     * @return generic Object
     */
    public Object getInputTree(String viewType){
        //I'd like to use switch here on the String which apparently is appearing in Java SE7. For now..
        if(viewType.equalsIgnoreCase("servicetypeview")){
            return buildServiceTypeList();
        }
        else if(viewType.equalsIgnoreCase("serviceview")){
            
        }
        else if(viewType.equalsIgnoreCase("datatypeview")){
            
        }
        else if(viewType.equalsIgnoreCase("layerview")){
            
        }
        return new Object();
    }
    
    /**
     * Iterate through a set of IResolves and build a service type list 
     *  of string values for the tree viewer
     *  @todo   Standardize names from Messages
     *  @todo   Get total list of classes from geotools
     * @return List ArrayList of String values for ServiceTypes
     */
    public List<String> buildServiceTypeList(){
        //Usage of Set ensures non-duplication of Service Type values
        serviceText = new HashSet<String>();
        for( ISearch searchCatalog : CatalogPlugin.getDefault().getCatalogs()){
            try {
                for( IResolve resolveItem : searchCatalog.members(null)){
                    //using {else-if} instead of {if}. A resource can be categorized under many categories?
                    if( resolveItem.canResolve(ShapefileDataStore.class))
                        serviceText.add("File"); //$NON-NLS-1$
                    else if( resolveItem.canResolve(WebMapServer.class))
                        serviceText.add("Web"); //$NON-NLS-1$
                    else if( resolveItem.canResolve(PostgisDataStore.class))
                        serviceText.add("Database"); //$NON-NLS-1$
                    //Location for everything else
                    else
                        serviceText.add("Uncategorized"); //$NON-NLS-1$    
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return new ArrayList<String>(serviceText);
    }
    
}


