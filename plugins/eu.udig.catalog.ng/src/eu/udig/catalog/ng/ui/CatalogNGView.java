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




import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

import eu.udig.catalog.ng.CatalogNGTreeFilter;
import eu.udig.catalog.ng.internal.DataTypeElement;
import eu.udig.catalog.ng.internal.ServiceElement;
import eu.udig.catalog.ng.internal.ServiceTypeElement;
import eu.udig.catalog.ng.ui.internal.SelectionProviderWrapper;

/**
 * View for CatalogNG browse, a Java Browsing Perspective like browsing view for catalog, without the Perspective part 
 * <p>
 * The catalog browse, aka CatalongNG view will consist of the following view components.
 * <ul>
 * <li>Service Type</li>
 * <li>Service</li>
 * <li>Data Type | Feature Type</li>
 * <li>Layers</li>
 * </ul>
 * Components to listen to each other in a given order for changes
 * </p>
 * @author  Mifan Careem     mifanc@gmail.com
 * @since   1.2.0
 */
public class CatalogNGView extends CatalogNGViewPart implements ISelectionListener {
    
    public static final String VIEW_ID = "eu.udig.catalog.ng.ui.CatalongNGView"; //$NON-NLS-1$
    
    //Types of the views used for building unique trees
    public final String SERVICE_TYPE_ID = "ServiceTypeView"; //$NON-NLS-1$
    public final String SERVICE_ID = "ServiceView"; //$NON-NLS-1$
    public final String DATA_TYPE_ID = "DataTypeView"; //$NON-NLS-1$
    public final String LAYER_ID = "LayerView"; //$NON-NLS-1$
    
    private CatalogNGTreeView treeViewerServiceType,treeViewerService,treeViewerDataType,treeViewerLayers;
    private CatalogNGTreeFilter treeFilterServiceType,treeFilterService,treeFilterDataType,treeFilterLayers;
    private Composite serviceTypeComposite, serviceComposite, dataTypeComposite, layerComposite;
    private GridLayout stGrid, sGrid, dtGrid, lGrid;
    
    private String serviceTypeTxt = "Service Type";
    private String serviceTxt = "Service Groups";
    private String dataTypeTxt = "Services";
    private String layerTxt = "Layers";
    
    private String selectionValue = ""; //hold selection for service type
    private String persSelectionValue = ""; //hold selection for service
    private String dataTypeSelectionValue = ""; //hold selection for data type
    
    private SashForm splitter;
    private GridData gridData;
    
    private Action copyAction;
    private Clipboard clipBoard;
    
    private Display display;
    
    
    //Custom selection provider class to handle multiple providers within a viewpart
    private SelectionProviderWrapper selectionProviderWrapper = new SelectionProviderWrapper();
    


    /**
     * 
     */
    public CatalogNGView() {
        // TODO Auto-generated constructor stub
    }


    @Override
    /**
     * create tree components and handle selection provider on focus
     */
    public void createPartControl( Composite parent ) {
        // TODO Auto-generated method stub
        splitter = new SashForm(parent, SWT.HORIZONTAL);
        
       
        
        serviceTypeComposite = new Composite(splitter, SWT.SINGLE);
        stGrid = new GridLayout(1, false);
        stGrid.marginHeight = 0;
        stGrid.marginWidth = 0;
        serviceTypeComposite.setLayout(stGrid);
        Label stLabel = new Label(serviceTypeComposite, SWT.BOLD | SWT.CENTER);
        stLabel.setText(serviceTypeTxt);

        treeViewerServiceType = new CatalogNGTreeView(serviceTypeComposite, SERVICE_TYPE_ID);
        treeFilterServiceType = new CatalogNGTreeFilter();
        treeViewerServiceType.setInput(treeFilterServiceType.getInputTree(SERVICE_TYPE_ID,null,null, null));
        treeViewerServiceType.getTree().setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        treeViewerServiceType.getControl().addFocusListener(new FocusListener(){          
            @Override
            public void focusLost( FocusEvent e ) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void focusGained( FocusEvent e ) {
                // TODO Auto-generated method stub
                selectionProviderWrapper.setSelectionProvider(treeViewerServiceType);
            }
        });

        serviceComposite = new Composite(splitter, SWT.SINGLE);
        sGrid = new GridLayout(1, false);
        sGrid.marginHeight = 0;
        sGrid.marginWidth = 0;
        serviceComposite.setLayout(sGrid);
        Label sLabel = new Label(serviceComposite, SWT.BOLD | SWT.CENTER);
        sLabel.setText(serviceTxt);

        treeViewerService = new CatalogNGTreeView(serviceComposite, SERVICE_ID);
        treeFilterService = new CatalogNGTreeFilter();
        treeViewerService.setInput(treeFilterService.getInputTree(SERVICE_ID,null,null, null));
        treeViewerService.getTree().setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        treeViewerService.getControl().addFocusListener(new FocusListener(){
            @Override
            public void focusLost( FocusEvent e ) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void focusGained( FocusEvent e ) {
                // TODO Auto-generated method stub
                selectionProviderWrapper.setSelectionProvider(treeViewerService);
            }
        });
        
        dataTypeComposite = new Composite(splitter, SWT.SINGLE);
        dtGrid = new GridLayout(1, false);
        dtGrid.marginHeight = 0;
        dtGrid.marginWidth = 0;
        dataTypeComposite.setLayout(dtGrid);
        Label dtLabel = new Label(dataTypeComposite, SWT.BOLD | SWT.CENTER);
        dtLabel.setText(dataTypeTxt);
        treeViewerDataType = new CatalogNGTreeView(dataTypeComposite, DATA_TYPE_ID);
        treeFilterDataType = new CatalogNGTreeFilter();
        treeViewerDataType.setInput(treeFilterDataType.getInputTree(DATA_TYPE_ID, null, null, null));
        treeViewerDataType.getTree().setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        treeViewerDataType.getControl().addFocusListener(new FocusListener(){          
            @Override
            public void focusLost( FocusEvent e ) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void focusGained( FocusEvent e ) {
                // TODO Auto-generated method stub
                selectionProviderWrapper.setSelectionProvider(treeViewerDataType);
            }
        });
        
        layerComposite = new Composite(splitter, SWT.SINGLE);
        lGrid = new GridLayout(1, false);
        lGrid.marginHeight = 0;
        lGrid.marginWidth = 0;
        layerComposite.setLayout(lGrid);
        Label lLabel = new Label(layerComposite, SWT.BOLD | SWT.CENTER);
        lLabel.setText(layerTxt);
        treeViewerLayers = new CatalogNGTreeView(layerComposite, LAYER_ID);
        treeFilterLayers = new CatalogNGTreeFilter();
        treeViewerLayers.setInput(treeFilterLayers.getInputTree(LAYER_ID, null, null, null));
        treeViewerLayers.getTree().setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
        
        
        
        //catalog sync listener
        CatalogPlugin.addListener(catalogSyncListener);
        
        //cannot have 2 selection providers within a viewpart. Calling custom Selection Provider for view
        getSite().setSelectionProvider(selectionProviderWrapper);        
        
        //Handle different components at selection listener
        getViewSite().getPage().addSelectionListener(this);

        FillLayout layout = new FillLayout();
        //parent.setLayout(layout);
        splitter.setLayout(layout);
        
        createMenu();
        
        createContextMenu(parent);
        
        
        super.createPartControl(parent);
    }
    
    /**
     * Menu item to add Copy to Clipboard capability
     * This allows for copying the layer name in the layer viewer to the clipboard
     * e.g: Copy the path of the filename
     * @param   parentWidget  Composite parent for display
     */
    private void createContextMenu(Composite parentWidget){
        final MenuManager contextMenu = new MenuManager();
        display = parentWidget.getDisplay();
        copyAction = new Action("Copy to Clipboard"){ //$NON-NLS-1$
            public void run() {
              IStructuredSelection sel = (IStructuredSelection) treeViewerLayers.getSelection();
                sel.getFirstElement();
                String textData = (String)sel.getFirstElement();
                TextTransfer textTransfer = TextTransfer.getInstance();
                clipBoard = new Clipboard(display);
                clipBoard.setContents(new Object[]{textData}, new Transfer[]{textTransfer});
            }
        };
        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener(){

            public void menuAboutToShow( IMenuManager mgr ) {
                contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                contextMenu.add(new Separator());
                contextMenu.add(copyAction);

                // contextMenu.add(refreshAction);
                IWorkbenchWindow window = getSite().getWorkbenchWindow();
                IAction action = ActionFactory.IMPORT.create(window);

            }

        });

        // Create menu.
        Menu menu = contextMenu.createContextMenu(treeViewerLayers.getControl());
        treeViewerLayers.getControl().setMenu(menu);

        // Register menu for extension.
        getSite().registerContextMenu(contextMenu, treeViewerLayers);

        
        
    }
    
    /**
     * Create menu with refresh option.
     */
    private void createMenu() {
        IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
        //mgr.add(saveAction);
        //mgr.add(loadAction);
    }


    @Override
    public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
        // TODO Auto-generated method stub
        if( selection instanceof IStructuredSelection){
            Object selected = ((IStructuredSelection) selection).getFirstElement();
            
            if ( selected instanceof ServiceTypeElement){
                selectionValue = ((ServiceTypeElement)selected).getServiceTypeName();
                System.out.println("SELECTION: "+selectionValue);
                treeViewerService.setInput(treeFilterService.getInputTree(SERVICE_ID,selectionValue,null,null));
            }
            
            else if ( selected instanceof ServiceElement){
                selectionValue = ((ServiceElement)selected).getServiceName();
                persSelectionValue = ((ServiceElement)selected).getServiceTypeName();
                System.out.println("SELECTION SERVICE: "+selectionValue+"-"+persSelectionValue);
                treeViewerDataType.setInput(treeFilterDataType.getInputTree(DATA_TYPE_ID,selectionValue,persSelectionValue,null));
            }
            
            else if ( selected instanceof DataTypeElement){
                selectionValue = ((DataTypeElement)selected).getServiceName();
                persSelectionValue = ((DataTypeElement)selected).getServiceTypeName();
                dataTypeSelectionValue = ((DataTypeElement)selected).getDataTypeName();
                System.out.println("SELECTION DATA: "+selectionValue+"-"+persSelectionValue+"-"+dataTypeSelectionValue);
                treeViewerLayers.setInput(treeFilterLayers.getInputTree(LAYER_ID,selectionValue,persSelectionValue,dataTypeSelectionValue));
            }
        }
    }
    
    /**
     * Listener to listen to catalog change events and update view components(s)
     */
    IResolveChangeListener catalogSyncListener = new IResolveChangeListener(){
        
        @Override
        public void changed( IResolveChangeEvent event ) {
            // TODO Auto-generated method stub
            System.out.println("catalog change "+event.getType());
            IResolve resolve = event.getResolve();
            
        }
    };

}
