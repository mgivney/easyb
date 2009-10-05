package org.easyb.eclipse.templates.manager;

import org.eclipse.jface.text.templates.Template;

import junit.framework.TestCase;

public class TemplateManagerTest extends TestCase{

	private static final String GIVEN_TEMPLATE = "given";

	public void testGetTemplates(){
		Template[] templates = 
			TemplateManager.getInstance().getTemplates();
		
		assertNotNull(templates);
		
		for(Template template : templates){
			assertNotNull(template.getPattern());
			assertNotNull(template.getName());
			assertTrue(template.getPattern().trim().length()>0);
			assertTrue(template.getName().trim().length()>0);
		}
		
	}
	
	public void testGetTemplate(){
		Template template = 
			TemplateManager.getInstance().getTemplate("given");
		
		assertNotNull(template);
		assertTrue(template.getPattern().trim().length()>0);
		assertTrue(template.getName().trim().length()>0);
	}
	
	public void testGetEmptyDocument()throws Exception{
		Template template = 
			TemplateManager.getInstance().getTemplate(GIVEN_TEMPLATE);
		
		String pattern = 
			TemplateManager.getInstance().getEmptyDocumentResolvedPattern(template);
		
		assertTrue(pattern.length()>0);
		assertFalse(pattern.contains("$"));
	}
	
	
	
}
