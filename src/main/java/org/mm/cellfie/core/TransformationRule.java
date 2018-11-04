package org.mm.cellfie.core;

public class TransformationRule {

    private final String comment;
    private final String rule;

    private boolean active = false;

    public TransformationRule(String comment, String rule) {
        this.active = true;
        this.comment = comment;
        this.rule = rule;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public String getRuleString() {
        return rule;
    }

    public String getComment() {
        return comment;
    }

    public String toString() {
        return "TransformationRule [" + "expression=" + rule + ", " + "comment=" + comment + ", " + "active=" + active
                + "]";
    }
}