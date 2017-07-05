var filter = function(name) {
    return name.getName() != null && name.getAge() > 18;
};

var limit = function() {
	return 15;
};

var comparator = function(name1, name2) {
	var result = name1.getName()
		.compareTo(name2.getName());
	if (result == 0)
	{
		return name1.getAge()
			.compareTo(name2.getAge());
	}
	else
	{
		return result;
	}
};

var JLocalDate = Java.type('java.time.LocalDate');
var JDateTimeFormatter = Java.type("java.time.format.DateTimeFormatter");
var JFunction = Java.type('java.util.function.Function');

var dateMapperFunction = new JFunction() {
    apply: function(dateString) {
    	var inputFormatter = JDateTimeFormatter.ofPattern("MM/dd/yyyy");
    	var date = JLocalDate.from(inputFormatter.parse(dateString));
    	var outputFormatter = JDateTimeFormatter.ofPattern("yyyy-MM-dd");
    	return date.format(outputFormatter);
    }
};

var runTimeMapper = function(x) {
	return dateMapperFunction(x.getGraduationDate());
};

var greet = function(person) {
	return "Hello " + person.getName();
};