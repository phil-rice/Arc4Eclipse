package org.softwareFm.jdtBinding.mocks;

import org.eclipse.jdt.core.dom.IBinding;

public interface IBindingBuilder extends IBinding {

	IBindingBuilder withClass(String className);

	IBindingBuilder child();

	IBindingBuilder withMethod(String string);

	IBindingBuilder withLocalVariable(String string);

	IBindingBuilder withInterface(String string);

	IBindingBuilder withPackage(String string);

	public class Utils {

		public static IBindingBuilder parent(String path, Class<?> markerClass) {
			return new BindingBuilder(path, markerClass);
		}

	}

}
