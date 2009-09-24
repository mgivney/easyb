package org.easyb.eclipse.templates.processor;

import java.io.IOException;
import java.io.Reader;

import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.jdt.internal.ui.text.template.contentassist.TemplateEngine;
import org.eclipse.jface.text.templates.TemplateContextType;


public class BehaviourTemplateEngine extends TemplateEngine {
	private TemplateContextType ctxType;
	public BehaviourTemplateEngine(TemplateContextType contextType) {
		super(contextType);
		ctxType = contextType;
	}

	
}
