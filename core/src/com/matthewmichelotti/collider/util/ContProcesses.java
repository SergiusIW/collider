/*
 * Copyright 2013-2014 Matthew D. Michelotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.matthewmichelotti.collider.util;

import java.util.ArrayList;

/**
 * Couples multiple {@link ContProcess} objects together
 * to handle their respective events in chronological order.
 *
 * @author Matthew Michelotti
 */
public class ContProcesses {
	private ArrayList<ContProcess> processes = new ArrayList<ContProcess>();
	private double time = 0.0;

	/**
	 * Constructs a new ContProcesses object.  Time is initialized to zero.
	 */
	public ContProcesses() {}

	/**
	 * Adds a new continuous-time process.  The {@link ContProcess#stepToTime(double)} method
	 * is called to advance the process to the current time of this object.  No events should be scheduled
	 * before this time.
	 * @param process The new continuous-time process to add.
	 * @return True iff the process was not already in this collection.
	 */
	public boolean addProcess(ContProcess process) {
		if(processes.indexOf(process) >= 0) return false;
		double nextEventTime = process.peekNextEventTime();
		if(nextEventTime < time) throw new RuntimeException("process event time has already passed");
		process.stepToTime(time);
		processes.add(process);
		return true;
	}

	/**
	 * Removes a process.  Methods of the given process will no longer be invoked.
	 * @param process The process to remove.
	 * @return True iff the process was found.
	 */
	public boolean removeProcess(ContProcess process) {
		return processes.remove(process);
	}

	/**
	 * Returns the current time of the processes.
	 * @return The current time.
	 */
	public double getTime() {
		return time;
	}

	/**
	 * Advances all processes to the given time, resolving all events along the way.
	 * All processes will share the same time whenever an event is resolved.
	 * If events from two processes occur at the same time, the event for
	 * the process that was added first will be resolved first.
	 * @param newTime Time to advance the processes to.
	 */
	public void stepToTime(double newTime) {
		if(newTime < time) throw new RuntimeException();
		while(true) {
			double minEvtTime = newTime;
			int minEvtTimeI = -1;
			for(int i = 0; i < processes.size(); i++) {
				double evtTime = processes.get(i).peekNextEventTime();
				if(evtTime < time) throw new RuntimeException();
				if(evtTime < minEvtTime) {
					minEvtTime = evtTime;
					minEvtTimeI = i;
				}
			}
			if(minEvtTime != time) {
				for(int i = 0; i < processes.size(); i++) {
					processes.get(i).stepToTime(minEvtTime);
				}
			}
			this.time = minEvtTime;
			if(minEvtTimeI < 0) break;
			processes.get(minEvtTimeI).resolveEvent();
		}
	}
}
