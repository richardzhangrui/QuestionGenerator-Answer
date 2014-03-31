package com.generation;

public class QuestionGeneratorFactory {
	public static QuestionGenerator makeGenerator(String classname) {
		Class<?> c;
		try {
			c = Class.forName("com.generation." + classname);
			return (QuestionGenerator) c.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
