package com.stabilit.sc.util;

public abstract class LockAdapter<T> implements Lockable<T> {

	@Override
	public T run() throws Exception {
		return null;
	}

	@Override
	public T run(T obj) throws Exception {
		return null;
	}

	@Override
	public T run(T obj1, T obj2) throws Exception {
		return null;
	}

	@Override
	public T run(T obj1, T obj2, T obj3) throws Exception {
		return null;
	}

	@Override
	public T run(T... objects) throws Exception {
		return null;
	}

}
