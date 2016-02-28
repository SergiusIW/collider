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

/**
 * A continuous-time process. This can be used to couple Collider
 * with other continuous-time processes that the user creates.
 *
 * @see FlowProcesses
 */
public interface FlowProcess {
	/**
	 * Returns the time that the next event will occur in this process.
	 * Assumes no other processes have an event before that time.
	 * @return The time of the next event.
	 */
	public double peekNextTime();

	/**
	 * Advances this process to the given time, without resolving
	 * any events at that time.  No events should occur before
	 * this time either.
	 * @param time Time to advance the process to.  This must be
	 * at most {@link #peekNextTime()}.
	 */
	public void advance(double time);

	/**
	 * Resolve an event that occurs at the current time.
	 */
	public void resolveNext();
}
