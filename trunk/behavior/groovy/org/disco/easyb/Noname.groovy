package org.disco.easyb

var = "string"

it "should have a length of 6", {
	ensure(var.length()){
		isEqualTo6
	}
}

