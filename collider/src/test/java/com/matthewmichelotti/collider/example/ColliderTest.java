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

package com.matthewmichelotti.collider.example;

import static org.junit.Assert.*;

import com.matthewmichelotti.collider.*;
import org.junit.Test;

public class ColliderTest {

	@Test
	public void collisionAndSeparationTest() {
		ColliderOpts opts = new ColliderOpts();
		opts.cellWidth = 2.0;
		opts.maxForesightTime = 2.0;
		opts.separateBuffer = .1;
		opts.interactTester = new InteractTester() {
			@Override public boolean canInteract(HitBox a, HitBox b) { return true; }
			@Override public int[] getInteractGroups(HitBox hitBox) { return new int[] {0}; }
		};

		Collider collider = new Collider(opts);

		HBRect rect = collider.makeRect();
		rect.setDims(2.0, 2.0);
		rect.setPos(10.0, 0.0);
		rect.setVel(-1.0, 0.0);
		rect.commit(Double.POSITIVE_INFINITY);

		HBCircle circle = collider.makeCircle();
		circle.setDiam(2.0);
		circle.setPos(0.0, 0.0);
		circle.setVel(1.0, 0.0);
		circle.commit(Double.POSITIVE_INFINITY);

		ColliderEvent event = collider.stepToTime(100.0);
		assertEquals("unexpected event time", 4.0, event.getTime(), .0001);
		assertTrue("event should have rect hitbox", event.getFirst() == rect || event.getSecond() == rect);
		assertTrue("event should have circle hitbox", event.getFirst() == circle || event.getSecond() == circle);
		assertTrue("event should be a collision event", event.isCollision());

		event = collider.stepToTime(100.0);
		assertEquals("unexpected event time", 6.05, event.getTime(), .0001);
		assertTrue("event should have rect hitbox", event.getFirst() == rect || event.getSecond() == rect);
		assertTrue("event should have circle hitbox", event.getFirst() == circle || event.getSecond() == circle);
		assertTrue("event should be a separation event", event.isSeparation());
	}
}
