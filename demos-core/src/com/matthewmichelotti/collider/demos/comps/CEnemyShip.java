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

package com.matthewmichelotti.collider.demos.comps;

import com.badlogic.gdx.graphics.Color;
import com.matthewmichelotti.collider.HBCircle;
import com.matthewmichelotti.collider.demos.FunctionEvent;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.PosAndVel;

/**
 * An enemy ship for a danmaku scenario that uses a radial attack pattern.
 * @author Matthew Michelotti
 */
public class CEnemyShip extends CTarget {
	public final static double DIAM = 25;
	
	public final static Color COLOR = new Color(1.0f, 0.25f, 0.07f, 1.0f);
	private double baseAngle = .1;
	
	public CEnemyShip(final double x, final double y) {
		super(Game.engine.makeCircle(), COLOR);
		HBCircle circ = circ();
		circ.setDiam(DIAM);
		circ.setPos(x, y);
		circ.setEndTime(Double.POSITIVE_INFINITY);
		
		final int numPoints = 170;
		final double offset = .5*(DIAM + CBullet.DIAM);
		
		Game.engine.addEvent(new FunctionEvent() {
			@Override public void resolve() {
				for(int i = 0; i < numPoints; i++) {
					if(i % 10 < 6) continue;
					double angle = baseAngle + 2*Math.PI*i/(double)numPoints;
					new CBullet(true, PosAndVel.radial(x, y, angle, offset, 100));
				}
				baseAngle = (baseAngle + .24) % (2*Math.PI);
				setTime(getTime() + .9);
				Game.engine.addEvent(this);
			}
		});
	}
	
	@Override public boolean isEnemy() {return true;}
}
