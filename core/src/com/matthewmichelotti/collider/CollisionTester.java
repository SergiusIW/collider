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

package com.matthewmichelotti.collider;

final class CollisionTester {
	private double separateBuffer;
	private HBRect dummyRect = new HBRect(null);
	private HBCircle dummyPoint = new HBCircle(null);
	private HBRect dummyRect2 = new HBRect(null);
	private HBCircle dummyCircle = new HBCircle(null);
	private Normal normal = new Normal();
	
	CollisionTester(ColliderOpts opts) {
		this.separateBuffer = opts.separateBuffer;
		if(this.separateBuffer <= 0.0) throw new IllegalArgumentException();
	}
	
	double collideTime(HitBox a, HitBox b, double startTime) {
		double endTime = Arith.min(a.endTime, b.endTime);
		if(endTime <= startTime) return Double.POSITIVE_INFINITY;
		if(!boundBoxTest(a, b, startTime, endTime)) return Double.POSITIVE_INFINITY;
		return getTime(a, b, startTime, endTime, true);
	}
	
	double separateTime(HitBox a, HitBox b, double startTime) {
		double endTime = Arith.min(a.endTime, b.endTime);
		if(endTime <= startTime) return Double.POSITIVE_INFINITY;
		boolean aIsRect = (a.getClass() == HBRect.class);
		boolean bIsRect = (b.getClass() == HBRect.class);
		if(aIsRect && bIsRect) {
			HBRect rect = (HBRect)a;
			double hw = rect.startHW;
			double hh = rect.startHH;
			rect.startHW += separateBuffer;
			rect.startHH += separateBuffer;
			double result = getTime(a, b, startTime, endTime, false);
			rect.startHW = hw;
			rect.startHH = hh;
			return result;
		}
		else {
			HBCircle circ = (HBCircle)(aIsRect ? b : a);
			double rad = circ.startRad;
			circ.startRad += separateBuffer;
			double result = getTime(a, b, startTime, endTime, false);
			circ.startRad = rad;
			return result;
		}
	}
	
	Normal normal(HitBox src, HitBox dst, double time) {
		if(src.getClass() == HBRect.class) {
			if(dst.getClass() == HBRect.class) return rectRectNormal((HBRect)src, (HBRect)dst, time);
			else return rectCircNormal((HBRect)src, (HBCircle)dst, time);
		}
		else {
			if(dst.getClass() == HBRect.class) {
				Normal normal = rectCircNormal((HBRect)dst, (HBCircle)src, time);
				normal.x = -normal.x;
				normal.y = -normal.y;
				return normal;
			}
			else return circCircNormal((HBCircle)src, (HBCircle)dst, time);
		}
	}
	
	private double getTime(HitBox a, HitBox b, double startTime, double endTime, boolean forCollide) {
		boolean aIsRect = (a.getClass() == HBRect.class);
		boolean bIsRect = (b.getClass() == HBRect.class);
		double result;
		if(aIsRect) {
			if(bIsRect) result = rectRectTime((HBRect)a, (HBRect)b, startTime, endTime, forCollide);
			else result = rectCircTime((HBRect)a, (HBCircle)b, startTime, endTime, forCollide);
		}
		else {
			if(bIsRect) result = rectCircTime((HBRect)b, (HBCircle)a, startTime, endTime, forCollide);
			else result = circCircTime((HBCircle)a, (HBCircle)b, startTime, forCollide);
		}
		if(result >= endTime) result = Double.POSITIVE_INFINITY;
		return result;
	}
	
	private double rectCircTime(HBRect a, HBCircle b, double startTime, double endTime,
			boolean forCollide)
	{
		if(forCollide) return rectCircCollideTime(a, b, startTime, endTime);
		else return rectCircSeparateTime(a, b, startTime, endTime);
	}
	
	private static boolean boundBoxTest(HitBox a, HitBox b, double startTime, double endTime) {
		for(int dir = 0; dir < 4; dir++) {
			double overlap = a.getBoundEdgeComp(dir, startTime, endTime)
					+ b.getBoundEdgeComp(Dir.opp(dir), startTime, endTime);
			if(overlap <= 0.0) return false;
		}
		return true;
	}
	
	private static double rectRectTime(HBRect a, HBRect b, double startTime, double endTime,
			boolean forCollide)
	{
		double overlapStart = 0.0;
		double overlapEnd = 1.05*(endTime - startTime);
		
		for(int dir = 0; dir < 4; dir++) {
			double overlap = a.getEdgeComp(dir, startTime) + b.getEdgeComp(Dir.opp(dir), startTime);
			double overlapVel = a.getVelEdgeComp(dir) + b.getVelEdgeComp(Dir.opp(dir));
			if(overlap < 0.0) {
				if(!forCollide) return startTime;
				if(overlapVel <= 0.0) return Double.POSITIVE_INFINITY;
				else overlapStart = Arith.max(overlapStart, -overlap/overlapVel);
			}
			else if(overlapVel < 0.0) {
				overlapEnd = Arith.min(overlapEnd, -overlap/overlapVel);
			}
			if(overlapStart >= overlapEnd) return forCollide ? Double.POSITIVE_INFINITY : startTime;
		}
		
		return startTime + (forCollide ? overlapStart : overlapEnd);
	}
	
	private static double circCircTime(HBCircle a, HBCircle b, double startTime, boolean forCollide)
	{
		double sign = forCollide ? 1.0 : -1.0;
		
		double netRad = a.getRad(startTime) + b.getRad(startTime);
		double distX = a.getX(startTime) - b.getX(startTime);
		double distY = a.getY(startTime) - b.getY(startTime);
		
		double coeffC = sign*(netRad*netRad - distX*distX - distY*distY);
		if(coeffC > 0.0) return startTime;
		
		double netRadVel = a.velRad + b.velRad;
		double distXVel = a.velX - b.velX;
		double distYVel = a.velY - b.velY;
		
		double coeffA = sign*(netRadVel*netRadVel - distXVel*distXVel - distYVel*distYVel);
		double coeffB = sign*2.0*(netRad*netRadVel - distX*distXVel - distY*distYVel);
		
		double result = Arith.quadRootAscending(coeffA, coeffB, coeffC);
		if(result >= 0.0) return startTime + result;
		else return Double.POSITIVE_INFINITY; //NOTE: handles NaN case
	}
	
	private double rectCircCollideTime(HBRect a, HBCircle b, double startTime, double endTime)
	{
//		if(!forCollide) {
//			pair.init(a, b);
//			double overlap = pair.getOverlap();
//			pair.clear();
//			if(overlap <= 0.0) return startTime;
//		}
		dummyRect.dummyMimicCircle(b);
		double time = rectRectTime(a, dummyRect, startTime, endTime, true);
		if(time >= endTime) return Double.POSITIVE_INFINITY;
//		if(time >= endTime) {
//			if(forCollide) return Double.POSITIVE_INFINITY;
//			else time = endTime;
//		}
		
		for(int dir = 0; dir < 2; dir++) {
			double hiEdge = a.getEdgeComp(dir, time);
			double loEdge = -a.getEdgeComp(Dir.opp(dir), time);
			double bCoord = b.getPosComp(dir, time);
			if(bCoord > hiEdge) {
				dummyPoint.dummySetStartCoord(dir, hiEdge);
				dummyPoint.dummySetVelCoord(dir, a.getVelEdgeComp(dir));
			}
			else if(bCoord < loEdge) {
				dummyPoint.dummySetStartCoord(dir, loEdge);
				dummyPoint.dummySetVelCoord(dir, -a.getVelEdgeComp(Dir.opp(dir)));
			}
			else return time;
		}
		dummyPoint.startTime = time;
		return circCircTime(dummyPoint, b, startTime, true);
	}
	
	private double rectCircSeparateTime(HBRect a, HBCircle b, double startTime, double endTime) {
		Normal normal = normal(a, b, startTime);
		if(normal.overlap <= 0.0) return startTime;
		mirror(a, dummyRect2, endTime);
		mirror(b, dummyCircle, endTime);
		dummyRect2.startTime = 0.0;
		dummyCircle.startTime = 0.0;
		double result = rectCircCollideTime(dummyRect2, dummyCircle, 0.0, endTime - startTime);
		return Arith.max(startTime, endTime - result);
	}
	
	private static void mirror(HBRect original, HBRect mirror, double endTime) {
		mirrorPos(original, mirror, endTime);
		mirror.startHW = original.getHW(endTime);
		mirror.startHH = original.getHH(endTime);
		mirror.velHW = -original.velHW;
		mirror.velHH = -original.velHH;
	}
	
	private static void mirror(HBCircle original, HBCircle mirror, double endTime) {
		mirrorPos(original, mirror, endTime);
		mirror.startRad = original.getRad(endTime);
		mirror.velRad = -original.velRad;
	}
	
	private static void mirrorPos(HBPositioned original, HBPositioned mirror, double endTime) {
		mirror.startX = original.getX(endTime);
		mirror.startY = original.getY(endTime);
		mirror.velX = -original.velX;
		mirror.velY = -original.velY;
	}
	
	private Normal rectRectNormal(HBRect src, HBRect dst, double time) {
		int minDir = 0;
		double overlap = Double.POSITIVE_INFINITY;
		for(int dir = 0; dir < 4; dir++) {
			double testOverlap = src.getEdgeComp(dir, time)
					+ dst.getEdgeComp(Dir.opp(dir), time);
			if(testOverlap < overlap) {
				overlap = testOverlap;
				minDir = dir;
			}
		}
		normal.x = Dir.x(minDir);
		normal.y = Dir.y(minDir);
		normal.overlap = overlap;
		return normal;
	}
	
	private Normal circCircNormal(HBCircle src, HBCircle dst, double time) {
		double nx = dst.getX(time) - src.getX(time);
		double ny = dst.getY(time) - src.getY(time);
		double dist = Math.sqrt(nx*nx + ny*ny);
		if(dist == 0.0) {
			nx = 1.0;
			ny = 0.0;
		}
		else {//NOTE: if dist != 0.0, dist is at least Math.sqrt(Double.MIN_VALUE)
			double invNMag = 1.0/dist;
			nx *= invNMag;
			ny *= invNMag;
		}
		normal.x = nx;
		normal.y = ny;
		normal.overlap = src.getRad(time) + dst.getRad(time) - dist;
		return normal;
	}
	
	private Normal rectCircNormal(HBRect src, HBCircle dst, double time) {
		for(int dir = 0; dir < 2; dir++) {
			double dstCoord = dst.getPosComp(dir, time);
			double srcHi = src.getEdgeComp(dir, time);
			double srcLo = -src.getEdgeComp(Dir.opp(dir), time);
			if(dstCoord > srcHi) dummyPoint.dummySetStartCoord(dir, srcHi);
			else if(dstCoord < srcLo) dummyPoint.dummySetStartCoord(dir, srcLo);
			else {
				dummyRect.dummyMimicCircle(dst);
				return rectRectNormal(src, dummyRect, time);
			}
		}
		dummyPoint.velX = 0.0;
		dummyPoint.velY = 0.0;
		return circCircNormal(dummyPoint, dst, time);
	}
}
