/*
 * Copyright 2013-2016 Matthew D. Michelotti
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

package com.matthewmichelotti.collider.processes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Couples multiple {@link FlowProcess} objects together
 * to handle their respective events in chronological order.
 */
public class FlowProcesses {
	private List<FlowProcess> processes;
	private double time = 0.0;

	/**
	 * Constructs a new FlowProcesses object.  Time is initialized to zero.
	 * @param processes list of processes to use
	 */
	public FlowProcesses(FlowProcess... processes) {
		this.processes = Arrays.asList(processes);
		for(FlowProcess process : this.processes) {
			process.advance(0.0);
		}
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
	public void advance(double newTime) {
		if(newTime < time) throw new RuntimeException();
		while(true) {
			double minEvtTime = newTime;
			int minEvtTimeI = -1;
			for(int i = 0; i < processes.size(); i++) {
				double evtTime = processes.get(i).peekNextTime();
				if(evtTime < time) throw new RuntimeException();
				if(evtTime < minEvtTime) {
					minEvtTime = evtTime;
					minEvtTimeI = i;
				}
			}
			if(minEvtTime != time) {
				for(int i = 0; i < processes.size(); i++) {
					processes.get(i).advance(minEvtTime);
				}
			}
			this.time = minEvtTime;
			if(minEvtTimeI < 0) break;
			processes.get(minEvtTimeI).resolveNext();
		}
	}
}
