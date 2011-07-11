package org.arc4eclipse.utilities.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.arc4eclipse.utilities.aggregators.IAggregator;
import org.arc4eclipse.utilities.exceptions.WrappedException;
import org.arc4eclipse.utilities.functions.IAggregateFunction;
import org.arc4eclipse.utilities.functions.IFoldFunction;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.codehaus.cake.forkjoin.ForkJoinPool;
import org.codehaus.cake.forkjoin.RecursiveTask;

public class SimpleLists {

	public static <T> ISimpleList<T> simpleList(final T... ts) {
		return new ISimpleList<T>() {
			public int size() {
				return ts.length;
			}

			public T get(int index) {
				return ts[index];
			}

		};

	}

	public static <T> ISimpleList<T> fromList(final List<T> list) {
		return new ISimpleList<T>() {
			public int size() {
				return list.size();
			}

			public T get(int index) {
				return list.get(index);
			}

		};
	}

	public static <From, To, Result> Future<IAggregator<To, Result>> mapAggregate(final int maxForOneThread, ForkJoinPool pool, final ISimpleList<From> from, //
			final IAggregator<To, Result> aggregator, final IFunction1<From, To> mapFunction) {

		class AggregateProcess extends RangeProcess<IAggregator<To, Result>> {
			public AggregateProcess(int low, int high) {
				super(low, high, maxForOneThread);
			}

			
			protected IAggregator<To, Result> combine(IAggregator<To, Result> left, IAggregator<To, Result> right) {
				assert left == right;
				return left;
			}

			
			protected RangeProcess<IAggregator<To, Result>> makeProcess(int low, int high) {
				return new AggregateProcess(low, high);
			}

			
			protected IAggregator<To, Result> doInRange() throws Exception {
				for (int i = low; i < high; i++)
					aggregator.add(mapFunction.apply(from.get(i)));
				return aggregator;
			}

		}
		AggregateProcess process = new AggregateProcess(0, from.size());
		return pool.submit(process);
	}

	public static <From, To, Result> Future<IAggregator<Result, Result>> mapTwoAggregators(final int maxForOneThread, ForkJoinPool pool, final ISimpleList<From> from, //
			final IAggregator<Result, Result> middleAggregator, final Callable<IAggregator<To, Result>> leafAggregator, final IFunction1<From, To> mapFunction) {

		class AggregateProcess extends RecursiveTask<IAggregator<Result, Result>> {
			private final int low;
			private final int high;

			public AggregateProcess(int low, int high) {
				this.low = low;
				this.high = high;
			}

			
			protected IAggregator<Result, Result> compute() {
				try {
					if (high - low <= maxForOneThread) {
						IAggregator<To, Result> aggregator = leafAggregator.call();
						for (int i = low; i < high; i++)
							aggregator.add(mapFunction.apply(from.get(i)));
						middleAggregator.add(aggregator.result());
						return middleAggregator;
					} else {
						int mid = low + (high - low) / 2;
						AggregateProcess left = new AggregateProcess(low, mid);
						AggregateProcess right = new AggregateProcess(mid, high);
						left.fork();
						right.compute();
						left.join();
						return middleAggregator;
					}
				} catch (Exception e) {
					throw WrappedException.wrap(e);
				}
			}

		}
		AggregateProcess process = new AggregateProcess(0, from.size());
		return pool.submit(process);
	}

	public static <From, To, Result> Future<Result> mapReduce(final int maxForOneThread, ForkJoinPool pool, final ISimpleList<From> from, //
			final IAggregateFunction<Result> aggregateFunction, final IFoldFunction<To, Result> foldFunction, final IFunction1<From, To> mapFunction, final Result initial) {
		class FoldRangeProcess extends RangeProcess<Result> {
			public FoldRangeProcess(int low, int high) {
				super(low, high, maxForOneThread);
			}

			
			protected Result combine(Result left, Result right) {
				return aggregateFunction.apply(left, right);
			}

			
			protected RangeProcess<Result> makeProcess(int low, int high) {
				return new FoldRangeProcess(low, high);
			}

			
			protected Result doInRange() throws Exception {
				Result value = initial;
				for (int i = low; i < high; i++) {
					From thisFrom = from.get(i);
					To thisTo = mapFunction.apply(thisFrom);
					value = foldFunction.apply(thisTo, value);
				}
				return value;
			}

		}
		FoldRangeProcess process = new FoldRangeProcess(0, from.size());
		return pool.submit(process);
	}

	public static <From, To> To foldInRange(ISimpleList<From> from, int low, int high, IFoldFunction<From, To> foldFunction, To initialValue) {
		To value = initialValue;
		for (int i = low; i < high; i++)
			value = foldFunction.apply(from.get(i), value);
		return value;
	}

	public static <T> Iterable<T> asIterable(final ISimpleList<T> simpleList) {
		return new AbstractFindNextIterable<T, AtomicInteger>() {
			
			protected T findNext(AtomicInteger context) throws Exception {
				int index = context.getAndIncrement();
				if (index >= simpleList.size())
					return null;
				return simpleList.get(index);
			}

			
			protected AtomicInteger reset() throws Exception {
				return new AtomicInteger();
			}
		};

	}

	public static <T> List<T> asList(ISimpleList<T> simpleList) {
		List<T> result = new ArrayList<T>();
		for (int i = 0; i < simpleList.size(); i++)
			result.add(simpleList.get(i));
		return result;
	}

}

abstract class RangeProcess<Result> extends RecursiveTask<Result> {
	int max;
	int low;
	int high;

	public RangeProcess(int low, int high, int max) {
		this.low = low;
		this.high = high;
		this.max = max;
	}

	
	protected Result compute() {
		try {
			if (high - low <= max) {
				return doInRange();
			} else {
				int mid = low + (high - low) / 2;
				RangeProcess<Result> left = makeProcess(low, mid);
				RangeProcess<Result> right = makeProcess(mid, high);
				left.fork();
				Result rightAns = right.compute();
				Result leftAns = left.join();
				return combine(leftAns, rightAns);
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	abstract protected Result combine(Result left, Result right) throws Exception;

	abstract protected RangeProcess<Result> makeProcess(int low, int high) throws Exception;

	abstract protected Result doInRange() throws Exception;

}