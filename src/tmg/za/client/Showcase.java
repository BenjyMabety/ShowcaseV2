package tmg.za.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Showcase implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		MainLayout layout = new MainLayout();

		RootLayoutPanel.get().add(layout);
	}
}
