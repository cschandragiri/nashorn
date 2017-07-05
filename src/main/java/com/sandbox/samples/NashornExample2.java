package com.sandbox.samples;

import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import io.vavr.control.Try;

public class NashornExample2
{

	public static void main(String args[]) throws Exception
	{
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		Person obj = Try
				.of(() -> engine.eval(new FileReader("src/main/java/com/sandbox/samples/example2.js")))
				.onFailure(System.out::println)
				.map(e -> (Person) e)
				.getOrNull();
		System.out.println(obj);
	}

}
