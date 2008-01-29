package org.disco.bdd.behavior.zip

import org.disco.bdd.zip.ZipCodeValidator

before "initialize zipcodevalidator instance", {
	 zipvalidate = new ZipCodeValidator();
}

it "should deny invalid zip codes", {
	["221o1", "2210", "22010-121o"].each{ zip ->
		ensure(zipvalidate.validate(zip)) { 
			isFalse 
		}
	}
}

it "should accept valid zip codes", {
	["22101", "22100", "22010-1210"].each{ zip ->
		ensure(zipvalidate.validate(zip)) { 
			isTrue
		}
	}
}