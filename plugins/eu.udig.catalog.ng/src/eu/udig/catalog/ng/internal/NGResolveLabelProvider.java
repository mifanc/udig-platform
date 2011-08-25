package eu.udig.catalog.ng.internal;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IProcess;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.catalog.IResolveFolder;
import net.refractions.udig.catalog.ISearch;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class NGResolveLabelProvider extends LabelProvider implements IResolveChangeListener {
    
    public NGResolveLabelProvider(){
        
    }

    public void changed( final IResolveChangeEvent event ) {
        if (event.getType() != IResolveChangeEvent.Type.POST_CHANGE)
            return;

        final IResolve resolve = event.getResolve();
        if (resolve == null)
            return;

        Display.getDefault().asyncExec(new Runnable(){
            public void run() {
                fireLabelProviderChanged(new LabelProviderChangedEvent(
                        NGResolveLabelProvider.this, resolve));
            }
        });
        
    }
    

    /**
     * Generate text from the resolve.getURI()
     * <p>
     * Note this name is only used as a first try, the ResolveLabelDecorator is expected to provide
     * a label based on Name or Title information.
     * </p>
     * 
     * @param element
     * @return label based on IResolve.getIdentifier
     */
    public String getText( Object element ) {

        if (element instanceof IResolve) {
            IResolve resolve = (IResolve) element;
            try {
                
                if (resolve instanceof IGeoResource) {
                    IGeoResource resource = (IGeoResource) resolve;
                    String title = resource.getTitle();
// This provider should be non-blocking                    
//                    if (title == null) {
//                        IGeoResourceInfo info = resource.getInfo(new NullProgressMonitor());
//                        if(info != null) {
//                          title = info.getTitle();
//                        }
//                    }
                    ID id = resource.getID();
                    if(title == null) {
                        title = id.labelResource();
                    }
                    return title;

                } else if (resolve instanceof IService) {
                    IService service = (IService) resolve;
                    ID id = service.getID();

                    String title = service.getTitle();
                    if (title == null) {
                        IServiceInfo info = service.getInfo(new NullProgressMonitor());
                        if (info != null) {
                            title = info.getTitle();
                        }
                    }
                    if (title == null) {
                        // we are going to fake something here
                        String name = id.toString();
                        name = name.replace('_', ' ');
                        name = name.replace("%20", " "); //$NON-NLS-1$ //$NON-NLS-2$
                        return name;
                    }
                    if (id.getTypeQualifier() != null) {
                        return title + "(" + id.getTypeQualifier() + ")";
                    } else {
                        return title;
                    }
                }  else {
                    return resolve.getTitle();
                }
            }
            catch (Exception e) {
                // TODO: handle exception
            }
        }        
        return super.getText(element);
        
    }

    /**
     * Obtain image for the provided element.
     * <p>
     * To accomplish this quickly we simply make use of constants from CatalogUIPlugin. We need a
     * second pass that makes use of the real icon from the real resource.
     * </p>
     * 
     * @param element is expected to be IResolve
     * @return the image used to label the element, or <code>null</code> if there is no image for
     *         the given object
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object element ) {
        if (element instanceof IResolve) {
            return CatalogUIPlugin.image((IResolve) element);
        }
        return super.getImage(element);
    }


}
