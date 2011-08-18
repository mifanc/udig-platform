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
 * TODO Purpose of 
 * <p>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * @author nazgul
 * @since 1.2.0
 */
public class SelectionProviderWrapper implements ISelectionProvider {
    private ISelectionProvider provider;
    private List selectionListeners;
    private ISelection sel = StructuredSelection.EMPTY;
    
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        if (selectionListeners == null) selectionListeners = new ArrayList(1);
        selectionListeners.add(listener);
        if (provider != null) provider.addSelectionChangedListener(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        if (selectionListeners != null){
            selectionListeners.remove(listener);
            if (provider != null) provider.removeSelectionChangedListener(listener);
        }
    }
    
    public ISelection getSelection() {
        return provider != null ? provider.getSelection() : sel;
    }

    public void setSelection(ISelection selection) {
        if (provider != null){
            provider.setSelection(selection);
        }else{
            sel = selection;
            if (selectionListeners != null){
                SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
                for (Iterator it=selectionListeners.iterator(); it.hasNext();){
                    ((ISelectionChangedListener)it.next()).selectionChanged(event);
                }
            }

        }
    }
    
    public void setSelectionProvider(ISelectionProvider provider){
        if (this.provider != provider){
            ISelection _sel = null;
            if (selectionListeners != null){
                int c = selectionListeners.size();
                ISelectionChangedListener listener;
                if (this.provider != null){
                    for (int i=0; i<c; i++){
                        listener = (ISelectionChangedListener) selectionListeners.get(i);
                        this.provider.removeSelectionChangedListener(listener);
                    }                    
                }
                
                if (provider != null){
                    for (int i=0; i<c; i++){
                        listener = (ISelectionChangedListener) selectionListeners.get(i);
                        provider.addSelectionChangedListener(listener);
                    }
                    
                    _sel = provider.getSelection();
                }else{
                    _sel = sel;
                }
            }
            this.provider = provider;
            if (_sel != null){
                //force a selection change event propagation
                setSelection(_sel);
            }
        }
    }
    
    public ISelectionProvider getSelectionProvider(){
        return provider;
    }



}
