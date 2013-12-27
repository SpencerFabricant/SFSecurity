package sfsecurity.util;

public interface Subscriber<T> {
	public abstract void notify(T message);
}
