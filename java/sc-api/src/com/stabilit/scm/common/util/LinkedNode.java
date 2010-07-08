/*
 *-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.common.util;

public class LinkedNode<T> {
	public T value;
	public LinkedNode<T> next;
	private int referenced;

	public LinkedNode() {
	}

	public LinkedNode(T value) {
		this.value = value;
	}

	public LinkedNode(T value, LinkedNode<T> next) {
		this.value = value;
		this.next = next;
	}

	public LinkedNode<T> getNext() {
		return next;
	}

	public T getValue() {
		return value;
	}
	
	public boolean isReferenced() {
		return referenced > 0;
	}

	public void reference() {
		this.referenced++;
	}

	public void dereference() {
		this.referenced--;
	}
}
