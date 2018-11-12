package org.mm.app;

import org.mm.core.OWLOntologySource;
import org.mm.core.TransformationRuleSet;
import org.mm.renderer.Renderer;
import org.mm.renderer.owlapi.OWLRenderer;
import org.mm.renderer.text.TextRenderer;

public class PLCApplicationModel implements ApplicationModel{
    private final OWLOntologySource ontologySource;
    private final Renderer applicationRenderer;
    private final PLCTransformationRuleModel expressionMappingsModel;

    public PLCApplicationModel(OWLOntologySource ontologySource, TransformationRuleSet ruleSet) {
        this.ontologySource = ontologySource;

        applicationRenderer = new OWLRenderer(ontologySource);
        expressionMappingsModel = new PLCTransformationRuleModel(ruleSet);
    }

    public PLCTransformationRuleModel getTransformationRuleModel() {
        return expressionMappingsModel;
    }

    public Renderer getDefaultRenderer() {
        return applicationRenderer;
    }

    public TextRenderer getLogRenderer() {
        TextRenderer renderer = new TextRenderer();
        renderer.setComment(true);
        return renderer;
    }
}
