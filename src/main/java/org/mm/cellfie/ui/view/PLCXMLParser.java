package org.mm.cellfie.ui.view;

import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

public class PlcXmlParser {

  public Set<String> ruleTree;
  static int variableIndex = 1;

  class RuleTreeNode {
    String individualName;
    String typeName;
    String facts;

    @Override
    public String toString() {
      String axiom = "Individual: " + individualName;

      if (typeName.isEmpty()) {
        typeName = "project";
      }
      axiom = axiom + "\n" + "Types: " + typeName;

      if (!facts.isEmpty()) {
        axiom = axiom + "\n" + "Facts: " + facts;
      }

      return axiom;
    }
  };

  public PlcXmlParser() {
    this.ruleTree = new HashSet<String>();
  }

  public Set<String> treeWalk(Document document) {
    // initialization
    variableIndex = 1;

    treeWalk(document.getRootElement());
    return ruleTree;
  }

  public void treeWalk(Element element) {
    // fastloop
    for (int i = 0, size = element.nodeCount(); i < size; i++) {
      Node node = element.node(i);
      if (node instanceof Element) {
        treeWalk((Element) node);
      }
    }

    RuleTreeNode n = new RuleTreeNode();
    switch (element.getName()) {
    case "variable":
      if (element.attributeValue("name") != null) {
        n.individualName = "variable" + Integer.toString(variableIndex);
        n.typeName = "variable";
        n.facts = "hasVariableName \"" + element.attributeValue("name") + "\"";
        variableIndex++;
        ruleTree.add(n.toString());
      }
      break;

    case "leftPowerRail":
      if (element.attributeValue("localId") != null) {
        n.individualName = "leftPowerRail" + element.attributeValue("localId");
        n.typeName = "leftPowerRail";
        n.facts = "hasLocalId " + element.attributeValue("localId");
        ruleTree.add(n.toString());
      }
      break;

    case "rightPowerRail":
      if (element.attributeValue("localId") != null) {
        n.individualName = "rightPowerRail" + element.attributeValue("localId");
        n.typeName = "rightPowerRail";
        n.facts = "hasLocalId " + element.attributeValue("localId");
        ruleTree.add(n.toString());
      }
      break;

    case "contact":
      if (element.attributeValue("localId") != null) {
        n.individualName = "contact" + element.attributeValue("localId");
        n.typeName = "contact";
        n.facts = "hasLocalId " + element.attributeValue("localId");
        if (element.attributeValue("negated") != null) {
          n.facts += "\nFacts: isNegatedContact " + element.attributeValue("negated");
        }
        ruleTree.add(n.toString());
      }
      break;

    case "coil":
      if (element.attributeValue("localId") != null) {
        n.individualName = "coil" + element.attributeValue("localId");
        n.typeName = "coil";
        n.facts = "hasLocalId " + element.attributeValue("localId");
        n.facts += "\nFacts: hasCoilType \"" + element.attributeValue("storage") + "\"";
        ruleTree.add(n.toString());
      }
      break;

    case "block":
      if (element.attributeValue("typeName") != null) {
        if (element.attributeValue("instanceName") != null) {
          n.individualName = "FunctionBlock" + element.attributeValue("localId");
        } else {
          n.individualName = "Function" + element.attributeValue("localId");
        }
        n.typeName = element.attributeValue("typeName");
        n.facts = "hasLocalId " + element.attributeValue("localId");
        if (element.attributeValue("instanceName") != null) {
          n.facts += "\nFacts: hasInstanceName \"" + element.attributeValue("instanceName") + "\"";
        }
        ruleTree.add(n.toString());
      }
      break;

    case "pou":
      if (element.attributeValue("name") != null) {
        n.individualName = element.attributeValue("name");
        n.typeName = "POU";
        n.facts = "hasPouType \"" + element.attributeValue("pouType") + "\"";
        ruleTree.add(n.toString());
      }
      break;

    case "action":
      if (element.attributeValue("name") != null) {
        n.individualName = element.attributeValue("name");
        n.typeName = "Action";
        n.facts = "hasActionName \"" + element.attributeValue("name") + "\"";
        ruleTree.add(n.toString());
      }
      break;
    }
  }
}