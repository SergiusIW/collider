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

package com.matthewmichelotti.collider;

final class ECollide extends FunctionEvent {
	private HitBox a, b;
	private int idA, idB;
	private boolean collided;
	
	ECollide() {}
	
	void init(HitBox a, HitBox b, double time, boolean collided) {
		this.a = a;
		this.b = b;
		this.idA = a.getChangeId();
		this.idB = b.getChangeId();
		this.time = time;
		this.collided = collided;
	}
	
	@Override
	void resolve(Collider collider) {
		if(a.getChangeId() == idA && b.getChangeId() == idB) {
			collider.setCollision(a, b, collided);
		}
		a = null;
		b = null;
		collider.freeEvent(this);
	}
}
