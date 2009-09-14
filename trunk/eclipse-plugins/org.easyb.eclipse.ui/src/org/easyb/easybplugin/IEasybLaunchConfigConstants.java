package org.easyb.easybplugin;

/**
 * Constants to be used by plugin
 * @author whiteda
 *
 */
//TODO Refactor to seperate constants file probably IStoryFileConstants 
//and ILaunchConstants
public interface IEasybLaunchConfigConstants {
	public static final String LAUNCH_ATTR_CONTAINER_HANDLE= "EASYB_CONTAINER_HANDLE";
	public static final String LAUNCH_ATTR_STORY_PATH = "EASYB_FILE_PATH";
	public static final String LAUNCH_ATTR_STORIES_FULL_PATH = "EASYB_STORIES_FULL_PATH";
	public static final String BEHAVIOR_RUNNER_CLASS = "org.easyb.BehaviorRunner";
	public static final String EASYB_RUNNER_LIB_NAME = "resources/easyblib";
}