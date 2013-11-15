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

package com.matthewmichelotti.collider.demos;

/**
 * Helper class to store 2D coordinate and velocity information.
 * @author Matthew Michelotti
 */
public class PosAndVel {
	public double x, y, vx, vy;

	public PosAndVel() {}
	
	public PosAndVel(double x, double y, double vx, double vy) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
	}

	public static PosAndVel radial(double baseX, double baseY, double angle,
			double offset, double vel)
	{
		PosAndVel pos = new PosAndVel();
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		pos.x = baseX + cos*offset;
		pos.y = baseY + sin*offset;
		pos.vx = cos*vel;
		pos.vy = sin*vel;
		return pos;
	}
}
