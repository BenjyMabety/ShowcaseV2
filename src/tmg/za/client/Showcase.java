package tmg.za.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Showcase implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get();

		MainLayout layout = new MainLayout();
		root.add(layout);
		root.setWidgetLeftRight(layout, 0, Unit.PX, 0, Unit.PX);

		FlowPanel footer = new FlowPanel();
		footer.getElement().setId("showcaseFooter");
		footer.getElement().getStyle().setProperty("backgroundColor", "rgba(15, 18, 24, 0.92)");
		footer.getElement().getStyle().setProperty("color", "#e0e6ed");
		footer.getElement().getStyle().setProperty("fontFamily", "sans-serif");
		footer.getElement().getStyle().setProperty("fontSize", "12px");
		footer.getElement().getStyle().setProperty("lineHeight", "28px");
		footer.getElement().getStyle().setProperty("textAlign", "center");
		footer.getElement().getStyle().setProperty("borderTop", "1px solid rgba(255, 255, 255, 0.15)");
		footer.getElement().getStyle().setProperty("whiteSpace", "nowrap");
		footer.getElement().getStyle().setProperty("overflow", "hidden");
		footer.getElement().getStyle().setProperty("textOverflow", "ellipsis");

		InlineLabel credit = new InlineLabel("Developed by Ben Mbete, source: ");
		Anchor sourceLink = new Anchor("https://github.com/BenjyMabety/ShowcaseV2/", false,
				"https://github.com/BenjyMabety/ShowcaseV2/");
		sourceLink.getElement().setAttribute("target", "_blank");
		sourceLink.getElement().setAttribute("rel", "noopener noreferrer");
		sourceLink.getElement().getStyle().setProperty("color", "#64b5f6");
		sourceLink.getElement().getStyle().setProperty("textDecoration", "none");

		footer.add(credit);
		footer.add(sourceLink);

		root.add(footer);
		root.setWidgetLeftRight(footer, 0, Unit.PX, 0, Unit.PX);
		root.setWidgetBottomHeight(footer, 0, Unit.PX, 28, Unit.PX);

	}
}