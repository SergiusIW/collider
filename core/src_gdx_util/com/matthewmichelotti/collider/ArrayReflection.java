/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
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
 ******************************************************************************/
//!!!NOTICE!!! this file has been modified by Matthew Michelotti.
//This file was originally part of the libGDX library
//(https://github.com/libgdx/libgdx) under the Apache License as described
//above.  The AUTHORS file specified above can be found at
//https://github.com/libgdx/libgdx/blob/master/AUTHORS .
//The contents of the authors file is as follows:
//
//  # This is the official list of the AUTHORS of libgdx
//  # for copyright purposes.
//  # This file is distinct from the CONTRIBUTORS files.
//  # See the latter for an explanation.
//
//  # Names should be added to this file as
//  #	Name or Organization <email address>
//  # The email address is not required for organizations.
//  Mario Zechner <badlogicgames@gmail.com>
//  Nathan Sweet <nathan.sweet@gmail.com>
//
//End of AUTHORS file.
//
//All changes to this file are marked with comments starting with
//my initials (MM).


//MM: changed package name
//package com.badlogic.gdx.utils.reflect;
package com.matthewmichelotti.collider;

/** Utilities for Array reflection.
 * @author nexsoftware */
//MM: changed class from public to package-private
final class ArrayReflection {

	/** Creates a new array with the specified component type and length. */
	static public Object newInstance (Class c, int size) {
		return java.lang.reflect.Array.newInstance(c, size);
	}

	/** Returns the length of the supplied array. */
	static public int getLength (Object array) {
		return java.lang.reflect.Array.getLength(array);
	}

	/** Returns the value of the indexed component in the supplied array. */
	static public Object get (Object array, int index) {
		return java.lang.reflect.Array.get(array, index);
	}

	/** Sets the value of the indexed component in the supplied array to the supplied value. */
	static public void set (Object array, int index, Object value) {
		java.lang.reflect.Array.set(array, index, value);
	}

}
