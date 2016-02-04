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

package com.matthewmichelotti.collider.geom;

//TODO javadoc
public final class Shape {
	public final static Shape ZERO_CIRCLE = newCircle(0.0);
	public final static Shape ZERO_RECT = newRect(0.0, 0.0);

	private final boolean circle;
	private final double width, height;

	private Shape(boolean circle, double width, double height) {
		if(circle && width != height) throw new IllegalArgumentException();
		this.circle = circle;
		this.width = width;
		this.height = height;
	}

	public static Shape newCircle(double diam) {
		return new Shape(true, diam, diam);
	}

	public static Shape newRect(double width, double height) {
		return new Shape(false, width, height);
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public boolean isCircle() {
		return circle;
	}

	public boolean isRect() {
		return !circle;
	}
}
