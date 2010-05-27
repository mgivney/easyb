package org.easyb.domain;

import groovy.lang.GroovyShell;
import org.easyb.BehaviorStep;
import org.easyb.Configuration;
import org.easyb.StoryBinding;
import org.easyb.listener.ExecutionListener;
import org.easyb.plugin.EasybPlugin;
import org.easyb.plugin.PluginFactory;
import org.easyb.util.BehaviorStepType;
import org.easyb.util.PreProcessorable;

import java.io.*;

public class Story extends BehaviorBase {
  private boolean executeStory = true;

  public Story(GroovyShellConfiguration gShellConfig, String phrase, File file) {
    super(gShellConfig, phrase, file);
  }

  public Story(GroovyShellConfiguration gShellConfig, String phrase, File file, Configuration config, boolean executeStory) {
    super(gShellConfig, phrase, file, config);

    this.executeStory = executeStory;
  }

  public String getTypeDescriptor() {
    return "story";
  }

  public BehaviorStep execute(ExecutionListener listener) throws IOException {
    File file = getFile();
    String story = readStory(file);
    if (containsTag(story, this.getTags())) {
      BehaviorStep currentStep = new BehaviorStep(BehaviorStepType.STORY, getPhrase());

      listener.startBehavior(this);
      listener.startStep(currentStep);

      StoryBinding binding = StoryBinding.getBinding(listener);
      GroovyShell g = new GroovyShell(getClassLoader(), binding);
      bindShellVariables(g);

      setBinding(binding);
      listener.startStep(new BehaviorStep(BehaviorStepType.EXECUTE, getPhrase()));

      //Pass in path to original file so it can be used in debuggers
      g.evaluate(story, file.getAbsolutePath());
      binding.replaySteps(executeStory);
      listener.stopStep(); // EXEC

      listener.stopStep();
      listener.stopBehavior(currentStep, this);

      return currentStep;
    } else {
      return null;
    }
  }

  protected String readStory(File story) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    InputStream input = new BufferedInputStream(new FileInputStream(story));
    int character;
    while (( character = input.read() ) != -1) {
      output.write(character);
    }
    input.close();

    PreProcessorable sharedProcessor = new SharedBehaviorPreProcessor(new NarrativePreProcessor());
    return sharedProcessor.process(output.toString());
  }
}
