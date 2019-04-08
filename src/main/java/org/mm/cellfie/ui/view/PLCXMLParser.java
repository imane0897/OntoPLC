package org.mm.cellfie.ui.view;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Iterator;

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

      if (typeName != null && !typeName.isEmpty()) {
        axiom += "\nTypes: " + typeName;
      } else {
        axiom += "\nTypes: Project";
      }

      if (facts != null && !facts.isEmpty()) {
        axiom += "\nFacts: " + facts;
      }
      return axiom;
    }
  };

  public PlcXmlParser() {
    this.ruleTree = new HashSet<String>();
  }

  public Set<String> parseXml(Document document) {
    Node node = document.selectSingleNode("//*[local-name()='fileHeader']");
    Element element = (Element) node;
    if (element.attributeValue("companyName").equals("Beremiz")) {
      parseBeremizXml(document);
    } else if (element.attributeValue("productName").equals("CoDeSys")) {
      parseCodesysXml(document);
    }
    return ruleTree;
  }

  public void parseBeremizXml(Document document) {
    List<Node> pouList = document.selectNodes("//*[local-name()='pou']");
    for (Iterator<Node> iter = pouList.iterator(); iter.hasNext();) {
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      n.individualName = "Pou_" + element.attributeValue("name");
      n.typeName = "POU";
      n.facts = "hasPouName \"" + element.attributeValue("name") + "\"";
      n.facts += "\nFacts: hasPouType \"" + element.attributeValue("pouType") + "\"";
      ruleTree.add(n.toString());
    }

    List<Node> variableList = document.selectNodes("//*[local-name()='variable'][@name]");
    for (Iterator<Node> iter = variableList.iterator(); iter.hasNext();) {
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      n.individualName = "Variable_" + element.attributeValue("name");
      n.typeName = "Variable";
      n.facts = "hasVariableName \"" + element.attributeValue("name") + "\"";
      ruleTree.add(n.toString());
    }

    List<Node> leftPowerRailList = document.selectNodes("//*[local-name()='leftPowerRail']");
    for (Iterator<Node> iter = leftPowerRailList.iterator(); iter.hasNext();) {
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      n.individualName = "LeftPowerRail" + element.attributeValue("localId");
      n.typeName = "LeftPowerRail";
      n.facts = "hasLocalId " + element.attributeValue("localId");
      n.facts += "\nFacts: hasHeight " + element.attributeValue("height");
      n.facts += "\nFacts: hasWidth " + element.attributeValue("width");
      Node childNode = element.selectSingleNode("*[local-name()='position']");
      if (childNode != null) {
        Element position = (Element) childNode;
        n.facts += "\nFacts: hasPositionX " + position.attributeValue("x");
        n.facts += "\nFacts: hasPositionY " + position.attributeValue("y");
      }
      ruleTree.add(n.toString());
    }

    List<Node> rightPowerRailList = document.selectNodes("//*[local-name()='rightPowerRail']");
    for (Iterator<Node> iter = rightPowerRailList.iterator(); iter.hasNext();) {
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      n.individualName = "RightPowerRail" + element.attributeValue("localId");
      n.typeName = "RightPowerRail";
      n.facts = "hasLocalId " + element.attributeValue("localId");
      n.facts += "\nFacts: hasHeight " + element.attributeValue("height");
      n.facts += "\nFacts: hasWidth " + element.attributeValue("width");
      Node childNode = element.selectSingleNode("*[local-name()='position']");
      if (childNode != null) {
        Element position = (Element) childNode;
        n.facts += "\nFacts: hasPositionX " + position.attributeValue("x");
        n.facts += "\nFacts: hasPositionY " + position.attributeValue("y");
      }
      ruleTree.add(n.toString());
    }

    List<Node> contactList = document.selectNodes("//*[local-name()='contact']");
    for (Iterator<Node> iter = contactList.iterator(); iter.hasNext();) {
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      n.individualName = "RightPowerRail" + element.attributeValue("localId");
      n.typeName = "RightPowerRail";
      n.facts = "hasLocalId " + element.attributeValue("localId");
      n.facts += "\nFacts: hasHeight " + element.attributeValue("height");
      n.facts += "\nFacts: hasWidth " + element.attributeValue("width");
      if (element.attributeValue("negated") != null) {
        n.facts += "\nFacts: isNegatedContact " + element.attributeValue("negated");
      }
      Node childNode = element.selectSingleNode("*[local-name()='position']");
      if (childNode != null) {
        Element position = (Element) childNode;
        n.facts += "\nFacts: hasPositionX " + position.attributeValue("x");
        n.facts += "\nFacts: hasPositionY " + position.attributeValue("y");
      }
      ruleTree.add(n.toString());
    }

    List<Node> coilList = document.selectNodes("//*[local-name()='coil']");
    for (Iterator<Node> iter = coilList.iterator(); iter.hasNext();) {
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      n.individualName = "Coil" + element.attributeValue("localId");
      n.typeName = "Coil";
      n.facts = "hasLocalId " + element.attributeValue("localId");
      n.facts += "\nFacts: hasHeight " + element.attributeValue("height");
      n.facts += "\nFacts: hasWidth " + element.attributeValue("width");
      n.facts += "\nFacts: hasCoilType \"" + element.attributeValue("storage") + "\"";
      Node childNode = element.selectSingleNode("*[local-name()='position']");
      if (childNode != null) {
        Element position = (Element) childNode;
        n.facts += "\nFacts: hasPositionX " + position.attributeValue("x");
        n.facts += "\nFacts: hasPositionY " + position.attributeValue("y");
      }
      ruleTree.add(n.toString());
    }

    List<Node> blockList = document.selectNodes("//*[local-name()='block']");
    for (Iterator<Node> iter = blockList.iterator(); iter.hasNext();) {
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      if (element.attributeValue("instanceName") != null) {
        n.individualName = "FunctionBlock" + element.attributeValue("localId");
        n.typeName = element.attributeValue("typeName");
      } else {
        n.individualName = "Function" + element.attributeValue("localId");
        n.typeName = element.attributeValue("typeName") + "_F";
      }
      n.facts = "hasLocalId " + element.attributeValue("localId");
      n.facts += "\nFacts: hasHeight " + element.attributeValue("height");
      n.facts += "\nFacts: hasWidth " + element.attributeValue("width");
      if (element.attributeValue("instanceName") != null) {
        n.facts += "\nFacts: hasInstanceName \"" + element.attributeValue("instanceName") + "\"";
      }
      // position
      Node childNode = element.selectSingleNode("*[local-name()='position']");
      if (childNode != null) {
        Element position = (Element) childNode;
        n.facts += "\nFacts: hasPositionX " + position.attributeValue("x");
        n.facts += "\nFacts: hasPositionY " + position.attributeValue("y");
      }
      // inputVariables
      childNode = element.selectSingleNode("*[local-name()='inputVariables']");
      if (childNode != null) {
        List<Node> inputVariables = childNode.selectNodes("*");
        for (Iterator<Node> var = inputVariables.iterator(); var.hasNext();) {
          Element varElement = (Element) var.next();
          n.facts += "\nFacts: hasInputVariable \"" + varElement.attributeValue("formalParameter") + "\"";
        }
      }
      // outputVariables
      childNode = element.selectSingleNode("*[local-name()='outputVariables']");
      if (childNode != null) {
        List<Node> outputVariables = childNode.selectNodes("*");
        for (Iterator<Node> var = outputVariables.iterator(); var.hasNext();) {
          Element varElement = (Element) var.next();
          n.facts += "\nFacts: hasOutputVariable \"" + varElement.attributeValue("formalParameter") + "\"";
        }
      }
      ruleTree.add(n.toString());
    }

    List<Node> inVariableList = document.selectNodes("//*[local-name()='inVariable'][@localId]");
    for (Iterator<Node> iter = inVariableList.iterator(); iter.hasNext();){
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      n.individualName = "Variable" + element.attributeValue("localId");
      n.typeName = "Variable";
      n.facts = "hasHeight " + element.attributeValue("height");
      n.facts += "\nFacts: hasWidth " + element.attributeValue("width");
      if (element.attributeValue("executionOrderId") != null) {
        n.facts += "\nFacts: hasExecutionOrderId " + element.attributeValue("executionOrderId");
      }
      if (element.attributeValue("negated") != null) {
        n.facts += "\nFacts: isNegated " + element.attributeValue("negated");
      }
      ruleTree.add(n.toString());
    }

    List<Node> actionList = document.selectNodes("//*[local-name()='action'][@localId]");
    for (Iterator<Node> iter = actionList.iterator(); iter.hasNext();) {
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      n.individualName = "Action" + element.attributeValue("localId");
      n.typeName = "Action";
      n.facts = "hasLocalId " + element.attributeValue("localId");
      if (element.attributeValue("hasActionQualifier") != null) {
        n.facts += "\nFacts: hasActionQualifier \"" + element.attributeValue("qualifier") + "\"";
      }
      if (element.attributeValue("duration") != null) {
        n.facts += "\nFacts: hasActionDuration \"" + element.attributeValue("duration") + "\"";
      }
      ruleTree.add(n.toString());
    }

    List<Node> transitionList = document.selectNodes("//*[local-name()='transition'][@localId]");
    for (Iterator<Node> iter = transitionList.iterator(); iter.hasNext();) {
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      n.individualName = "Transition" + element.attributeValue("localId");
      n.typeName = "Transition";
      n.facts = "hasLocalId " + element.attributeValue("localId");
      Node childNode = element.selectSingleNode("*[local-name()='position']");
      if (childNode != null) {
        Element position = (Element) childNode;
        n.facts += "\nFacts: hasPositionX " + position.attributeValue("x");
        n.facts += "\nFacts: hasPositionY " + position.attributeValue("y");
      }
      ruleTree.add(n.toString());
    }

    List<Node> stepList = document.selectNodes("//*[local-name()='step']");
    for (Iterator<Node> iter = stepList.iterator(); iter.hasNext();) {
      Element element = (Element) iter.next();
      RuleTreeNode n = new RuleTreeNode();
      n.individualName = "Step" + element.attributeValue("localId");
      n.typeName = "Step";
      n.facts = "hasLocalId " + element.attributeValue("localId");
      n.facts += "\nFacts: hasStepName \"" + element.attributeValue("name") + "\"";
      if (element.attributeValue("initialStep") != null) {
        n.facts += "\nFacts: isInitialStep \"" + element.attributeValue("initialStep") + "\"";
      }
      Node childNode = element.selectSingleNode("*[local-name()='position']");
      if (childNode != null) {
        Element position = (Element) childNode;
        n.facts += "\nFacts: hasPositionX " + position.attributeValue("x");
        n.facts += "\nFacts: hasPositionY " + position.attributeValue("y");
      }
      ruleTree.add(n.toString());
    }
  }

  public void parseCodesysXml(Document document) {

  }
}