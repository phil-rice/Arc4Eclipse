package org.arc4eclipse.displayCore.api;

import java.util.Collections;

import org.arc4eclipse.swtBasics.images.Resources;
import org.arc4eclipse.swtBasics.text.TitleAndTextField;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class BoundTitleAndTextField extends TitleAndTextField {

	private String url;

	public BoundTitleAndTextField(Composite parent, final DisplayerContext displayerContext, final DisplayerDetails displayerDetails) {
		super(displayerContext.configForTitleAnd, parent, Resources.getTitle(displayerContext.resourceGetter, displayerDetails.key));
		addCrListener(new Listener() {
			@Override
			public void handleEvent(Event e) {
				displayerContext.repository.modifyData(displayerDetails.entity, url, displayerDetails.key, getText(), Collections.<String, Object> emptyMap());
			}
		});
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
