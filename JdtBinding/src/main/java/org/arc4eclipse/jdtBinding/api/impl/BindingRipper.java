package org.arc4eclipse.jdtBinding.api.impl;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.arc4eclipse.jdtBinding.api.BindingRipperResult;
import org.arc4eclipse.jdtBinding.api.IBindingRipper;
import org.arc4eclipse.utilities.collections.Files;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

public class BindingRipper implements IBindingRipper {

	private final Map<IPath, String> cache = Maps.newMap();

	@Override
	public BindingRipperResult apply(IBinding from) {
		try {
			if (from != null) {
				IJavaElement javaElement = from.getJavaElement();
				if (javaElement != null) {
					final IPath path = javaElement.getPath();
					IJavaProject project = javaElement.getJavaProject();
					IPackageFragmentRoot root = project.findPackageFragmentRoot(path);
					IPath attachmentPath = root == null ? null : root.getSourceAttachmentPath();
					String digestAsHexString = Maps.findOrCreate(cache, path, new Callable<String>() {
						@Override
						public String call() throws Exception {
							File file = path.toFile();
							String result = Files.digestAsHexString(file);
							return result;
						}
					});

					if (javaElement instanceof IMethod) {
						String packageName = ((IMethodBinding) from).getDeclaringClass().getPackage().getName();
						String className = javaElement.getParent().getElementName();
						String methodName = javaElement.getElementName();
						return new BindingRipperResult(project, root, path, digestAsHexString, attachmentPath, packageName, className, methodName);
					} else if (javaElement instanceof IClassFile) {
						String packageName = ((ITypeBinding) from).getPackage().getName();
						String className = javaElement.getElementName();
						return new BindingRipperResult(project, root, path, digestAsHexString, attachmentPath, packageName, className, null);
					} else
						return new BindingRipperResult(project, root, path, digestAsHexString, attachmentPath, "", "", "");
				}
			}
			return BindingRipperResult.empty;
		} catch (JavaModelException e) {
			throw WrappedException.wrap(e);
		}
	}

}
