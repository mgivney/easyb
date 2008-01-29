package org.disco.easyb;

int value = 0

before "increment the int value", {
	value++
}

it "should be 1 now", {
	ensure(value) {
    isEqualTo1
  }
}

it "should be 2 now",{
	ensure(value) { isEqualTo2 }
}