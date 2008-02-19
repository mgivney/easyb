package org.disco.easyb.core.delegates;

/**
 * this is essentially a marker interface
 * @author aglover
 *
 */
public interface RichlyEnsurable extends Plugable{
	/**
	 * ideally, flexible delegates can do interesting things to 
	 * the object passed in....
	 * 
	 * @param verified
	 */
	void setVerified(Object verified);
}
