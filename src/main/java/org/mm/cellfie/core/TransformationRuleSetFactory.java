package org.mm.cellfie.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

public class TransformationRuleSetFactory {
    public static TransformationRuleSet createEmptyTransformationRuleSet() {
        return new TransformationRuleSet();
    }

    public static TransformationRuleSet loadTransformationRulesFromDocument(String location)
            throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(location));
        return new Gson().fromJson(br, TransformationRuleSet.class);
    }

    public static void saveTransformationRulesToDocument(String location, List<TransformationRule> rules)
            throws IOException {
        String json = new Gson().toJson(TransformationRuleSet.create(rules));
        FileWriter writer = new FileWriter(location);
        writer.write(json);
        writer.close();
    }
}
