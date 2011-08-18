/**
 * 
 */
package eu.udig.catalog.ng.ui;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import eu.udig.catalog.ng.CatalogNGTreeFilter;

/**
 * View for Service Type - the first level in the catalog browse view. 
 * <p>
 * The catalog browse, aka CatalongNG view will consist of the following views
 * <ul>
 * <li>Service Type</li>
 * <li>Service</li>
 * <li>Data Type | Feature Type</li>
 * <li>Layers</li>
 * </ul>
 * </p>
 * @author  Mifan Careem     mifanc@gmail.com
 * @since   1.2.0
 * @todo    Implement IAdaptable
 * @todo    ISelectionListener
 * @todo    Currently borrows from CatalogView - change to be unique
 */
public class CatalogNGView extends CatalogNGViewPart {
    
    public static final String VIEW_ID = "eu.udig.catalog.ng.ui.CatalongNGView"; //$NON-NLS-1$
    
    //Types of the views used for building unique trees
    public final String SERVICE_TYPE_ID = "ServiceTypeView"; //$NON-NLS-1$
    public final String SERVICE_ID = "ServiceView"; //$NON-NLS-1$
    public final String DATA_TYPE_ID = "DataTypeView"; //$NON-NLS-1$
    
    private CatalogNGTreeView treeViewerServiceType,treeViewerService,treeViewerDataType;
    private CatalogNGTreeFilter treeFilterServiceType,treeFilterService,treeFilterDataType;
    

    /**
     * 
     */
    public CatalogNGView() {
        // TODO Auto-generated constructor stub
    }


    @Override
    public void createPartControl( Composite parent ) {
        // TODO Auto-generated method stub
        treeViewerServiceType = new CatalogNGTreeView(parent, SERVICE_TYPE_ID);
        treeFilterServiceType = new CatalogNGTreeFilter();
        
        treeViewerServiceType.setInput(treeFilterServiceType.getInputTree(SERVICE_TYPE_ID,null,null));
        
        
        treeViewerService = new CatalogNGTreeView(parent, SERVICE_ID);
        treeFilterService = new CatalogNGTreeFilter();
        treeViewerService.setInput(treeFilterService.getInputTree(SERVICE_ID,null,null));
        
        /*
        treeViewerDataType = new CatalogNGTreeView(parent, DATA_TYPE_ID);
        treeFilterDataType = new CatalogNGTreeFilter();
        treeViewerDataType.setInput(treeFilterDataType.getInputTree(DATA_TYPE_ID,null,null));
        */
        FillLayout layout = new FillLayout();
        parent.setLayout(layout);
        
        super.createPartControl(parent);
    }
    
    



}
