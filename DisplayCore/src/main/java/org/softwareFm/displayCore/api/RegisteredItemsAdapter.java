package org.softwareFm.displayCore.api;

import org.softwareFm.repository.api.IUrlGenerator;

public class RegisteredItemsAdapter implements IRegisteredItems {

	@Override
	public IValidator getValidator(String validatorName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IEditor getEditor(String editorName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ILineEditor<T> getLineEditor(String lineEditorName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IDisplayer<?, ?> getDisplayer(String displayName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IUrlGenerator getUrlGenerator(String entityName) {
		throw new UnsupportedOperationException();
	}

}
