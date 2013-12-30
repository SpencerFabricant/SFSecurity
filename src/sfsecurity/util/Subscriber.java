package sfsecurity.util;

public interface Subscriber<T> {
	public abstract void update(T message);
}
