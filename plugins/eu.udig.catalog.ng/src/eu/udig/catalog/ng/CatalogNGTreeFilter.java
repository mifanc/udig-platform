/**
 * 
 */
package eu.udig.catalog.ng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
     * Filter catalogs and create a MemoryCatalog based on view to return to the Tree
     * @param viewType
     * @return ISearch MemoryCatalog Implementation for the TreeView
     */
    public ISearch getCatalogTree(String viewType){
        //I'd like to use switch here on the String which apparently is appearing in Java SE7. For now..
        if(viewType.equalsIgnoreCase("servicetypeview")){
            foundCatalogs = new ArrayList<IResolve>();
            for (ISearch searchCatalog : CatalogPlugin.getDefault().getCatalogs()){
                foundCatalogs.addAll(searchCatalog.search("*", null, null));
            }
            //Convert IResolves from above to services and create  a ISearch
            //TEST
            MemoryCatalog localCatalog = new MemoryCatalog(new ID(CatalogPlugin.getDefault().getLocalCatalog().getID(),"file"), "LocalMemoryCatalog", null);
           return localCatalog; 
           
        }
        else if(viewType.equalsIgnoreCase("serviceview")){
            
        }
        else if(viewType.equalsIgnoreCase("datatypeview")){
            
        }
        else if(viewType.equalsIgnoreCase("layerview")){
            
        }
        return new ID(sourceCatalog.getID(),"");
    }
    
}
