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

package com.matthewmichelotti.collider.demos;

import com.matthewmichelotti.collider.HBCircle;
import com.matthewmichelotti.collider.HBRect;
import com.matthewmichelotti.collider.HitBox;

/**
 * Some geometric helper functions.
 * @author Matthew Michelotti
 */
public class Geom {

	private Geom() {}
	
	public static double area(HitBox hitBox) {
		if(hitBox instanceof HBCircle) {
			double r = .5*((HBCircle)hitBox).getDiam();
			return Math.PI*r*r;
		}
		else {
			HBRect rect = (HBRect)hitBox;
			return rect.getWidth()*rect.getHeight();
		}
	}

	public static double area2Diam(double area) {
		return 2*Math.sqrt(area/Math.PI);
	}
}
