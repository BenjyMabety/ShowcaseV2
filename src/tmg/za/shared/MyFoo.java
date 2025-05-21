package tmg.za.shared;

import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Widget;

public class MyFoo extends Widget {
	public interface MyStyle extends CssResource {
		String enabledBall();

		String enabledSpace();

		String disabled();
	}

	public MyFoo() {
	}

}
