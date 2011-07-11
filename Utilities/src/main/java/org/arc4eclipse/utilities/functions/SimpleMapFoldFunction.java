package org.arc4eclipse.utilities.functions;

import org.arc4eclipse.utilities.maps.ISimpleMap;
import org.arc4eclipse.utilities.maps.SimpleMaps;

public class SimpleMapFoldFunction<K, V> implements ISymmetricFunction<ISimpleMap<K, V>> {

	@SuppressWarnings("unchecked")
	public ISimpleMap<K, V> apply(ISimpleMap<K, V> value, ISimpleMap<K, V> initial) {
		return SimpleMaps.fromMap(SimpleMaps.<K, V> merge(value, initial));
	}
}
