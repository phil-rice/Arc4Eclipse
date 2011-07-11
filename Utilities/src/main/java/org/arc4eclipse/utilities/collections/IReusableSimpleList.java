package org.arc4eclipse.utilities.collections;


public interface IReusableSimpleList<T> extends ISimpleList<T>, IAddable<T> {

	T getAllowingOld(int i);

}
