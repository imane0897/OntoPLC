package org.mm.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mm.core.TransformationRule;
import org.mm.core.TransformationRuleSet;

public class PLCTransformationRuleModel implements TransformationRuleModel {
    private TransformationRuleSet ruleSet;

    private List<TransformationRule> cache = new ArrayList<TransformationRule>();

    public PLCTransformationRuleModel() {
        this(new TransformationRuleSet());
    }

    public PLCTransformationRuleModel(TransformationRuleSet ruleSet) {
        if (ruleSet == null) {
            throw new ApplicationStartupException("Transformation rule set cannot be null");
        }
        changeTransformationRuleSet(ruleSet);
    }

    @Override
    public List<TransformationRule> getRules() {
        return Collections.unmodifiableList(cache);
    }

    public void changeTransformationRuleSet(TransformationRuleSet ruleSet) {
        this.ruleSet = ruleSet;
        fireModelChanged();
    }

    private void fireModelChanged() {
        cache.clear(); // reset the cache
        for (TransformationRule rule : ruleSet) {
            cache.add(rule);
        }
    }

    public TransformationRule getRule(int index) {
        return cache.get(index);
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public boolean contains(TransformationRule rule) {
        return cache.contains(rule);
    }

    public void addMappingExpression(TransformationRule rule) {
        cache.add(rule);
    }

    public void removeMappingExpression(TransformationRule rule) {
        cache.remove(rule);
    }
}
