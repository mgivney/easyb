package org.easyb.launch.launcher;

import java.util.ArrayList;
import java.util.List;

import org.easyb.launch.ILaunchConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

/**
 * Used to launch stories using the BehaviourRunner in a separate JVM
 * @author whiteda
 *
 */
public class BehaviourLaunchConfigurationDelegate extends JavaLaunchDelegate{
	public static final String ID = "org.easyb.launch.launcher.BehaviourLaunchConfigurationDelegate"; 
	
	private String os = Platform.getOS();
	
	//Method used for testing only
	public void setPlatformOS(String os){
		this.os = os;
	}
	
	@Override
	public String getMainTypeName(ILaunchConfiguration configuration) throws CoreException {
		return ILaunchConstants.BEHAVIOR_RUNNER_CLASS;
	}
	
	/**
	 * Need to add the plugin jars to the classpath as well as any user jars
	 * @return
	 */
	@Override
	public String[] getClasspath(ILaunchConfiguration config)throws CoreException{
		//TODO possible issue with clash of jar file versions with 
		//easyb and users classpath.Handle this.
		return ClasspathBuilder.getRuntimeClasspath(super.getClasspath(config));
	}
	
	/**
	 * Add additional commands to the command line. Such as the stories to be executed
	 */
	@Override
	public String getProgramArguments(ILaunchConfiguration config)throws CoreException{
		
		StringBuilder args = new StringBuilder(super.getProgramArguments(config));
		
		List<String> storyPaths = 
			config.getAttribute(ILaunchConstants.LAUNCH_ATTR_STORIES_FULL_PATH,new ArrayList(0));
		
		for(String storyPath : storyPaths){
			args.append(" ");
			
			//Wrap in " encase of spaces on windows
			if(os.equals(Platform.OS_WIN32)){
				args.append("\"").append(storyPath).append("\"");
			}else{
				args.append(storyPath);
			}
		}
		
		return args.toString();
	}

}
