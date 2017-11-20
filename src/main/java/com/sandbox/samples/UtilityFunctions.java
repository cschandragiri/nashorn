package com.sandbox.samples;

import java.util.Objects;
import java.util.function.BiFunction;

import javax.script.Invocable;

import org.apache.log4j.Logger;

import io.vavr.CheckedPredicate;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.control.Try;

public final class UtilityFunctions {

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
	public static BiFunction<Invocable, Logger, Integer> limiter = (invocable, logger) -> {
		return Try.of(() -> invocable.invokeFunction("limit"))
				.filter(e -> e != null && e instanceof Integer)
				.onFailure(logger::error)
				.map(Integer.class::cast)
				.getOrElse(10);
	};

	/**
	 * Here, we get the mapping function from the JS side
	 */
	public static Function3<Invocable, Logger, Person, Object> runTimeMapper = (invocable, logger,
			person) -> {
		return Try.of(() -> invocable.invokeFunction("runTimeMapper", person))
				.filter(e -> e != null)
				.onFailure(logger::error)
				.getOrNull();
	};

	/**
	 * Person Comparator abstracted out to js side
	 */
	public static Function4<Invocable, Logger, Person, Person, Integer> comparator = (invocable, logger,
			person1, person2) -> {
		return Try.of(() -> invocable.invokeFunction("comparator", person1, person2))
				.filter(e -> e != null && e instanceof Integer)
				.onFailure(logger::error)
				.map(Integer.class::cast)
				.getOrElse(0);
	};
}
