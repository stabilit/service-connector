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
package test.stabilit.sc.common.util;

import java.lang.reflect.Method;

/**
 * @author JTraber
 * 
 */
public class ReflectionUtil {

	public static Method getCurrentMethod(Object obj, Class<?>... args) throws Exception {
		String currentMethodName = getCurrentMethodName(3);
		Class<?> cl = obj.getClass();
		return cl.getMethod(currentMethodName, args);
	}

	public static String getCurrentMethodName(int index) {
		StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
		return stackTraceElement[index].getMethodName();
	}

	public static Method getMethod(Class<?> cl, String methodName, Class<?>... args) throws Exception {
		return cl.getMethod(methodName, args);
	}
}
