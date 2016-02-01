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

/**
 * Creates a {@link FunctionEvent} to periodically
 * adjust values and velocities in a wave-like fashion.
 * @author Matthew Michelotti
 */
public abstract class WaveUpdater {
	private final static double[] SIN_SAMPLES;
	
	static {
		double[] halfSinSamples = new double[] {.25, .365, .45, .5, .55, .635, .75};
		SIN_SAMPLES = new double[halfSinSamples.length*2];
		for(int i = 0; i < halfSinSamples.length; i++) {
			SIN_SAMPLES[i] = .5*halfSinSamples[i];
			SIN_SAMPLES[halfSinSamples.length + i] = .5 + .5*halfSinSamples[i];
		}
	}
	
	private double mean;
	private double amplitude;
	private double period;
	private int index = 0;

	protected WaveUpdater(double mean, double amplitude, double period) {
		this.mean = mean;
		this.amplitude = amplitude;
		this.period = period;
		
		if(!isValid()) return;
		double endTime = step(0.0, SIN_SAMPLES[0]);
		Game.engine.addEvent(new FunctionEvent(endTime) {
			@Override public void resolve() {
				if(!isValid()) return;
				double startFrac = SIN_SAMPLES[index];
				index++;
				double endFrac;
				if(index < SIN_SAMPLES.length) {
					endFrac = SIN_SAMPLES[index];
				}
				else {
					index = 0;
					endFrac = 1.0 + SIN_SAMPLES[0];
				}
				double endTime = step(startFrac, endFrac);
				setTime(endTime);
				Game.engine.addEvent(this);
			}
		});
	}
	
	protected abstract boolean isValid();
	protected abstract void update(double value, double vel, double endTime);
	
	private double step(double startFrac, double endFrac) {
		double duration = (endFrac - startFrac)*period;
		double endTime = Game.engine.getTime() + duration;
		double startValue = mean + amplitude*Math.sin(2*Math.PI*startFrac);
		double endValue = mean + amplitude*Math.sin(2*Math.PI*endFrac);
		update(startValue, (endValue - startValue)/duration, endTime);
		return endTime;
	}
}
