package sfsecurity.util;


public interface Publisher<T> {
	public void publish(T message);
	public void addSubscriber(Subscriber<T> sub);
	public void removeSubscriber(Subscriber<T> sub);
}
