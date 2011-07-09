package net.refractions.udig.catalog.ui.browse;

import java.io.IOException;

import java.util.Iterator;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.ui.IHelpContextIds;
import net.refractions.udig.catalog.internal.ui.ImageConstants;
import net.refractions.udig.catalog.ui.CatalogTreeViewer;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.StatusLineMessageBoardAdapter;
import net.refractions.udig.catalog.ui.internal.Messages;
import net.refractions.udig.internal.ui.IDropTargetProvider;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.ProgressManager;
import net.refractions.udig.ui.UDIGDragDropUtilities;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.prefs.BackingStoreException;



public class ServiceTypeView extends ViewPart implements ISetSelectionTarget, IDropTargetProvider{
    public static final String VIEW_ID = "net.refractions.udig.catalog.ui.browse.ServiceTypeView"; //$NON-NLS-1$

    CatalogTreeViewer treeviewer;

    Action removeAction; // addAction

    private Action saveAction;
    private Action loadAction;

    private Action propertiesAction;

    public ServiceTypeView() {
        // TODO Auto-generated constructor stub
    }

    public void createPartControl( Composite parent ) {
        // create viewer
//        treeviewer = new CatalogTreeViewer(parent, false);
        treeviewer = new CatalogTreeViewer(parent, true);
        treeviewer.setMessageBoard(new StatusLineMessageBoardAdapter(getViewSite().getActionBars().getStatusLineManager()));

        UDIGDragDropUtilities.addDragDropSupport(treeviewer, this);

        getSite().setSelectionProvider(treeviewer);
        // Create menu and toolbars
        createActions();
        createMenu();
        createToolbar();
        createContextMenu();
        hookGlobalActions();

        // restore state (from previous session)

    }

    /**
     * We need to hook up to a few global actions such as Properties and Delete.
     * <ul>
     * <li>
     */
    protected void hookGlobalActions(){
        getViewSite().getActionBars().setGlobalActionHandler(
                IWorkbenchActionConstants.PROPERTIES,
                propertiesAction );
    }
    
    private void createContextMenu() {
        final MenuManager contextMenu = new MenuManager();

        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener(){

            public void menuAboutToShow( IMenuManager mgr ) {
                contextMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                contextMenu.add(new Separator());
                contextMenu.add(removeAction);
                IWorkbenchWindow window = getSite().getWorkbenchWindow();
                IAction action = ActionFactory.IMPORT.create(window);
                contextMenu.add(action);
                contextMenu.add(new Separator());
                contextMenu.add(UiPlugin.getDefault().getOperationMenuFactory().getContextMenu(treeviewer.getSelection()));
                contextMenu.add(new Separator());
                contextMenu.add(ActionFactory.EXPORT.create(getSite().getWorkbenchWindow()));
            }

        });

        // Create menu.
        Menu menu = contextMenu.createContextMenu(treeviewer.getControl());
        treeviewer.getControl().setMenu(menu);

        // Register menu for extension.
        getSite().registerContextMenu(contextMenu, treeviewer);

    }

    /**
     * Create a few actions such as add, remove, properties and so on.
     * <p>
     * These properties will be registered in our view menu, as global handlers
     * and so forth.
     * </p>
     */
    private void createActions() {
        propertiesAction  =
            new PropertyDialogAction( getViewSite().getWorkbenchWindow(), treeviewer );
                   
        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), propertiesAction);        
        getSite().getKeyBindingService().registerAction(propertiesAction);
        
        removeAction = new Action(){
            public void run() {
                IStructuredSelection selected = (IStructuredSelection) treeviewer.getSelection();
                removeSelected( selected );
            }
        };
        
        Messages.initAction(removeAction, "action_remove"); //$NON-NLS-1$
        removeAction.setEnabled(false);
        removeAction.setImageDescriptor(CatalogUIPlugin.getDefault().getImageDescriptor(ImageConstants.REMOVE_CO));
        removeAction.setActionDefinitionId("org.eclipse.ui.edit.delete"); //$NON-NLS-1$
        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), removeAction);
        getSite().getKeyBindingService().registerAction(removeAction);
        
        PlatformUI.getWorkbench().getHelpSystem().setHelp(removeAction,
                IHelpContextIds.REMOVE_SERVICE_ACTION);

        saveAction = new Action(Messages.CatalogView_save_label){ 
            public void run() {
                try {
                    CatalogPlugin.getDefault().storeToPreferences(ProgressManager.instance().get());
                } catch (BackingStoreException e) {
                    CatalogPlugin.log(null, e);
                } catch (IOException e) {
                    CatalogPlugin.log(null, e);
                }
            }
        };

        loadAction = new Action(Messages.CatalogView_load_label){ 
            public void run() {
                try {
                    CatalogPlugin.getDefault().restoreFromPreferences();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Add selection listener.
        treeviewer.addSelectionChangedListener(new ISelectionChangedListener(){
            public void selectionChanged( SelectionChangedEvent event ) {
                updateActionEnablement();
            }
        });
    }

    void registerDatasource() {
        // Call to wizard here...
    }

    protected void showProperties( IStructuredSelection selected ){
        if( selected.isEmpty() ) return; // action should of been disabled!        
        Object content = selected.getFirstElement();
        
        for( Iterator iter = selected.iterator(); iter.hasNext(); ) {
            @SuppressWarnings("all")//$NON-NLS-1$
            Object o = iter.next();
            if (o instanceof IService)
                remove((IService) o);
            else if (o instanceof IGeoResource)
                remove((IGeoResource) o);
        }
    }
    
    /**
     * Remove selected stuff from the catalog.
     * <p>
     * Please note that this just smacks the Catalog; any Maps or Pages holding
     * references to this Service will just be confused. The even is sent out
     * but chances are they may just recreate this Service from scratch next
     * time they are opened.
     * </p>
     * So if this Service was in use chances are it will just pop back in again.
     * @see remove( IService )
     * @see remove( IGeoResource )
     */
    protected void removeSelected( IStructuredSelection selected ) {
        // Free selected data source - but only if it is not
        // in use...
        
        for( Iterator iter = selected.iterator(); iter.hasNext(); ) {
            @SuppressWarnings("all")//$NON-NLS-1$
            Object o = iter.next();
            if (o instanceof IService)
                remove((IService) o);
            else if (o instanceof IGeoResource)
                remove((IGeoResource) o);
        }
    }

    /**
     * Straight call of CatalogPlugin.getDefault().getLocalCatalog().remove( service )
     * @param service
     */
    private void remove( IService service ) {
        CatalogPlugin.getDefault().getLocalCatalog().remove(service);
    }

    /**
     * Will remove the service of the selected resource.
     * <p>
     * We may try doing something more smart here on a service by service
     * basis.
     * @param georesource
     */
    private void remove( IGeoResource georesource ) {
        try {
            remove(georesource.service(null));
        } catch (IOException e) {
            CatalogUIPlugin.log(null, e);
        }
    }

    void updateActionEnablement() {
        IStructuredSelection sel = (IStructuredSelection) treeviewer.getSelection();
        if( sel.size() == 0 ){
            removeAction.setEnabled(false);
            propertiesAction.setEnabled(false);
        }
        else {
            removeAction.setEnabled(true);
            propertiesAction.setEnabled(true);
        }
    }

    /**
     * Create menu with refresh option.
     */
    private void createMenu() {
        IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
        mgr.add(saveAction);
        mgr.add(loadAction);
    }

    /**
     * Create toolbar with new and delete buttons.
     */
    private void createToolbar() {
        IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
        // mgr.add(addAction);

        IWorkbenchWindow window = getSite().getWorkbenchWindow();
        IAction action = ActionFactory.IMPORT.create(window);

        action.setImageDescriptor(CatalogUIPlugin.getDefault()
                .getImageDescriptor(ImageConstants.PATH_ETOOL + "import_wiz.gif")); //$NON-NLS-1$
        mgr.add(action);

        mgr.add(removeAction);
    }

    /**
     * Asks this view take focus within the workbench.
     * <p>
     * From IWorkbenchPart: Clients should not call this method (the workbench calls this method at
     * appropriate times). To have the workbench activate a part, use
     * <code>IWorkbenchPage.activate(IWorkbenchPart) instead</code>.
     * </p>
     * <p>
     * Used to set the focus to the appropriate control, for us that is the treeviewer. But if we
     * were smart we could send the user off to a search field or something they actually need (like
     * a broken datastore) based on context.
     * </p>
     */
    public void setFocus() {
        treeviewer.getControl().setFocus();
    }
    /**
     * @return Returns the treeviewer.
     */
    public CatalogTreeViewer getTreeviewer() {
        return treeviewer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.ISetSelectionTarget#selectReveal(org.eclipse.jface.viewers.ISelection)
     */
    public void selectReveal( ISelection selection ) {
        treeviewer.setSelection(selection, true);
    }

    public Object getTarget(DropTargetEvent event) {
        return this;
    }

}
