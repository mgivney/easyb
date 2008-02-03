package org.disco.easyb

import org.disco.easyb.core.delegates.EnsuringDelegate
import org.disco.easyb.core.result.Result
import org.disco.easyb.core.delegates.PlugableDelegate
import org.disco.easyb.core.exception.VerificationException

class SpecificationBinding {

	
  static{
	  ExpandoMetaClass.enableGlobally()

	  def isEqualTo = { value ->
		  if(value.getClass() == String.class){
			  if(!value.toString().equals(delegate.toString())){
				  throw new VerificationException("expected ${value.toString()} but was ${delegate.toString()}")
			  }
		  }else{
			  if(value != delegate){
				  throw new VerificationException("expected ${value} but was ${delegate}")
			  }
		  }
	  }
	  
	  def isNotEqualTo = { value ->
		  if(value.getClass() == String.class){
			  if(value.toString().equals(delegate.toString())){
				  throw new VerificationException("expected values to differ but both were ${value.toString()}")
			  }
		  }else{
			  if(value == delegate){
				  throw new VerificationException("expected values to differ but both were ${value}")
			  }
		  }
	  }
	  
	  def isA = { type ->
	  	if(!type.equals(delegate.getClass())){
	  		throw new VerificationException("expected ${type} but was ${delegate.getClass()}")
	  	}
	  }
	  
	  def isNotA = {  type ->
	  	if(type.equals(delegate.getClass())){
			throw new VerificationException("expected ${type} but was ${delegate.getClass()}")
		}
	  }
	  
	  def has = { value ->
	  	  
		  if(delegate instanceof Map){
			  if(value instanceof Map){
				  handleMapContains(delegate, value)
				  
			  }else{
				if(!delegate.containsKey(value)){
					if(!delegate.containsValue(value)){
						throw new VerificationException("${delegate.toString()} doesn't contain ${value.toString()} as a key or a value")
					}
				}
		      }
		   }else{
		  	if(value instanceof String){
				  if(!delegate.toString().contains(value.toString())){
					  throw new VerificationException("${delegate.toString()} doesn't contain ${value.toString()}")
			  		}
		 	 }else if(value instanceof Collection){
				  if(!delegate.containsAll(value)){
					  throw new VerificationException("${delegate} doesn't contain ${value}")
			  	}
		 	 }else if(value instanceof Map){
		 		def outval =  "Warning! The has expando method isn't working " +
			  	   "100% with JavaBean-like Objects. ${delegate.toString()} invoking has w/${value} may not work " +
			  	   "as you intend."
			  	   println outval
		 		 //println "helo, delegate is ${delegate}"
		 		 value.each{ ky, vl ->
		 		 	//println "${ky}, ${vl}"
		 		 	//delegate.getDeclaredMethods().each{
		 		 	//	println it
		 		 	//}
		 		 	//delegate.
		 		 	def fld = delegate.getClass().getDeclaredField(ky)
		 		 	//println "${fld}"
		 		 	fld.setAccessible(true)
		 		 	def ret = fld.get(delegate)
		 		 	if(ret.getClass() instanceof String){
		 		 		if(!ret.equals(vl)){
		 		 			throw new VerificationException("${delegate.getClass().getName()}.${ky} doesn't equal ${vl}")
		 		 		}
		 		 	}else{
		 		 		if(ret != vl){
		 		 			throw new VerificationException("${delegate.getClass().getName()}.${ky} doesn't equal ${vl}")
		 		 		}
		 		 	}
		 		 }
		  	 }else{
				  if(!delegate.contains(value)){
					  throw new VerificationException("${delegate} doesn't contain ${value}")
			  	}
		  	}
		  }
	  }
	  /**
	   *
	   */
	  def handleMapContains = { java.util.LinkedHashMap delegate, java.util.LinkedHashMap values ->
		def foundcount = 0
		values.each { key, val ->
			delegate.each{ vkey, vvalue ->
				if((vkey.equals(key) && (val == vvalue))){
					foundcount++
	 			}
			}
		}
		if(foundcount != values.size()){
			throw new VerificationException("values ${values} not found in ${delegate}")
		}
	  }
	
	  Object.metaClass.shouldBe = isEqualTo
	  Object.metaClass.shouldBeEqual = isEqualTo
	  Object.metaClass.shouldBeEqualTo = isEqualTo
	  Object.metaClass.shouldEqual = isEqualTo
	  
	  //these need some renaming
	  Object.metaClass.is = isEqualTo
	  Object.metaClass.isnt = isNotEqualTo
	  Object.metaClass.isNot = isNotEqualTo
	  
	  Object.metaClass.shouldNotBe = isNotEqualTo
	  Object.metaClass.shouldNotEqual = isNotEqualTo
	  Object.metaClass.shouldntBe = isNotEqualTo
	  Object.metaClass.shouldntEqual = isNotEqualTo
	  Object.metaClass.shouldBeA = isA
	  Object.metaClass.shouldNotBeA = isNotA
	  Object.metaClass.shouldBeAn = isA
	  Object.metaClass.shouldNotBeAn = isNotA
	  Object.metaClass.handleMapContains = handleMapContains
	  Object.metaClass.shouldHave = has
	  
  }
  // TODO change to constants when i break the binding into story and behavior bindings
  public static final String STORY = "story"
  public static final String STORY_SCENARIO = "scenario"
  public static final String STORY_GIVEN = "given"
  public static final String STORY_WHEN = "when"
  public static final String STORY_THEN = "then"
  public static final String BEHAVIOR_IT = "it"
  public static final String AND = "and"

  /**
	 * This method returns a fully initialized Binding object (or context) that 
	 * has definitions for methods such as "it" and "given", which are used
	 * in the context of behaviors (or stories). 
	 */
  static Binding getBinding(listener){
  	def binding = new Binding()

    def basicDelegate = basicDelegate()
    def givenDelegate = givenDelegate()

    def beforeIt
	
    binding.scenario = { scenarioDescription, scenarioClosure ->
      listener.gotResult(new Result(scenarioDescription, STORY_SCENARIO, Result.SUCCEEDED))
      scenarioClosure()
    }

    binding.before = { beforeDescription, closure ->
      beforeIt = closure
    }

    def itClosure = { spec, closure, storyPart ->
      closure.delegate = basicDelegate
      
      try{
        if(beforeIt != null){
          beforeIt()
        }
        closure()
        listener.gotResult(new Result(spec, storyPart, Result.SUCCEEDED))
      }catch(ex){
        listener.gotResult(new Result(spec, storyPart, ex))
      }
    }

    binding.it = { spec, closure ->
      itClosure(spec, closure, BEHAVIOR_IT)
    }

    binding.then = {spec, closure ->
      itClosure(spec, closure, STORY_THEN)
    }
        		  
	binding.when = { whenDescription, closure ->
		closure.delegate = basicDelegate
		closure()
		listener.gotResult(new Result(whenDescription, STORY_WHEN, Result.SUCCEEDED))
	}
	
	binding.given = { givenDescription, closure ->
		closure.delegate = givenDelegate
		closure()
        listener.gotResult(new Result(givenDescription, STORY_GIVEN, Result.SUCCEEDED))
	}
		
	binding.and = {
  	  //println "in and!!"
      listener.gotResult(new Result("", AND, Result.SUCCEEDED))
	}
	  		  
	 return binding
	}

	/**
	 * The easy delegate handles "it", "then", and "when"
	 * Currently, this delegate isn't plug and play.
	 */
	private static basicDelegate(){
		return new EnsuringDelegate()
	}
	/** 
	 * The "given" delegate supports plug-ins, consequently, 
	 * the flex guys are utlized. Currently, there is a DbUnit
	 * "given" plug-in and it is envisioned that there could be 
	 * others like Selenium. 
	 */
	private static givenDelegate(){
		return new PlugableDelegate()
	}
}