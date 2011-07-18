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

import net.refractions.udig.catalog.CatalogServiceTypePlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolve.Status;
import net.refractions.udig.catalog.ui.CatalogViewerSorter;
import net.refractions.udig.catalog.ui.IMessageBoard;
import net.refractions.udig.catalog.ui.ResolveContentProvider;
import net.refractions.udig.catalog.ui.ResolveLabelProviderSimple;
import net.refractions.udig.catalog.ui.ResolveTitlesDecorator;
import net.refractions.udig.catalog.ui.internal.Messages;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides Tree view of the Registry Service Types for a unified catalog NG view 
 * @todo    Look into making this generic for all 4 views
 * <p>
 * Supports the following:
 * <ul>
 * <li>List
 * <li>IService
 * <li>IGeoReference
 * <li>ICatalog
 * <li>String
 * </ul>
 * </p>
 * 
 * @author  Mifan Careem     mifanc@gmail.com
 * @since   1.2.0
 */
public class CatalogNGTreeView extends TreeViewer implements ISelectionChangedListener{
    
    private IMessageBoard messageBoard;

    /**
     * Construct <code>CatalogNGTreeView</code>.
     * 
     * @param parent
     */
    public CatalogNGTreeView( Composite parent ) {
        this(parent, true);
                                                                                
    }
    /**
     * Construct <code>CatalogNGTreeView</code>.
     * 
     * @param parent
     */
    public CatalogNGTreeView(Composite parent, boolean titles) {
        // TODO Auto-generated constructor stub
        this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, titles);
    }
    
    /**
     * Construct <code>CatalogNGTreeView</code>.
     * 
     * @param parent Parent component
     * @param style The other constructor uses SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
     * @todo    add custom sorter 
     */
    public CatalogNGTreeView( Composite parent, int style, boolean titles ) {
        super(parent, style|SWT.VIRTUAL);
        setContentProvider(new ResolveContentProvider());
        ResolveLabelProviderSimple resolveLabelProviderSimple = new ResolveLabelProviderSimple();
        if (titles) {
            setLabelProvider(new DecoratingLabelProvider(resolveLabelProviderSimple,
                    new ResolveTitlesDecorator(resolveLabelProviderSimple)));
        } else {
            setLabelProvider(resolveLabelProviderSimple);
        }
        
        setUseHashlookup(true);
        
        //set filtered input here to show only ServiceTypes etc depending on view
        setInput(CatalogServiceTypePlugin.getDefault().getLocalCatalog());
        
        //set custom sorter instead of CatalogViewerSorter
        setSorter(new CatalogViewerSorter());
        
        addSelectionChangedListener(this);
        
    }
    @Override
    public void selectionChanged( SelectionChangedEvent event ) {
        // TODO Auto-generated method stub
        if( messageBoard==null )
            return;
        
        ISelection selection = event.getSelection();
        if( selection instanceof IStructuredSelection ){
            IStructuredSelection sel=(IStructuredSelection) selection;
            if( sel.size()==1 ){
                Object obj=sel.getFirstElement();
                if (obj instanceof IResolve) {
                    IResolve resolve = (IResolve) obj;
                    if( resolve.getStatus()==Status.BROKEN ){
                        if (null == resolve.getMessage()) {
                            messageBoard.putMessage(Messages.CatalogTreeViewer_broken, IMessageBoard.Type.ERROR);
                        } else {
                            messageBoard.putMessage(resolve.getMessage().getLocalizedMessage(), IMessageBoard.Type.ERROR);   
                        }
                    }else if( resolve.getStatus()==Status.RESTRICTED_ACCESS ){
                        messageBoard.putMessage(Messages.CatalogTreeViewer_permission, IMessageBoard.Type.ERROR);
                    }else{
                        messageBoard.putMessage(null, IMessageBoard.Type.NORMAL);
                    }

                    
                }else{
                    messageBoard.putMessage(null, IMessageBoard.Type.NORMAL);
                }
            }else{
                messageBoard.putMessage(null, IMessageBoard.Type.NORMAL);
            }
        }else{
            messageBoard.putMessage(null, IMessageBoard.Type.NORMAL);
        }

        
    }
    
    /**
     * Sets the message board that this viewer will display status messages on. 
     * 
     * @param messageBoard
     */
    public void setMessageBoard( IMessageBoard messageBoard ){
        this.messageBoard=messageBoard;
    }
    

}
