package com.sandbox.samples;

import java.io.FileReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
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
import io.vavr.Function2;
import io.vavr.control.Try;

/**
 * Sample usage of nashorn engine. Filtering and streaming logic to applied on a stream of data can
 * be configured dynamically from the script.js file.
 */
public class NashornExample1
{
	final static Logger logger = Logger.getLogger(NashornExample1.class);
	
	public interface PersonService
	{
		public String greet(Person person);
	}

	public static void main(String args[]) throws Exception
	{
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		Bindings localBindings = new SimpleBindings(
				Stream.of(new SimpleEntry<>("a", 10), new SimpleEntry<>("b", 20))
						.collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
		engine.setBindings(localBindings, ScriptContext.ENGINE_SCOPE);
		
		Bindings globalBindings = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
		/** Bind log4j logger to global scope for debugging purposes **/
		globalBindings.put("logger", logger);
		engine.setBindings(globalBindings, ScriptContext.ENGINE_SCOPE);
		
		Try.of(() -> engine.eval(new FileReader("src/main/java/com/sandbox/samples/example1.js")))
				.onFailure(logger::error);

		List<Person> list2 = new ArrayList<Person>();
		list2.add(new Person("Jack", 21, "01/05/2017"));
		list2.add(new Person("Nyugen", 34, "02/05/2017"));
		list2.add(new Person("Smith", 79, "03/05/2017"));
		list2.add(new Person("Raj", 3, "04/05/2017"));
		list2.add(new Person("Rashid", 43, "05/05/2017"));
		list2.add(new Person("Yi", 21, "06/05/2017"));
		list2.add(new Person("Shankar", 2, "07/05/2017"));
		list2.add(new Person("Yoda", 1030, "08/05/2017"));
		list2.add(new Person("Christie", 29, "09/05/2017"));
		list2.add(new Person(null, 25, "10/05/2017"));
		list2.add(new Person("Smith", 40, "11/05/2017"));
		list2.add(new Person("Smith", 23, null));

		Invocable invocable = (Invocable) engine;
		CheckedPredicate<Person> predicate = e ->
		{
			Object result = invocable.invokeFunction("filter", e);
			if (!Objects.isNull(result) && result instanceof Boolean)
			{
				return (boolean) result;
			}
			return false;
		};

		Supplier<Integer> limit = () ->
		{
			return Try.of(() -> invocable.invokeFunction("limit"))
					.filter(e -> e != null && e instanceof Integer)
					.onFailure(logger::error)
					.map(Integer.class::cast)
					.getOrElse(10);
		};

		Function2<Person, Person, Integer> comparator = (e1, e2) ->
		{
			return Try.of(() -> invocable.invokeFunction("comparator", e1, e2))
					.filter(e -> e != null && e instanceof Integer)
					.onFailure(logger::error)
					.map(Integer.class::cast)
					.getOrElse(0);
		};

		Function<Person, Object> runTimeMapper = e1 ->
		{
			return Try.of(() -> invocable.invokeFunction("runTimeMapper", e1))
					.filter(e -> e != null)
					.onFailure(logger::error)
					.getOrNull();
		};

		/********** Filter & compare based on the condition defined in the script.js *********/
		List<Person> output = list2.stream()
				.filter(e ->
				{
					return Try.of(() -> e)
							.filterTry(predicate)
							.onFailure(logger::error)
							.isSuccess();
				})
				.sorted((e1, e2) -> comparator.apply(e1, e2))
				.limit(limit.get())
				.collect(Collectors.toList());
		System.out.println("Filtered person: " + output);

		/********** Map person to Object, the mapping logic is moved to javascript **********/
		List<Object> dates = list2.stream()
				.map(e -> runTimeMapper.apply(e))
				.collect(Collectors.toList());
		System.out.println("Dates: " + dates);

		PersonService service = invocable.getInterface(PersonService.class);
		Person person = new Person("Jack", 21, "01/05/2017");
		System.out.println(service.greet(person));
	}
}
