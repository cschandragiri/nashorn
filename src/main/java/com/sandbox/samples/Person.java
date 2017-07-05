package com.sandbox.samples;

public class Person
{

	String name;
	int age;
	String graduationDate;

	public String getName()
	{
		return name;
	}

	public int getAge()
	{
		return age;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	public String getGraduationDate()
	{
		return graduationDate;
	}

	public void setGraduationDate(String graduationDate)
	{
		this.graduationDate = graduationDate;
	}

	public Person(String name, int age, String graduationDate)
	{
		super();
		this.name = name;
		this.age = age;
		this.graduationDate = graduationDate;
	}

	@Override
	public String toString()
	{
		return "Person [name=" + name + ", age=" + age + ", graduationDate=" + graduationDate + "]";
	}

}
