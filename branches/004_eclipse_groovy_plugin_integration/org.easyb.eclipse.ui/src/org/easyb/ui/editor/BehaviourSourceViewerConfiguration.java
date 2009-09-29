package org.easyb.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.eclipse.editor.GroovyColorManager;
import org.codehaus.groovy.eclipse.editor.GroovyConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

public class BehaviourSourceViewerConfiguration extends GroovyConfiguration{

	private ITextEditor textEditor;
	private BehaviourTagScanner tagScanner;
	public BehaviourSourceViewerConfiguration(GroovyColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor textEditor){
			super(colorManager,preferenceStore,textEditor);
			this.textEditor = textEditor;
			this.tagScanner = new BehaviourTagScanner(colorManager); 
	}
	
	//TODO DELETE 
	//cursor is in the document
/*	DONT USE AS USE A JavaCompletionComputer INstead @Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = (ContentAssistant)super.getContentAssistant(sourceViewer);
		
	//	ContentAssistant assistant= new ContentAssistant();
	//	assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		IContentAssistProcessor processor= new BehaviourCompletionProcessor();
		assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);

		//assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		//assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));

		return assistant;
	}*/
	
    @Override
    protected RuleBasedScanner getCodeScanner() {
        return tagScanner;
    }
	
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
	{
		List<String> contentTypes = new ArrayList<String>();
		Collections.addAll(contentTypes,PartitionScannerBuilder.EASYB_ALL_PARTITION_TYPES);
		Collections.addAll(contentTypes,super.getConfiguredContentTypes(sourceViewer));
		
		return contentTypes.toArray(new String[contentTypes.size()]);
	}
	
    
	/*@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return PartitionScannerBuilder.PARTITIONER_ID;
	}*/
	
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer){

		//Todo change editor to implement a listener interface 
		//rather then doing this nasty cast
		IReconciler reconciler = 
			new MonoReconciler(new BehaviourReconcilerStrategy((BehaviourEditor)textEditor),false); 
		return reconciler;	
	}
}
