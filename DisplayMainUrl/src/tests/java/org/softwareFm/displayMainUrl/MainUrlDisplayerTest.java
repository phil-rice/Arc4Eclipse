package org.softwareFm.displayMainUrl;

import org.eclipse.swt.widgets.Control;
import org.softwareFm.core.plugin.AbstractDisplayerTest;
import org.softwareFm.displayCore.api.IDisplayer;

public class MainUrlDisplayerTest extends AbstractDisplayerTest<MainUrlPanel, Control> {

	@Override
	protected String getDataKey() {
		return "Key1";
	}

	@Override
	protected String getDisplayerKey() {
		return "mainUrl";
	}

	@Override
	protected Object getSampleData(String key) {
		return "data for " + key;
	}

	@Override
	protected IDisplayer<MainUrlPanel, Control> getDisplayer() {
		return new MainUrlDisplayer();
	}

	@Override
	protected void checkData(Object sampleData, MainUrlPanel largeControl, Control smallControl) {
		assertEquals(sampleData, largeControl.getText());
	}

	@Override
	protected String getSmallImageKey() {
		return "Clear";
	}

}
