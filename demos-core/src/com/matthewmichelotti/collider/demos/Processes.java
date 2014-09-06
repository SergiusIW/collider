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

package com.matthewmichelotti.collider.demos;

import com.badlogic.gdx.utils.Array;

/**
 * Couples multiple continuous-time processes together
 * to handle their respective events in chronological order.
 * @author Matthew Michelotti
 */
public class Processes {
	private Array<Process> processes = new Array<Process>();
	private double time = 0.0;
	private boolean started = false;
	
	public Processes() {}
	
	public void addProcess(Process process) {
		if(started) throw new RuntimeException();
		processes.add(process);
	}
	
	public double getTime() {
		return time;
	}
	
	public void stepToTime(double newTime) {
		if(newTime < time) throw new RuntimeException();
		started = true;
		while(true) {
			double minEvtTime = newTime;
			int minEvtTimeI = -1;
			for(int i = 0; i < processes.size; i++) {
				double evtTime = processes.get(i).peekNextEventTime();
				if(evtTime < time) throw new RuntimeException();
				if(evtTime < minEvtTime) {
					minEvtTime = evtTime;
					minEvtTimeI = i;
				}
			}
			if(minEvtTime != time) {
				for(Process p : processes) {
					p.stepToTime(minEvtTime);
				}
			}
			this.time = minEvtTime;
			if(minEvtTimeI < 0) break;
			processes.get(minEvtTimeI).resolveEvent();
		}
	}
}
