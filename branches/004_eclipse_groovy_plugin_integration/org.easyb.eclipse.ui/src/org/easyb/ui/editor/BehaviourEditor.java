package org.easyb.ui.editor;

import org.codehaus.groovy.eclipse.GroovyPlugin;
import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.codehaus.groovy.eclipse.editor.GroovyTextTools;
import org.easyb.ui.EasybUIActivator;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class BehaviourEditor extends GroovyEditor
{
	private BehaviourOutlinePage outlinePage = null;
	
	public BehaviourEditor(){
	}
	
	public void updateOutline(){
		outlinePage.update();
	}
	
	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (outlinePage == null) {
				outlinePage= new BehaviourOutlinePage(this);
				if (getEditorInput() != null){
					outlinePage.setInput(getEditorInput());
				}
			}
			return outlinePage;
		}
		return super.getAdapter(required);
	}
	
	@Override
	public void dispose(){
		super.dispose();
		if (outlinePage != null){
			outlinePage.setInput(null);
		}
	}
	
	 protected void setPreferenceStore(IPreferenceStore store) {
		 super.setPreferenceStore(store);
		 GroovyTextTools textTools= GroovyPlugin.getDefault().getTextTools();
		 setSourceViewerConfiguration(new BehaviourSourceViewerConfiguration(textTools.getColorManager(),getPreferenceStore(),this));
	 }
	 
}
