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
 * Generates morphing sticky circles in a radial pattern.
 * @author Matthew Michelotti
 */
public class CMorphStickySpinner extends Component {
	private double baseAngle = .1;
	
	public CMorphStickySpinner(final double x, final double y)
	{
		super(Game.engine.makeCircle());
		HBCircle circ = circ();
		circ.setDiam(CEnemyShip.DIAM);
		circ.setPos(x, y);
		circ.setGroup(-1);
		circ.finalize(Double.POSITIVE_INFINITY);
		
		final double offset = .5*(CEnemyShip.DIAM + CMorphBullet.DIAM);
		
		Game.engine.addEvent(new FunctionEvent() {
			@Override public void resolve() {
				for(int i = 0; i < 3; i++) {
					double angle = baseAngle + i*2*Math.PI/3.0;
					new CMorphSticky(PosAndVel.radial(x, y, angle, offset, 440));
				}
				baseAngle = (baseAngle + .15921) % (2*Math.PI);
				setTime(getTime() + .094);
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
