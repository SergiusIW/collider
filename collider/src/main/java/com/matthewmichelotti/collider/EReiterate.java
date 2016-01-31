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

package com.matthewmichelotti.collider;

final class EReiterate extends FunctionEvent {
	private HitBox hitBox;
	private double period;
	private double endTime;
	private int changeId;
	
	EReiterate() {}
	
	void init(HitBox hitBox, double startTime, double endTime, double period) {
		if(startTime >= endTime) throw new RuntimeException();
		this.hitBox = hitBox;
		this.time = startTime;
		this.endTime = endTime;
		this.period = period;
		this.changeId = hitBox.getChangeId();
	}

	@Override
	void resolve(Collider collider) {
		if(changeId == hitBox.getChangeId()) {
			double stepEndTime = collider.getTime() + period;
			if(endTime <= stepEndTime) {
				hitBox.commit(endTime);
			}
			else {
				hitBox.commit(stepEndTime);
				collider.processCurHBAndCollision(false);
				changeId = hitBox.getChangeId();
				time = stepEndTime;
				collider.queue(this);
				return;
			}
		}
		hitBox = null;
		collider.freeEvent(this);
	}
}
