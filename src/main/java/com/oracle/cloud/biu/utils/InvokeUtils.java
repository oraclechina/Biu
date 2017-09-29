package com.oracle.cloud.biu.utils;

import java.lang.reflect.Method;

public class InvokeUtils {

	/**
	 * 通过字符串串调用方法
	 * 
	 * @param classAndMethod
	 *            类名-方法名，通过此字符串调用类中的方法
	 * @param paramTypes
	 *            方法类型列表(因为方法可能重载)
	 * @param params
	 *            需要调用的方法的参数列表
	 * @return
	 */
	public static Object exec(String classAndMethod) {
		String[] args = classAndMethod.split("-");
		// 要调用的类名
		String className = args[0];
		// 要调用的方法名
		String method = args[1];
		try {
			// 加载类，参数是完整类名
			Class clazz = Class.forName(className);

			// 第一个参数是方法名，后面的参数指示方法的参数类型和个数
			Method theMethod = clazz.getMethod(method);

			// 第一个参数类实例(必须有对象才能调用非静态方法,如果是静态方法此参数可为null)，后面是要传个方法的参数
			Object result = theMethod.invoke(clazz.newInstance());

			return result;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	/**
	 * 通过字符串串调用方法
	 * 
	 * @param classAndMethod
	 *            类名-方法名，通过此字符串调用类中的方法
	 * @param paramTypes
	 *            方法类型列表(因为方法可能重载)
	 * @param params
	 *            需要调用的方法的参数列表
	 * @return
	 */
	public static Object exec(String classAndMethod, Class[] paramTypes, Object[] params) {
		String[] args = classAndMethod.split("-");
		// 要调用的类名
		String className = args[0];
		// 要调用的方法名
		String method = args[1];
		try {
			// 加载类，参数是完整类名
			Class clazz = Class.forName(className);

			// 第一个参数是方法名，后面的参数指示方法的参数类型和个数
			Method theMethod = clazz.getMethod(method, paramTypes);

			// 第一个参数类实例(必须有对象才能调用非静态方法,如果是静态方法此参数可为null)，后面是要传个方法的参数
			Object result = theMethod.invoke(clazz.newInstance(), params);

			return result;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}