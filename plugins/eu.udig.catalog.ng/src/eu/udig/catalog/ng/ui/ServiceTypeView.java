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
import net.refractions.udig.internal.ui.IDropTargetProvider;
import net.refractions.udig.ui.UDIGDragDropUtilities;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;

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
public class ServiceTypeView extends ViewPart implements ISetSelectionTarget, IDropTargetProvider{
    public static final String VIEW_ID = "eu.udig.catalog.ng.ui.ServiceTypeView"; //$NON-NLS-1$
    public final String TYPE_ID = "ServiceTypeView"; //$NON-NLS-1$
    
    //setup tree view
    CatalogNGTreeView treeViewer;
    
    //actions for service type
    private Action removeAction;
    private Action propertiesAction;

    public ServiceTypeView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl( Composite parent ) {
        // TODO Auto-generated method stub
        //treeViewer = new CatalogNGTreeView(parent, true);
        treeViewer = new CatalogNGTreeView(parent, TYPE_ID);
        treeViewer.setMessageBoard(new StatusLineMessageBoardAdapter(getViewSite().getActionBars().getStatusLineManager()));
        UDIGDragDropUtilities.addDragDropSupport(treeViewer, this);
        getSite().setSelectionProvider(treeViewer);

        
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
        treeViewer.getControl().setFocus();

    }

    @Override
    public Object getTarget( DropTargetEvent event ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void selectReveal( ISelection selection ) {
        // TODO Auto-generated method stub
        
    }

}
