/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package eu.udig.catalog.ng.ui;

import net.refractions.udig.catalog.ui.StatusLineMessageBoardAdapter;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import eu.udig.catalog.ng.CatalogNGTreeFilter;
import eu.udig.catalog.ng.internal.ServiceTypeElement;

/**
 * View for Services- the 2nd level in the catalog browse view. 
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
 */
public class ServiceView extends ViewPart implements ISelectionListener {
    
    public static final String VIEW_ID = "eu.udig.catalog.ng.ui.ServiceView"; //$NON-NLS-1$
    public final String TYPE_ID = "ServiceView"; //$NON-NLS-1$
    
    //setup tree view
    private CatalogNGTreeView treeViewer;
    private CatalogNGTreeFilter treeFilter;
    
    //actions for service type
    private Action removeAction;
    private Action propertiesAction;
    
    private String selectionValue;

    public ServiceView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl( Composite parent ) {
        // TODO Auto-generated method stub
        treeViewer = new CatalogNGTreeView(parent, TYPE_ID);
        treeFilter = new CatalogNGTreeFilter();
        
        //treeViewer.setInput(null);
        //treeViewer.setInput(treeFilter.getInputTree(TYPE_ID,new String("all"),new String("all")));
        treeViewer.setMessageBoard(new StatusLineMessageBoardAdapter(getViewSite().getActionBars().getStatusLineManager()));
        
        //listen to selection events from other views
        //@todo: workbench listener?
        getViewSite().getPage().addSelectionListener(this);
        
        //Publish selection events to other views
        //@todo Make this view specific?
        getSite().setSelectionProvider(treeViewer);

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        // TODO Auto-generated method stub
        if( selection instanceof IStructuredSelection){
            Object selected = ((IStructuredSelection) selection).getFirstElement();
            if ( selected instanceof ServiceTypeElement){
                selectionValue = ((ServiceTypeElement)selected).getServiceTypeName();
                System.out.println("SELECTION: "+selectionValue);
                //treeViewer.setInput(treeFilter.getInputTree(TYPE_ID,selectionValue,null));
            }
            //Create tree  based on selection
            //treeViewer.setInput(treeFilter.getInputTree(TYPE_ID,selected));
            
        }
        
        
    }

}
