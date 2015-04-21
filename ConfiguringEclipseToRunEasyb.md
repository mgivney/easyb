Configuring Eclipse to run easyb stories is easy!


# Details #

First, all three easyb libraries (`commons-cli-1.1.jar`, `groovy-1.5.4.jar`, and `easyb-0.9.jar`) must be in your project's classpath. Next, select the **Run** menu item and then select **Run Configurations**. In this dialog box, you'll select **Java Application**-- right click and select the **New** option.


Clicking the **New** option will create a dialog where you can enter in a few values-- namely, the **Name** of the configuration, the **Project**, and the **Main class**. You can give any name you'd like for the configuration (such as "easyb story runner") and of course, the project is your Eclipse project you'd like to have the runner available for.

For the main class, click the "Include system libraries when searching for a main class" option and then press the **Search** button. In the new dialog that pops up, start typing "Behavior" and you should see a class called `BehaviorRunner`-- select it!

easyb's `BehaviorRunner` has a few arguments-- namely, the story you'd like to run and some optional flags for reports. At a minimum, you must provide a story. Accordingly, click the **Arguments** tab and in the **Program arguments** text box, put the full path to a story, such as `./stories/NPVCalculations.story`. Next, hit the **Apply** button to save what you've done thus far.

Now, you should be able to run the `BehaviorRunner` against your supplied story by hitting the **Run** button. Depending on what your story does, you will see the output in the Console