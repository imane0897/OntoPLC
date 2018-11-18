package org.mm.cellfie.ui.view;

import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

public class PLCXMLParser {

    public Set<String> ruleTree;

    class RuleTreeNode {
        String nodeName;
        String parentName;

        @Override
        public String toString() {
            return "Class: " + nodeName + "\n" + "SubClassOf: " + parentName;
        }
    };

    public PLCXMLParser() {
        this.ruleTree = new HashSet<String>();
    }

    public Set<String> treeWalk(Document document) {
        treeWalk(document.getRootElement());
        return ruleTree;
    }

    public void treeWalk(Element element) {
        for (int i = 0, size = element.nodeCount(); i < size; i++) {
            Node node = element.node(i);
            if (node instanceof Element) {
                treeWalk((Element) node);
            } else {
                RuleTreeNode n = new RuleTreeNode();
                n.nodeName = element.getName();
                if (element.getParent() != null) {
                    n.parentName = element.getParent().getName();
                } else {
                    n.parentName = "";
                }
                ruleTree.add(n.toString());
            }
        }
    }
}