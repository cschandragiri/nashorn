load("https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.8.3/underscore-min.js");

var filter = function(name) {
    return name.getName() != null && name.getAge() > 18
};

var limit = function() {
	return 15
};

var comparator = function(name1, name2) {
	var result = name1.getName()
		.compareTo(name2.getName())
	if (result == 0)
	{
		return name1.getAge()
			.compareTo(name2.getAge())
	}
	else
	{
		return result
	}
};

var JLocalDate = Java.type('java.time.LocalDate');
var JDateTimeFormatter = Java.type("java.time.format.DateTimeFormatter");
var JFunction = Java.type('java.util.function.Function');

var dateMapperFunction = new JFunction() {
    apply: function(person) {
	    	if (_.isNull(person.getGraduationDate())) {
	    		logger.error("Date String is null for user: " + person.getName())
	    		return
	    	}
	    	var inputFormatter = JDateTimeFormatter.ofPattern("MM/dd/yyyy")
	    	var date = JLocalDate.from(inputFormatter.parse(person.getGraduationDate()))
	    	var outputFormatter = JDateTimeFormatter.ofPattern("yyyy-MM-dd")
	    	return date.format(outputFormatter)
    }
};

var ageMapperFunction = new JFunction() {
    apply: function(person) {
	    	if (_.isNull(person.getAge())) {
	    		logger.error("Age is null for user: " + person.getName())
	    		return
	    	}
	    return person.getAge()
    }
};

var runTimeMapper = function(x) {
	//return dateMapperFunction(x);
	return ageMapperFunction(x)
};

var greet = function(person) {
	return "Hello " + person.getName()
};