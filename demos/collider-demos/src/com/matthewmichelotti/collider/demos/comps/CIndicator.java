/*****************************************************************************
 * Copyright 2013 Matthew D. Michelotti.
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
 ****************************************************************************/

package com.matthewmichelotti.collider.demos.comps;

import com.badlogic.gdx.graphics.Color;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.Game;

/**
 * Indicates how many bullets are overlapping with this Component.
 * @author Matthew Michelotti
 */
public class CIndicator extends Component {
	private static Color LO_COLOR = new Color(0.0f, 0.0f, 0.3f, 1.0f);
	private static Color HI_COLOR = new Color(0.2f, 0.8f, 1.0f, 1.0f);
	
	private int overlaps = 0;
	private Color blendColor = new Color();

	public CIndicator(double x, double y, boolean isRect) {
		super(isRect ? Game.engine.makeRect() : Game.engine.makeCircle());
		if(isRect) rect().setDims(160.0);
		else circ().setDiam(180.0);
		hitBox().setPos(x, y);
		hitBox().setEndTime(Double.POSITIVE_INFINITY);
	}

	@Override public void onCollide(Component other) {overlaps++;}
	@Override public void onSeparate(Component other) {overlaps--;}
	@Override public boolean canInteract(Component other) {
		return other instanceof CBullet || other instanceof CMorphBullet
				|| other instanceof CWaveBullet;
	}
	@Override public boolean interactsWithBullets() {return true;}
	@Override public Color getColor() {
		float alpha = 1.0f - (1.0f/(1 + .2f*overlaps));
		blendColor.set(LO_COLOR);
		blendColor.lerp(HI_COLOR, alpha);
		return blendColor;
	}
	@Override public String getMessage() {return "" + overlaps;}
}
