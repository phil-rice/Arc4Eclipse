package org.arc4eclipse.utilities.pooling;

public interface IPoolCleanTestCallback<T> {
	void setData(T item);

	void checkDataBlank(T item);

	void checkDataHasBeenSet(T item);
}
