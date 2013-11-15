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
import com.matthewmichelotti.collider.HBRect;
import com.matthewmichelotti.collider.demos.Component;
import com.matthewmichelotti.collider.demos.Game;

/**
 * Displays a text message.
 * @author Matthew Michelotti
 */
public class CText extends Component {
	private String message;

	public CText(String message, double x, double y) {
		super(Game.engine.makeRect());
		HBRect rect = rect();
		rect.setPos(x, y);
		rect.setDims(1);
		rect.setGroup(-1);
		rect.setEndTime(Double.POSITIVE_INFINITY);
		this.message = message;
	}

	@Override public void onCollide(Component other) {}
	@Override public void onSeparate(Component other) {}
	@Override public boolean canInteract(Component other) {return false;}
	@Override public boolean interactsWithBullets() {return false;}
	@Override public Color getColor() {return null;}
	@Override public String getMessage() {return message;}
}
