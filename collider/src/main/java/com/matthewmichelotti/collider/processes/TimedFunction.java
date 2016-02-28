package com.matthewmichelotti.collider.processes;

import java.util.concurrent.atomic.AtomicLong;

public abstract class TimedFunction implements Comparable<TimedFunction> {
	private final static AtomicLong NEXT_ID = new AtomicLong();

	private final double time;
	private final long id = NEXT_ID.incrementAndGet();

	protected TimedFunction(double time) {
		this.time = time;
	}

	public final double getTime() {
		return time;
	}

	public abstract void invoke();

	@Override
	public final int compareTo(TimedFunction other) {
		int result = Double.compare(time, other.time);
		if(result == 0) result = Long.compare(id, other.id);
		return result;
	}
}
