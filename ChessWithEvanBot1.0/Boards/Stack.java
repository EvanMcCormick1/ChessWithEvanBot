package Boards;

import java.util.ArrayList;


public class Stack<T> extends ArrayList<T>{

	public T peek() {
		if (this.isEmpty()) {
			return null;
		}
		return this.get(this.size() - 1);
	}
	
	public T pop() {
		if (this.isEmpty()) {
			return null;
		}
		T top = this.get(this.size() - 1);
		this.remove(this.size() - 1);
		return top;
	}
	public void push(T element) {
		this.add(element);
	}
	public int size() {
		return super.size();
	}
	public boolean isEmpty() {
		return super.isEmpty();
	}
}
