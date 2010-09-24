package name.nanek.gdwprototype.shared.model.support;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class StringUtil {

	public static boolean nullOrEmpty(String testValue) {
		return null == testValue || "".equals(testValue.trim());
	}

	public static String escapeHtml(String maybeHtml) {
		final Element div = DOM.createDiv();
		DOM.setInnerText(div, maybeHtml);
		return DOM.getInnerHTML(div);
	}
	
}
