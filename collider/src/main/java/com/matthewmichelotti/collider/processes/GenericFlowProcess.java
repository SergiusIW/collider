package com.matthewmichelotti.collider.processes;

import java.util.TreeSet;

public final class GenericFlowProcess implements FlowProcess {
	private final TreeSet<TimedFunction> queue = new TreeSet<>();
	private double time = 0.0;

	public GenericFlowProcess() {}

	public boolean add(TimedFunction function) {
		return queue.add(function);
	}

	public boolean remove(TimedFunction function) {
		return queue.remove(function);
	}

	public double getTime() {
		return time;
	}

	@Override
	public double peekNextTime() {
		if(queue.isEmpty()) return Double.POSITIVE_INFINITY;
		else return Math.max(time, queue.first().getTime());
	}

	@Override
	public void advance(double newTime) {
		if(newTime < time) throw new IllegalStateException();
		if(newTime > peekNextTime()) throw new IllegalStateException();
		time = newTime;
	}

	@Override
	public void resolveNext() {
		if(queue.first().getTime() != time) throw new IllegalStateException();
		queue.pollFirst().invoke();
	}
}
