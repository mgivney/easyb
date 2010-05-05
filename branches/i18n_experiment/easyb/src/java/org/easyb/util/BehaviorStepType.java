package org.easyb.util;

import java.util.Locale;
import java.util.ResourceBundle;

public enum BehaviorStepType {

    GENESIS("genesis"),
    SPECIFICATION("specification"),
    STORY("story"),
    EXECUTE("execute"),
    SCENARIO("scenario"),
    GIVEN("given"),
    WHEN("when"),
    THEN("then"),
    AND("and"),
    BEFORE("before"),
    AFTER("after"),
    IT("it"),
    DESCRIPTION("description"),
    NARRATIVE("narrative"),
    NARRATIVE_ROLE("role"),
    NARRATIVE_FEATURE("feature"),
    NARRATIVE_BENEFIT("benefit");

    private ResourceBundle strings = ResourceBundle.getBundle("org.easyb.i18n.BehaviorStepType", Locale.getDefault());

    private final String type;

    BehaviorStepType(String type) {
        this.type = strings.getString( type );
    }

    public String type() {
        return type;
    }
    
    public int getSoftTabs(BehaviorStepType genesis) {
        int softTabs = 0;
        switch(this) {
            case STORY:
            case SPECIFICATION:
                softTabs = 2;
                break;
            case DESCRIPTION:
            case NARRATIVE:
                softTabs = 3;
                break;
            case SCENARIO:
            case BEFORE:
            case AFTER:
            case IT:
                softTabs = 4;
                break;
            case GIVEN:
            case WHEN:
            case THEN:
            case NARRATIVE_ROLE:
            case NARRATIVE_FEATURE:
            case NARRATIVE_BENEFIT:
                softTabs = 6;
                break;
            case AND:
                softTabs = genesis.equals(BehaviorStepType.STORY) ? 6 : 4;
                break;
                
        }
        return softTabs;
    }
    
    public String format(BehaviorStepType genesis) {
        String format = type;
        switch(this) {
            case STORY:
            case SPECIFICATION:
            case NARRATIVE:
                format = type.substring(0, 1).toUpperCase() + type.substring(1) + ":";
                break;
            case NARRATIVE_ROLE:
                format = "As a";
                break;
            case NARRATIVE_FEATURE:
                format = "I want";
                break;
            case NARRATIVE_BENEFIT:
                format = "So that";
                break;
            case DESCRIPTION:
                format = genesis.equals(BehaviorStepType.STORY) ? type.substring(0, 1).toUpperCase() + type.substring(1) + ":" : "";
                break;
        }
        return format;
    }
}
