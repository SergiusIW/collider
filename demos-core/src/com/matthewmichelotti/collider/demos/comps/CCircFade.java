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
import com.matthewmichelotti.collider.Normal;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.FunctionEvent;
import com.matthewmichelotti.collider.demos.Game;

/**
 * A visual effect that displays a shrinking circle.
 * @author Matthew Michelotti
 */
public class CCircFade extends Component {
	private Color color;
	
	public CCircFade(HBCircle origCirc, Color color, double delay, Normal normal) {
		this(origCirc.getX(), origCirc.getY(), origCirc.getDiam(), color, delay, normal);
	}
	
	public CCircFade(double x, double y, double diam, Color color, double delay, Normal normal) {
		super(Game.engine.makeCircle());
		HBCircle circ = circ();
		circ.setGroup(-1);
		circ.setX(x);
		circ.setY(y);
		circ.setDiam(diam);
		
		this.color = color;
		
		double vel = .5*circ.getDiam()/delay;
		if(normal != null) {
			circ.setVel(vel*normal.getUnitX(), vel*normal.getUnitY());
		}
		circ.setVelDiam(-2*vel);
		
		double endTime = circ.getTime() + .99*delay;
		circ.setEndTime(endTime);
		
		Game.engine.addEvent(new FunctionEvent(endTime) {
			@Override public void resolve() {delete();}
		});
	}

	@Override public boolean canInteract(Component o) {return false;}
	@Override public boolean interactsWithBullets() {return false;}
	@Override public void onCollide(Component other) {}
	@Override public void onSeparate(Component other) {}
	@Override public Color getColor() {return color;}
}
