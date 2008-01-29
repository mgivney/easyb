package org.disco.easyb

import org.disco.easyb.core.listener.SpecificationListener
import org.disco.easyb.core.listener.DefaultListener


before "obtain a reference to the binding object", {
  SpecificationListener behaviorListener = new DefaultListener()
  binding = SpecificationBinding.getBinding(behaviorListener)
}

it "should have an it method which isn't null", {
	itmethod = binding.it
	ensure(itmethod) {
    isNotNull
  }
}

it "should accept 2 parameters", {
	itmethod = binding.it
	ensure(itmethod.getMaximumNumberOfParameters()) {
    isEqualTo(2)
  }
}

it "should accept a String and a closure and not throw any exceptions",{
	itmethod = binding.it
	try{
		itmethod("test"){
			ensure(1) {
        isEqualTo(1)
      }
		}
	}catch(Throwable thr){
		fail("apparently the it closure isn't working")
	}
}

it "should create a delegate for the passed in closure",{
	itmethod = binding.it
	xclos = { 
		ensure(1) {
      isEqualTo(1)
    }
	}
	try{
		itmethod("blah", xclos)
		ensure(xclos.delegate.class.name) {
      		isEqualTo("org.disco.easyb.core.delegates.EnsuringDelegate")
    	}
	}catch(Throwable thr){
		fail("apparently the it closure isn't working")
	}
}