package org.mm.app;

import org.mm.core.OWLOntologySource;
import org.mm.core.TransformationRuleSet;
// import org.mm.ss.SpreadSheetDataSource;

public class PLCApplication {
    private PLCApplicationModel applicationModel;

    public PLCApplication(OWLOntologySource ontologySource, TransformationRuleSet ruleSet) {
        applicationModel = new PLCApplicationModel(ontologySource, ruleSet);
    }

    public PLCApplicationModel getApplicationModel() {
        return applicationModel;
    }
}
