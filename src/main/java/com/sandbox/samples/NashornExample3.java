package com.sandbox.samples;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;

import io.vavr.control.Try;

/**
 * 
 * @author cchandragiri
 *
 */
public class NashornExample3 {

	final static Logger logger = Logger.getLogger(NashornExample3.class);

	public static void main(String args[]) throws Exception {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

		Bindings globalBindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
		/** Bind log4j logger to global scope for debugging purposes **/
		globalBindings.put("logger", logger);
		engine.setBindings(globalBindings, ScriptContext.ENGINE_SCOPE);

		/**
		 * Force each iteration to sleep for 2 seconds, so that we could edit the mapper
		 * function and see the output being changed at runtime
		 */
		for (Person person : getDataSet()) {
			Try.of(() -> engine.eval(new FileReader("src/main/resources/example1.js")))
					.onFailure(logger::error);
			Invocable invocable = (Invocable) engine;
			System.out.println(
					UtilityFunctions.runTimeMapper.apply(invocable, logger, person));
			Thread.sleep(2000);
		}
	}

	private static List<Person> getDataSet() {
		List<Person> list = new ArrayList<Person>();
		list.add(new Person("Jack", 21, "01/05/2017"));
		list.add(new Person("Nyugen", 34, "02/05/2017"));
		list.add(new Person("Smith", 79, "03/05/2017"));
		list.add(new Person("Raj", 3, "04/05/2017"));
		list.add(new Person("Rashid", 43, "05/05/2017"));
		list.add(new Person("Yi", 21, "06/05/2017"));
		list.add(new Person("Shankar", 2, "07/05/2017"));
		list.add(new Person("Yoda", 1030, "08/05/2017"));
		list.add(new Person("Christie", 29, "09/05/2017"));
		list.add(new Person(null, 25, "10/05/2017"));
		list.add(new Person("Smith", 40, "11/05/2017"));
		list.add(new Person("Smith", 23, null));
		return list;
	}
}
