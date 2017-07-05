//Anonymous function
(function() {
	
	var Person = Java.type('com.sandbox.samples.Person');
	var PersonExtender = Java.extend(Person);
	var obj = new PersonExtender('Anita', 21, '01/05/2017') {
		toString: function() {
			return "Hello " + obj.getName();
		}
	}
	return obj;
}());