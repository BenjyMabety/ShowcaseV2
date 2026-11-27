package tmg.za.shared;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Widget;

/**
 * Style class
 */
public class MyFoo extends Widget {
	public interface MyStyle extends CssResource {
		String enabledBall();

		String enabledSpace();

		String disabled();
	}

	/**
	 * No arg nor body constructor
	 */
	public MyFoo() {
	}

}
