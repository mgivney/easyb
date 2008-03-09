package org.disco.easyb;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class EasybConfigurationTypeComponent implements ConfigurationType {
    public String getDisplayName() {
        return "Easyb";
    }

    public String getConfigurationTypeDescription() {
        return "Easyb Run Configuration";
    }

    public Icon getIcon() {
        return IconLoader.getIcon("/easyb.png");
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{new EasybConfigurationFactory(this)};
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return "EasybConfigurationTypeComponent";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }
}
