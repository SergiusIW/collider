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

package com.matthewmichelotti.collider;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;

final class SetPool <T> {
	private int arrayCap;
	private int arrayReturnThreshold;
	private int setInitCap;
	
	private Pool<Object[]> arrayPool;
	private Pool<ObjectSet<Object>> setPool;
	
	private SetIterator<T> iterator = new SetIterator<T>();
	
	private boolean lastOpSuccess;
	
	SetPool() {
		this(12, 32);
	}
	
	SetPool(int arrayCap) {
		this(arrayCap, 32);
	}
	
	SetPool(int arrayCap, int setInitCap) {
		if(arrayCap < 1) throw new IllegalArgumentException();
		if(setInitCap <= arrayCap) throw new IllegalArgumentException();
		this.arrayCap = arrayCap;
		this.arrayReturnThreshold = (arrayCap + 1)/2;
		this.setInitCap = setInitCap;
		
		arrayPool = new Pool<Object[]>() {
			@Override protected Object[] newObject() {
				return new Object[SetPool.this.arrayCap];
			}
		};
		
		setPool = new Pool<ObjectSet<Object>>() {
			@Override protected ObjectSet<Object> newObject() {
				return new ObjectSet<Object>(SetPool.this.setInitCap);
			}
		};
	}
	
	@SuppressWarnings("unchecked")
	Object add(Object setObj, T value) {
		if(value == null) throw new IllegalArgumentException();
		lastOpSuccess = true;
		Class<?> valueClass = value.getClass();
		if(valueClass == Object[].class || valueClass == ObjectSet.class) {
			throw new IllegalArgumentException(); 
		}
		if(setObj == null) return value;
		Class<?> setObjClass = setObj.getClass();
		if(setObjClass == Object[].class) {
			Object[] arr = (Object[])setObj;
			int size;
			for(size = 0; size < arrayCap && arr[size] != null; size++) {
				if(arr[size] == value) {
					lastOpSuccess = false;
					return arr;
				}
			}
			if(size != arrayCap) {
				arr[size] = value;
				return arr;
			}
			ObjectSet<Object> set = setPool.obtain();
			for(int i = 0; i < arrayCap; i++) {
				set.add(arr[i]);
				arr[i] = null;
			}
			set.add(value);
			arrayPool.free(arr);
			return set;
		}
		if(setObjClass == ObjectSet.class) {
			lastOpSuccess = ((ObjectSet<Object>)setObj).add(value);
			return setObj;
		}
		if(setObj == value) {
			lastOpSuccess = false;
			return setObj;
		}
		Object[] arr = arrayPool.obtain();
		arr[0] = setObj;
		arr[1] = value;
		return arr;
	}
	
	@SuppressWarnings("unchecked")
	Object clear(Object setObj) {
		if(setObj == null) return null;
		Class<?> setObjClass = setObj.getClass();
		if(setObjClass == Object[].class) {
			Object[] arr = (Object[])setObj;
			for(int i = 0; i < arr.length && arr[i] != null; i++) {
				arr[i] = null;
			}
			arrayPool.free(arr);
		}
		else if(setObjClass == ObjectSet.class) {
			ObjectSet<Object> set = (ObjectSet<Object>)setObj;
			set.clear(setInitCap);
			setPool.free(set);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	Object remove(Object setObj, T value) {
		if(value == null) throw new IllegalArgumentException();
		lastOpSuccess = true;
		if(setObj == null) {
			lastOpSuccess = false;
			return null;
		}
		Class<?> setObjClass = setObj.getClass();
		if(setObjClass == Object[].class) {
			Object[] arr = (Object[])setObj;
			int i, size;
			for(i = 0; i < arrayCap; i++) {
				Object elem = arr[i];
				if(elem == null) {
					lastOpSuccess = false;
					return setObj;
				}
				if(elem == value) break;
			}
			if(i == arrayCap) return setObj;
			for(size = i + 1; size < arrayCap && arr[size] != null; size++);
			arr[i] = arr[size - 1];
			arr[size - 1] = null;
			return shrinkArray(arr);
		}
		else if(setObjClass == ObjectSet.class) {
			ObjectSet<Object> set = (ObjectSet<Object>)setObj;
			lastOpSuccess = set.remove(value);
			if(!lastOpSuccess) return set;
			return shrinkSet(set);
		}
		else {
			if(setObj == value) return null;
			lastOpSuccess = false;
			return setObj;
		}
	}
	
	boolean wasSuccessful() {return lastOpSuccess;}
	
	SetIterator<T> iterator(Object setObj) {
		iterator.init(setObj);
		return iterator;
	}
	
	private Object shrinkArray(Object[] arr) {
		if(arr[1] != null) return arr;
		Object value = arr[0];
		arr[0] = null;
		arrayPool.free(arr);
		return value;
	}
	
	private Object shrinkSet(ObjectSet<Object> set) {
		if(set.size > arrayReturnThreshold) return set;
		Object result;
		if(set.size == 0) result = null;
		else if(set.size == 1) result = set.iterator().next();
		else {
			Object[] arr = arrayPool.obtain();
			int i = 0;
			ObjectSet.SetIterator<Object> iter = set.iterator();
			while(iter.hasNext()) {
				arr[i] = iter.next();
				i++;
			}
			result = arr;
		}
		set.clear(setInitCap);
		setPool.free(set);
		return result;
	}
	
	static class SetIterator <T> implements Iterator<T>, Iterable<T> {
		private Object value;
		private Object[] arr;
		private Iterator<Object> setIter;
		private int index;
		
		SetIterator() {}
		
		SetIterator(Object setObj) {init(setObj);}
		
		@SuppressWarnings("unchecked")
		void init(Object setObj) {
			clear();
			if(setObj == null) return;
			Class<?> setObjClass = setObj.getClass();
			if(setObjClass == Object[].class) {
				arr = (Object[])setObj;
				if(arr[0] == null) arr = null;
			}
			else if(setObjClass == ObjectSet.class) {
				ObjectSet<Object> set = (ObjectSet<Object>)setObj;
				if(set.size != 0) setIter = ((ObjectSet<Object>)setObj).iterator();
			}
			else {
				value = setObj;
			}
		}
		
		void clear() {
			value = null;
			arr = null;
			setIter = null;
			index = 0;
		}

		@Override
		public boolean hasNext() {
			return value != null || arr != null || setIter != null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			if(value != null) {
				Object result = value;
				value = null;
				return (T)result;
			}
			if(arr != null) {
				Object result = arr[index];
				index++;
				if(index >= arr.length || arr[index] == null) {
					arr = null;
					index = 0;
				}
				return (T)result;
			}
			if(setIter != null) {
				Object result = setIter.next();
				if(!setIter.hasNext()) setIter = null;
				return (T)result;
			}
			throw new NoSuchElementException();
		}
		
		@Override public void remove() {throw new UnsupportedOperationException();}
		@Override public Iterator<T> iterator() {return this;}
	}
}
