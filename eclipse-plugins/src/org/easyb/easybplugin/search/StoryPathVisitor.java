package org.easyb.easybplugin.search;

import java.util.ArrayList;
import java.util.List;

import org.easyb.easybplugin.IEasybLaunchConfigConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Matches any files in a resource which end with .story
 * @author whiteda
 *
 */
public class StoryPathVisitor implements IResourceProxyVisitor{
	private List<String> storyPaths = new ArrayList<String>();
	
	//TODO exclude output folders as otherwise stories 
	//could be included twice if part of a package
	@Override
	public boolean visit(IResourceProxy proxy) throws CoreException {
		
		if(IResource.FILE == proxy.getType() && !proxy.isHidden()){
			IFile file = (IFile)proxy.requestResource();
			if(file.getFileExtension().equals(IEasybLaunchConfigConstants.STORY_FILE_EXTENSION)){
				storyPaths.add(file.getRawLocation().toOSString());
			}
		}
		
		return true;
	}
	
	public List<String> getPaths(){
		return storyPaths;
	}

}
