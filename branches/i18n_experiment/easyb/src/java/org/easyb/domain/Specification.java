package org.easyb.domain;

import groovy.lang.GroovyShell;
import org.easyb.BehaviorStep;
import org.easyb.Configuration;
import org.easyb.SpecificationBinding;
import org.easyb.util.BehaviorStepType;
import org.easyb.listener.ExecutionListener;

import java.io.File;
import java.io.IOException;

public class Specification extends BehaviorBase {
    public Specification(GroovyShellConfiguration gShellConfig, String phrase, File file) {
        super(gShellConfig, phrase, file);
    }

    public Specification(GroovyShellConfiguration gShellConfig, String phrase, File file, Configuration config) {
        super(gShellConfig, phrase, file, config);
    }

    public String getTypeDescriptor() {
        return "specification";
    }

    public BehaviorStep execute(ExecutionListener listener) throws IOException {
        BehaviorStep currentStep = new BehaviorStep(BehaviorStepType.SPECIFICATION, getPhrase());

        listener.startBehavior(this);
        listener.startStep(currentStep);

        // need to discover locale before setting the binding because the bindings
        // method names depend on the locale we're using
        String specification = new LocalePreProcessor().process( getFile() );

        setBinding(SpecificationBinding.getBinding(listener) );
        GroovyShell g = new GroovyShell(getClassLoader(), getBinding());
        bindShellVariables(g);

        listener.startStep( new BehaviorStep (BehaviorStepType.EXECUTE, getPhrase()) );
        
        // match this to what goes on in Story.java
        g.evaluate (specification, getFile().getAbsolutePath());

        listener.stopStep(); // EXEC
        listener.stopStep(); // SPECIFICATION
        listener.stopBehavior(currentStep, this);

        return currentStep;
    }
}