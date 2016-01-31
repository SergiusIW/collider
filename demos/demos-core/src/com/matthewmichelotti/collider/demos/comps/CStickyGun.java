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
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.FunctionEvent;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.PosAndVel;

/**
 * Generates sticky circles shooting in a given direction
 * with some randomness.
 * @author Matthew Michelotti
 */
public class CStickyGun extends Component {
	private final static double BASE_VEL = 1500;
	
	public CStickyGun(final double x, final double y, final double baseAngle,
			final double shotDiam)
	{
		super(Game.engine.makeCircle());
		HBCircle circ = circ();
		circ.setDiam(CEnemyShip.DIAM);
		circ.setPos(x, y);
		circ.setGroup(-1);
		circ.commit(Double.POSITIVE_INFINITY);
		
		final double vel = BASE_VEL/shotDiam;
		final double period = 1.5*shotDiam/vel;
		final double offset = .5*(CEnemyShip.DIAM + shotDiam);
		
		Game.engine.addEvent(new FunctionEvent() {
			@Override public void resolve() {
				double angle = baseAngle + .45*(.5 - Math.random());
				new CSticky(shotDiam, PosAndVel.radial(x, y, angle, offset, vel));
				setTime(getTime() + period);
				Game.engine.addEvent(this);
			}
		});
	}

	@Override public void onCollide(Component other) {}
	@Override public void onSeparate(Component other) {}
	@Override public boolean canInteract(Component other) {return false;}
	@Override public boolean interactsWithBullets() {return false;}
	@Override public Color getColor() {return CEnemyShip.COLOR;}
}
