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
package eu.udig.catalog.ng.ui;



import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import eu.udig.catalog.ng.CatalogNGTreeFilter;
import eu.udig.catalog.ng.internal.ServiceTypeElement;
import eu.udig.catalog.ng.ui.internal.SelectionProviderWrapper;

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
public class CatalogNGView extends CatalogNGViewPart implements ISelectionListener {
    
    public static final String VIEW_ID = "eu.udig.catalog.ng.ui.CatalongNGView"; //$NON-NLS-1$
    
    //Types of the views used for building unique trees
    public final String SERVICE_TYPE_ID = "ServiceTypeView"; //$NON-NLS-1$
    public final String SERVICE_ID = "ServiceView"; //$NON-NLS-1$
    public final String DATA_TYPE_ID = "DataTypeView"; //$NON-NLS-1$
    
    private CatalogNGTreeView treeViewerServiceType,treeViewerService,treeViewerDataType;
    private CatalogNGTreeFilter treeFilterServiceType,treeFilterService,treeFilterDataType;
    
    String selectionValue = "";
    
    private SelectionProviderWrapper selectionProviderWrapper = new SelectionProviderWrapper();
    


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
        treeViewerServiceType.getControl().addFocusListener(new FocusListener(){
            
            @Override
            public void focusLost( FocusEvent e ) {
                // TODO Auto-generated method stub
                
                
            }
            
            @Override
            public void focusGained( FocusEvent e ) {
                // TODO Auto-generated method stub
                System.out.println("focus from servicetype");
                //getSite().setSelectionProvider(treeViewerServiceType);
                selectionProviderWrapper.setSelectionProvider(treeViewerServiceType);
                
            }
        });


        treeViewerService = new CatalogNGTreeView(parent, SERVICE_ID);
        treeFilterService = new CatalogNGTreeFilter();
        treeViewerService.setInput(treeFilterService.getInputTree(SERVICE_ID,null,null));
        treeViewerService.getControl().addFocusListener(new FocusListener(){
            
            @Override
            public void focusLost( FocusEvent e ) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void focusGained( FocusEvent e ) {
                // TODO Auto-generated method stub
                System.out.println("focus from service");
                //getSite().setSelectionProvider(treeViewerService);
                selectionProviderWrapper.setSelectionProvider(treeViewerService);
                
            }
        });
        
        treeViewerDataType = new CatalogNGTreeView(parent, DATA_TYPE_ID);
        treeFilterDataType = new CatalogNGTreeFilter();
        treeViewerDataType.setInput(treeFilterDataType.getInputTree(DATA_TYPE_ID, null, null));
        treeViewerDataType.getControl().addFocusListener(new FocusListener(){
            
            @Override
            public void focusLost( FocusEvent e ) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void focusGained( FocusEvent e ) {
                // TODO Auto-generated method stub
                System.out.println("focus from datatype");
                //getSite().setSelectionProvider(treeViewerDataType);
                
            }
        });
        
        
        //cannot have 2 selection providers withing a viewpart. Need to implement custom selectionproviders @see
        //getSite().setSelectionProvider(treeViewerServiceType);
        //getSite().setSelectionProvider(treeViewerService);
        getSite().setSelectionProvider(selectionProviderWrapper);        
        getViewSite().getPage().addSelectionListener(this);
        
        
        /*
        treeViewerDataType = new CatalogNGTreeView(parent, DATA_TYPE_ID);
        treeFilterDataType = new CatalogNGTreeFilter();
        treeViewerDataType.setInput(treeFilterDataType.getInputTree(DATA_TYPE_ID,null,null));
        */
        FillLayout layout = new FillLayout();
        parent.setLayout(layout);
        
        super.createPartControl(parent);
    }


    @Override
    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        // TODO Auto-generated method stub
        if( selection instanceof IStructuredSelection){
            Object selected = ((IStructuredSelection) selection).getFirstElement();
            
            if ( selected instanceof ServiceTypeElement){
                selectionValue = ((ServiceTypeElement)selected).getServiceTypeName();
                System.out.println("SELECTION: "+selectionValue);
                treeViewerService.setInput(treeFilterService.getInputTree(SERVICE_ID,selectionValue,null));
            }
            //Create tree  based on selection
            //treeViewer.setInput(treeFilter.getInputTree(TYPE_ID,selected));
            
        }
        
    }



    
    



}
