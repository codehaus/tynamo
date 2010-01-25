/*
 * Copyright © Pierangelo Sartini, 2010.
 */

package org.tynamo.seedentity.jpa.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class for reading and setting bean properties.
 * It uses getters and setters and does work with active
 * security manager.
 *
 * @author Pierangelo Sartini
 */
public class BeanUtil {
	public static Object getProperty(Object bean, String name) {
		Object ret = null;
		Class clazz = bean.getClass();

		name = name.substring(0, 1).toUpperCase().concat(name.substring(1));
		try {
			Method m = null;
			try {
				m = clazz.getDeclaredMethod("get" + name);
			} catch (NoSuchMethodException ex) {
				m = clazz.getDeclaredMethod("is" + name);
			}
			ret = m.invoke(bean);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static void setProperty(Object bean, String name, Object value) {
		Class clazz = bean.getClass();

		name = name.substring(0, 1).toUpperCase().concat(name.substring(1));
		try {
			Method m = null;
			m = clazz.getDeclaredMethod("set" + name, value.getClass());
			m.invoke(bean, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
