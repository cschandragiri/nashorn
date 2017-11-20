package com.sandbox.samples;

import java.io.FileReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;

import org.apache.log4j.Logger;

import io.vavr.CheckedPredicate;
import io.vavr.Function3;
import io.vavr.control.Try;

/**
 * Sample usage of nashorn engine. Filtering and streaming logic to applied on a stream of
 * data can be configured dynamically from the script.js file.
 */
public class NashornExample1 {
	final static Logger logger = Logger.getLogger(NashornExample1.class);

	public interface PersonService {
		public String greet(Person person);
	}

	public static CheckedPredicate<Person> getPredicate(Invocable invocable) {
		CheckedPredicate<Person> predicate = e -> {
			Object result = invocable.invokeFunction("filter", e);
			if (!Objects.isNull(result) && result instanceof Boolean) {
				return (boolean) result;
			}
			return false;
		};
		return predicate;
	}

	/**
	 * Get the limit value from JS side
	 */
	public static Function<Invocable, Integer> limiter = (invocable) -> {
		return Try.of(() -> invocable.invokeFunction("limit"))
				.filter(e -> e != null && e instanceof Integer)
				.onFailure(logger::error)
				.map(Integer.class::cast)
				.getOrElse(10);
	};

	/**
	 * Here, we get the mapping function from the JS side
	 */
	public static BiFunction<Invocable, Person, Object> runTimeMapper = (invocable,
			person) -> {
		return Try.of(() -> invocable.invokeFunction("runTimeMapper", person))
				.filter(e -> e != null)
				.onFailure(logger::error)
				.getOrNull();
	};

	/**
	 * Person Comparator abstracted out to js side
	 */
	public static Function3<Invocable, Person, Person, Integer> comparator = (invocable,
			person1, person2) -> {
		return Try.of(() -> invocable.invokeFunction("comparator", person1, person2))
				.filter(e -> e != null && e instanceof Integer)
				.onFailure(logger::error)
				.map(Integer.class::cast)
				.getOrElse(0);
	};
	
	public static void main(String args[]) throws Exception {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		Bindings localBindings = new SimpleBindings(Stream
				.of(new SimpleEntry<>("a", 10), new SimpleEntry<>("b", 20))
				.collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
		engine.setBindings(localBindings, ScriptContext.ENGINE_SCOPE);

		Bindings globalBindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
		/** Bind log4j logger to global scope for debugging purposes **/
		globalBindings.put("logger", logger);
		engine.setBindings(globalBindings, ScriptContext.ENGINE_SCOPE);

		Try.of(() -> engine
				.eval(new FileReader("src/main/resources/example1.js")))
				.onFailure(logger::error);

		Invocable invocable = (Invocable) engine;
		/**********
		 * Filter & compare based on the condition defined in the script.js
		 *********/
		List<Person> output = getDataSet().stream()
				.filter(e -> {
					return Try.of(() -> e)
							.filterTry(getPredicate(invocable))
							.onFailure(logger::error)
							.isSuccess();
				})
				.sorted((e1, e2) -> comparator.apply(invocable, e1, e2))
				.limit(limiter.apply(invocable))
				.collect(Collectors.toList());
		System.out.println("Filtered person: " + output);

		/**********
		 * Map person to Object, the mapping logic is delegated to javascript
		 **********/
		/*List<Object> dates = getDataSet().stream()
				.map(e -> runTimeMapper.apply(invocable, e))
				.collect(Collectors.toList());
		System.out.println("Dates: " + dates);*/
		
		List<Object> ages = getDataSet().stream()
				.map(e -> runTimeMapper.apply(invocable, e))
				.collect(Collectors.toList());
		System.out.println("Age: " + ages);

		PersonService service = invocable.getInterface(PersonService.class);
		Person person = new Person("Jack", 21, "01/05/2017");
		System.out.println(service.greet(person));
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
