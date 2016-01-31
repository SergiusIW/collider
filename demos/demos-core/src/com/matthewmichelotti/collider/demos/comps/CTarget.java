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
import com.matthewmichelotti.collider.HBPositioned;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.Game;

/**
 * A component that flashes when hit by a bullet,
 * sticky circle, etc.
 * @author Matthew Michelotti
 */
public class CTarget extends Component {
	private final static double LIT_TIME = .17;
	private double hitTime = Double.NEGATIVE_INFINITY;
	private Color color;
	private Color blendColor = new Color();

	public CTarget(HBPositioned hitBox, Color color) {
		super(hitBox);
		this.color = color;
	}

	public boolean isEnemy() {return false;}
	public void hit() {hitTime = Game.engine.getTime();}

	@Override public void onCollide(Component other) {}
	@Override public void onSeparate(Component other) {}
	@Override public boolean canInteract(Component other) {return false;}
	@Override public boolean interactsWithBullets() {return true;}

	@Override
	public Color getColor() {
		double timeDiff = Game.engine.getTime() - hitTime;
		if(timeDiff >= LIT_TIME) return color;
		double alpha = Math.cos(.5*Math.PI*timeDiff/LIT_TIME);
		if(alpha <= 0.0) return color;
		blendColor.set(color);
		return blendColor.lerp(Color.WHITE, (float)alpha);
	}
}
