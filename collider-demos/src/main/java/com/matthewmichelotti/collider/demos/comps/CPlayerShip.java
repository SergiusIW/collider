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

package com.matthewmichelotti.collider.demos.comps;

import com.badlogic.gdx.graphics.Color;
import com.matthewmichelotti.collider.HBCircle;
import com.matthewmichelotti.collider.HBPositioned;
import com.matthewmichelotti.collider.demos.FunctionEvent;
import com.matthewmichelotti.collider.demos.Game;
import com.matthewmichelotti.collider.demos.GameEngine;
import com.matthewmichelotti.collider.demos.MousePosListener;
import com.matthewmichelotti.collider.demos.PosAndVel;

/**
 * Player-controlled ship used in danmaku scenarios.
 * @author Matthew Michelotti
 */
public class CPlayerShip extends CTarget implements MousePosListener {
	public final static Color COLOR = new Color(0.1f, 0.15f, 1.0f, 1.0f);
	
	private final static double VEL = 600;
	
	private double stopEndTime;
	private FunctionEvent stopEvent = new FunctionEvent() {
		@Override public void resolve() {
			hitBox().setVel(0.0, 0.0);
			hitBox().commit(stopEndTime);
		}
	};

	public CPlayerShip() {
		super(Game.engine.makeCircle(), COLOR);
		HBCircle circ = circ();
		circ.setDiam(10);
		circ.setPos(.5*GameEngine.SCREEN_WIDTH, .2*GameEngine.SCREEN_HEIGHT);
		circ.commit(Double.POSITIVE_INFINITY);
		
		Game.engine.addEvent(new FunctionEvent() {
			@Override public void resolve() {
				double x = hitBox().getX();
				double y = hitBox().getY();
				new CBullet(false, new PosAndVel(x + 10, y + 10, 0, 1000));
				new CBullet(false, new PosAndVel(x - 10, y + 10, 0, 1000));
				setTime(getTime() + .13);
				Game.engine.addEvent(this);
			}
		});
	}

	@Override
	public void updateMousePos(double endTime, double mx, double my) {
		HBPositioned hitBox = hitBox();
		if(endTime <= hitBox.getTime()) return;
		double x = hitBox.getX();
		double y = hitBox.getY();
		double dx = mx - x;
		double dy = my - y;
		double dmag = Math.sqrt(dx*dx + dy*dy);
		double arriveTime = hitBox.getTime() + dmag/VEL;
		if(arriveTime <= hitBox.getTime()) {
			hitBox.setVel(0.0, 0.0);
			hitBox.commit(endTime);
			return;
		}
		double ux = dx/dmag;
		double uy = dy/dmag;
		hitBox.setVel(VEL*ux, VEL*uy);
		if(endTime <= arriveTime) {
			hitBox.commit(endTime);
			return;
		}
		hitBox.commit(arriveTime);
		this.stopEndTime = endTime;
		stopEvent.setTime(arriveTime);
		Game.engine.addEvent(stopEvent);
	}
}
