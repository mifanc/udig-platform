package eu.udig.catalog.ng;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CatalogNGPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "eu.udig.catalog.ng"; //$NON-NLS-1$

	// The shared instance
	private static CatalogNGPlugin plugin;
	
	/**
	 * The constructor
	 */
	public CatalogNGPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CatalogNGPlugin getDefault() {
		return plugin;
	}

}
