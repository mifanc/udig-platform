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
package eu.udig.catalog.ng.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * Custom selection selectionProvider to hold and delegate selection selectionProvider events for CatlaogNG view with multiple components 
 * @author Mifan Careem mifanc@gmail.com
 * @inspired by
 * @since 1.2.0
 */
public class SelectionProviderWrapper implements ISelectionProvider {
    private ISelectionProvider selectionProvider;
    private List selectionListeners;
    private ISelection sel = StructuredSelection.EMPTY;
    
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        if (selectionListeners == null) 
            selectionListeners = new ArrayList(1);
        selectionListeners.add(listener);
        if (selectionProvider != null) 
            selectionProvider.addSelectionChangedListener(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        if (selectionListeners != null){
            selectionListeners.remove(listener);
            if (selectionProvider != null) 
                selectionProvider.removeSelectionChangedListener(listener);
        }
    }
    
    public ISelection getSelection() {
        return selectionProvider != null ? selectionProvider.getSelection() : sel;
    }

    public void setSelection(ISelection selection) {
        if (selectionProvider != null){
            selectionProvider.setSelection(selection);
        }
        else{
            sel = selection;
            if (selectionListeners != null){
                SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
                for (Iterator it=selectionListeners.iterator(); it.hasNext();){
                    ((ISelectionChangedListener)it.next()).selectionChanged(event);
                }
            }

        }
    }
    
    public void setSelectionProvider(ISelectionProvider selectionProvider){
        if (this.selectionProvider != selectionProvider){
            ISelection _sel = null;
            if (selectionListeners != null){
                int c = selectionListeners.size();
                ISelectionChangedListener listener;
                if (this.selectionProvider != null){
                    for (int i=0; i<c; i++){
                        listener = (ISelectionChangedListener) selectionListeners.get(i);
                        this.selectionProvider.removeSelectionChangedListener(listener);
                    }                    
                }
                
                if (selectionProvider != null){
                    for (int i=0; i<c; i++){
                        listener = (ISelectionChangedListener) selectionListeners.get(i);
                        selectionProvider.addSelectionChangedListener(listener);
                    }
                    
                    _sel = selectionProvider.getSelection();
                }else{
                    _sel = sel;
                }
            }
            this.selectionProvider = selectionProvider;
            if (_sel != null){
                //force a selection change event propagation
                setSelection(_sel);
            }
        }
    }
    
    public ISelectionProvider getSelectionProvider(){
        return selectionProvider;
    }



}
