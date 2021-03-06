package org.softwareFm.utilities.tests;

import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.collections.Iterables;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.reflection.Classes;
import org.softwareFm.utilities.reflection.IClassAcceptor;

public class Tests {

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> E assertThrows(Class<E> class1, Runnable runnable) {
		try {
			runnable.run();
			Assert.fail(class1.getSimpleName() + " not thrown");
		} catch (Throwable e) {
			if (class1.isAssignableFrom(e.getClass()))
				return (E) e;
			Assert.fail(e.toString());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Throwable> E assertThrows(boolean expected, Class<E> clazz, Runnable runnable) {
		try {
			runnable.run();
			if (!expected)
				Assert.fail();
			return null;
		} catch (Throwable e) {
			if (e.getClass() == clazz)
				if (expected)
					Assert.fail();
				else
					return (E) e;
			throw WrappedException.wrap(e);
		}
	}

	public static TestSuite makeSuiteUnder(Class<?> marker, File root, IClassAcceptor acceptor) {
		Class<?>[] array = Iterables.list(testsUnder(root, acceptor)).toArray(new Class[0]);
		TestSuite suite = new TestSuite(array);
		return suite;
	}

	public static Iterable<Class<?>> testsUnder(File root, IClassAcceptor acceptor) {
		Iterable<File> children = Files.walkChildrenOf(root, Files.extensionFilter("class"));
		// System.out.println(Iterables.list(children));
		// System.out.println();
		Iterable<Class<?>> classes = Iterables.map(children, Classes.asClass(acceptor));
		// System.out.println(Iterables.list(classes));
		Iterable<Class<?>> result = Iterables.remove(classes, Functions.<Class<?>> isNull());
		// System.out.println();
		// System.out.println(Iterables.list(result));
		return result;
	}

	public static void main(String[] args) {
		for (Class<?> clazz : testsUnder(new File(".."), IClassAcceptor.Utils.isTest()))
			System.out.println(clazz.getName());
	}

	public static void executeTest(Class<? extends TestCase> class1) {
		TestSuite suite = new TestSuite("Tests");
		suite.addTestSuite(class1);
		new TestRunner().doRun(suite);
	}
}
