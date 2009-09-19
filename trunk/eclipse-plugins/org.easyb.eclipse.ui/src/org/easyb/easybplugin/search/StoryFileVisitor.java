package org.easyb.easybplugin.search;

import java.util.ArrayList;
import java.util.List;

import org.easyb.easybplugin.utils.StoryFileMatcher;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Matches files on *.story file extension and stores a list 
 * of those found
 * @author whiteda
 *
 */
public class StoryFileVisitor implements IResourceProxyVisitor{
	
	private List<IFile> storyFiles = new ArrayList<IFile>();
	//TODO exclude output folders as otherwise stories 
	//could be included twice if part of a package
	@Override
	public boolean visit(IResourceProxy proxy) throws CoreException {
		
		//Don`t want hidden,none accessibkle or derived (those in output folder)
		if(StoryFileMatcher.isStoryFile(proxy)){			
			storyFiles.add((IFile)proxy.requestResource());
		}
		
		return true;
	}
	
	public List<IFile> getFiles(){
		return storyFiles;
	}

}