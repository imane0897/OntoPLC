package org.mm.renderer.owlapi;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.mm.parser.node.NameNode;
import org.mm.parser.node.OWLAllValuesFromNode;
import org.mm.parser.node.OWLClassExpressionNode;
import org.mm.parser.node.OWLClassNode;
import org.mm.parser.node.OWLDataAllValuesFromNode;
import org.mm.parser.node.OWLDataSomeValuesFromNode;
import org.mm.parser.node.OWLEquivalentClassesNode;
import org.mm.parser.node.OWLExactCardinalityNode;
import org.mm.parser.node.OWLHasValueNode;
import org.mm.parser.node.OWLIntersectionClassNode;
import org.mm.parser.node.OWLMaxCardinalityNode;
import org.mm.parser.node.OWLMinCardinalityNode;
import org.mm.parser.node.OWLNamedIndividualNode;
import org.mm.parser.node.OWLObjectAllValuesFromNode;
import org.mm.parser.node.OWLObjectOneOfNode;
import org.mm.parser.node.OWLObjectSomeValuesFromNode;
import org.mm.parser.node.OWLPropertyNode;
import org.mm.parser.node.OWLRestrictionNode;
import org.mm.parser.node.OWLSomeValuesFromNode;
import org.mm.parser.node.OWLUnionClassNode;
import org.mm.parser.node.ReferenceNode;
import org.mm.renderer.ClassExpressionRenderer;
import org.mm.renderer.InternalRendererException;
import org.mm.renderer.RendererException;
import org.mm.rendering.owlapi.OWLClassExpressionRendering;
import org.mm.rendering.owlapi.OWLClassRendering;
import org.mm.rendering.owlapi.OWLEntityReferenceRendering;
import org.mm.rendering.owlapi.OWLLiteralReferenceRendering;
import org.mm.rendering.owlapi.OWLLiteralRendering;
import org.mm.rendering.owlapi.OWLNamedIndividualRendering;
import org.mm.rendering.owlapi.OWLPropertyRendering;
import org.mm.rendering.owlapi.OWLReferenceRendering;
import org.mm.rendering.owlapi.OWLRendering;
import org.mm.rendering.owlapi.OWLRestrictionRendering;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLProperty;

public class OWLClassExpressionRenderer implements ClassExpressionRenderer {
    private final OWLEntityRenderer entityRenderer;
    private final OWLReferenceRenderer referenceRenderer;
    private final OWLLiteralRenderer literalRenderer;
    private final OWLObjectFactory objectFactory;

    public OWLClassExpressionRenderer(OWLReferenceRenderer referenceRenderer, OWLObjectFactory objectFactory) {
        this.referenceRenderer = referenceRenderer;
        this.entityRenderer = referenceRenderer.getEntityRenderer();
        this.literalRenderer = referenceRenderer.getLiteralRenderer();
        this.objectFactory = objectFactory;
    }

    @Override
    public Optional<OWLClassExpressionRendering> renderOWLClassExpression(OWLClassExpressionNode classExpressionNode)
            throws RendererException {
        Optional<? extends OWLClassExpressionRendering> classExpressionRendering;
        if (classExpressionNode.hasOWLUnionClassNode()) {
            classExpressionRendering = renderOWLUnionClass(classExpressionNode.getOWLUnionClassNode());
        } else if (classExpressionNode.hasOWLRestrictionNode()) {
            classExpressionRendering = renderOWLRestriction(classExpressionNode.getOWLRestrictionNode());
        } else if (classExpressionNode.hasOWLClassNode()) {
            classExpressionRendering = entityRenderer.renderOWLClass(classExpressionNode.getOWLClassNode(), false);
        } else {
            throw new InternalRendererException("Unknown child for node " + classExpressionNode.getNodeName());
        }

        /*
         * Construct the final rendering for the class expression
         */
        OWLClassExpressionRendering finalRendering = null;
        if (classExpressionRendering.isPresent()) {
            OWLClassExpression classExpression = classExpressionRendering.get().getOWLClassExpression();
            Set<OWLAxiom> axioms = classExpressionRendering.get().getOWLAxioms();
            if (classExpressionNode.getIsNegated()) {
                OWLObjectComplementOf restriction = objectFactory.createOWLObjectComplementOf(classExpression);
                finalRendering = new OWLClassExpressionRendering(restriction, axioms);
            } else {
                finalRendering = new OWLClassExpressionRendering(classExpression, axioms);
            }
        }
        return Optional.ofNullable(finalRendering);
    }

    @Override
    public Optional<OWLClassExpressionRendering> renderOWLUnionClass(OWLUnionClassNode unionNode)
            throws RendererException {
        Set<OWLClassExpression> classExpressions = new HashSet<>();
        Set<OWLAxiom> axioms = new HashSet<>();
        for (OWLIntersectionClassNode intersectionNode : unionNode.getOWLIntersectionClassNodes()) {
            Optional<OWLClassExpressionRendering> rendering = renderOWLIntersectionClass(intersectionNode);
            if (rendering.isPresent()) {
                classExpressions.add(rendering.get().getOWLClassExpression());
                axioms.addAll(rendering.get().getOWLAxioms());
            }
        }

        /*
         * Construct the final rendering for union classes
         */
        OWLClassExpressionRendering finalRendering = null;
        if (classExpressions.size() == 1) {
            finalRendering = new OWLClassExpressionRendering(classExpressions.iterator().next(), axioms);
        } else if (classExpressions.size() > 1) {
            OWLObjectUnionOf restriction = objectFactory.createOWLObjectUnionOf(classExpressions);
            finalRendering = new OWLClassExpressionRendering(restriction, axioms);
        }
        return Optional.ofNullable(finalRendering);
    }

    @Override
    public Optional<OWLClassExpressionRendering> renderOWLObjectOneOf(OWLObjectOneOfNode objectOneOfNode)
            throws RendererException {
        Set<OWLNamedIndividual> namedIndividuals = new HashSet<>();
        for (OWLNamedIndividualNode namedIndividualNode : objectOneOfNode.getOWLNamedIndividualNodes()) {
            Optional<OWLNamedIndividualRendering> namedIndividualRendering = entityRenderer
                    .renderOWLNamedIndividual(namedIndividualNode, false);
            if (namedIndividualRendering.isPresent()) {
                OWLNamedIndividual namedIndividual = namedIndividualRendering.get().getOWLNamedIndividual();
                namedIndividuals.add(namedIndividual);
            }
        }

        /*
         * Construct the final rendering for object one of
         */
        OWLClassExpressionRendering finalRendering = null;
        if (!namedIndividuals.isEmpty()) {
            OWLObjectOneOf objectOneOf = objectFactory.createOWLObjectOneOf(namedIndividuals);
            finalRendering = new OWLClassExpressionRendering(objectOneOf);
        }
        return Optional.ofNullable(finalRendering);
    }

    @Override
    public Optional<OWLClassExpressionRendering> renderOWLIntersectionClass(OWLIntersectionClassNode intersectionNode)
            throws RendererException {
        Set<OWLClassExpression> classExpressions = new HashSet<>();
        Set<OWLAxiom> axioms = new HashSet<>();
        for (OWLClassExpressionNode classExpressionNode : intersectionNode.getOWLClassExpressionNodes()) {
            Optional<OWLClassExpressionRendering> rendering = renderOWLClassExpression(classExpressionNode);
            if (rendering.isPresent()) {
                classExpressions.add(rendering.get().getOWLClassExpression());
                axioms.addAll(rendering.get().getOWLAxioms());
            }
        }

        /*
         * Construct the final rendering for intersection classes
         */
        OWLClassExpressionRendering finalRendering = null;
        if (classExpressions.size() == 1) {
            finalRendering = new OWLClassExpressionRendering(classExpressions.iterator().next(), axioms);
        } else if (classExpressions.size() > 1) {
            OWLObjectIntersectionOf restriction = objectFactory.createOWLObjectIntersectionOf(classExpressions);
            finalRendering = new OWLClassExpressionRendering(restriction, axioms);
        }
        return Optional.ofNullable(finalRendering);
    }

    @Override
    public Optional<OWLRendering> renderOWLEquivalentClasses(OWLClassNode declaredClassNode,
            OWLEquivalentClassesNode equivalentClassesNode) throws RendererException {
        OWLRendering finalRendering = null;
        Optional<OWLClassRendering> declaredClassRendering = entityRenderer.renderOWLClass(declaredClassNode, false);
        if (declaredClassRendering.isPresent()) {
            OWLClass declaredClass = declaredClassRendering.get().getOWLClass();
            Set<OWLClassExpression> equivalentClassExpressions = new HashSet<>();
            Set<OWLAxiom> axioms = new HashSet<>();
            for (OWLClassExpressionNode equivalentClassNode : equivalentClassesNode.getClassExpressionNodes()) {
                Optional<OWLClassExpressionRendering> classExpressionRendering = renderOWLClassExpression(
                        equivalentClassNode);
                if (classExpressionRendering.isPresent()) {
                    equivalentClassExpressions.add(classExpressionRendering.get().getOWLClassExpression());
                    axioms.addAll(classExpressionRendering.get().getOWLAxioms());
                }
            }
            if (!equivalentClassExpressions.isEmpty()) {
                equivalentClassExpressions.add(declaredClass);
                OWLEquivalentClassesAxiom equivalentClassesAxiom = objectFactory
                        .createOWLEquivalentClassesAxiom(equivalentClassExpressions);
                axioms.add(equivalentClassesAxiom);
                finalRendering = new OWLRendering(axioms);
            }
        }
        return Optional.ofNullable(finalRendering);
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLRestriction(OWLRestrictionNode restrictionNode)
            throws RendererException {
        OWLPropertyNode propertyNode = restrictionNode.getOWLPropertyNode();
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLDataProperty) { // data property restrictions
                OWLDataProperty dataProperty = (OWLDataProperty) property;
                if (restrictionNode.isOWLMinCardinality()) {
                    OWLMinCardinalityNode dataMinCardinalityNode = restrictionNode.getOWLMinCardinalityNode();
                    return renderOWLDataMinCardinality(restrictionNode.getOWLPropertyNode(), dataMinCardinalityNode);
                } else if (restrictionNode.isOWLMaxCardinality()) {
                    OWLMaxCardinalityNode dataMaxCardinalityNode = restrictionNode.getOWLMaxCardinalityNode();
                    return renderOWLDataMaxCardinality(restrictionNode.getOWLPropertyNode(), dataMaxCardinalityNode);
                } else if (restrictionNode.isOWLExactCardinality()) {
                    OWLExactCardinalityNode dataExactCardinalityNode = restrictionNode.getOWLExactCardinalityNode();
                    return renderOWLDataExactCardinality(restrictionNode.getOWLPropertyNode(),
                            dataExactCardinalityNode);
                } else if (restrictionNode.isOWLHasValue()) { // data has value restriction
                    OWLHasValueNode hasValueNode = restrictionNode.getOWLHasValueNode();
                    return renderOWLDataHasValue(restrictionNode.getOWLPropertyNode(), hasValueNode);
                } else if (restrictionNode.isOWLAllValuesFrom()) { // data all values from restriction
                    OWLAllValuesFromNode allValuesFromNode = restrictionNode.getOWLAllValuesFromNode();
                    if (!allValuesFromNode.hasOWLDataAllValuesFromNode()) {
                        throw new RendererException(
                                "Expecting datatype for data all values data restriction " + restrictionNode);
                    }
                    OWLDataAllValuesFromNode dataAllValuesFromNode = allValuesFromNode.getOWLDataAllValuesFromNode();
                    return renderOWLDataAllValuesFrom(restrictionNode.getOWLPropertyNode(), dataAllValuesFromNode);
                } else if (restrictionNode.isOWLSomeValuesFrom()) {
                    OWLSomeValuesFromNode someValuesFromNode = restrictionNode.getOWLSomeValuesFromNode();
                    if (!someValuesFromNode.hasOWLDataSomeValuesFromNode()) {
                        throw new RendererException(
                                "Expecting literal for some values data restriction " + restrictionNode);
                    }
                    OWLDataSomeValuesFromNode dataSomeValuesFromNode = someValuesFromNode
                            .getOWLDataSomeValuesFromNode();
                    OWLDatatype datatype = objectFactory.createOWLDatatype(dataSomeValuesFromNode.getDatatypeName());
                    OWLDataSomeValuesFrom restriction = objectFactory.createOWLDataSomeValuesFrom(dataProperty,
                            datatype);
                    return Optional.of(new OWLRestrictionRendering(restriction));
                } else
                    return Optional.empty();
            } else if (property instanceof OWLObjectProperty) { // Object property restrictions
                if (restrictionNode.isOWLMinCardinality()) {
                    OWLMinCardinalityNode objectMinCardinalityNode = restrictionNode.getOWLMinCardinalityNode();
                    return renderOWLObjectMinCardinality(restrictionNode.getOWLPropertyNode(),
                            objectMinCardinalityNode);
                } else if (restrictionNode.isOWLMaxCardinality()) {
                    OWLMaxCardinalityNode objectMaxCardinalityNode = restrictionNode.getOWLMaxCardinalityNode();
                    return renderOWLObjectMaxCardinality(restrictionNode.getOWLPropertyNode(),
                            objectMaxCardinalityNode);
                } else if (restrictionNode.isOWLExactCardinality()) {
                    OWLExactCardinalityNode objectExactCardinalityNode = restrictionNode.getOWLExactCardinalityNode();
                    return renderOWLObjectExactCardinality(restrictionNode.getOWLPropertyNode(),
                            objectExactCardinalityNode);
                } else if (restrictionNode.isOWLHasValue()) {
                    OWLHasValueNode objectHasValueNode = restrictionNode.getOWLHasValueNode();
                    return renderOWLObjectHasValue(restrictionNode.getOWLPropertyNode(), objectHasValueNode);
                } else if (restrictionNode.isOWLAllValuesFrom()) { // Object all values from restriction
                    OWLAllValuesFromNode allValuesFromNode = restrictionNode.getOWLAllValuesFromNode();
                    if (allValuesFromNode.hasOWLDataAllValuesFromNode()) {
                        throw new RendererException(
                                "Expecting class for all values object restriction " + restrictionNode);
                    }
                    return renderOWLObjectAllValuesFrom(restrictionNode.getOWLPropertyNode(),
                            allValuesFromNode.getObjectOWLAllValuesFromNode());
                } else if (restrictionNode.isOWLSomeValuesFrom()) {
                    OWLSomeValuesFromNode someValuesFromNode = restrictionNode.getOWLSomeValuesFromNode();
                    if (someValuesFromNode.hasOWLDataSomeValuesFromNode()) {
                        throw new RendererException(
                                "Expecting class for object some values from restriction " + restrictionNode);
                    }
                    return renderOWLObjectSomeValuesFrom(restrictionNode.getOWLPropertyNode(),
                            someValuesFromNode.getOWLObjectSomeValuesFromNode());
                } else
                    return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLObjectExactCardinality(OWLPropertyNode propertyNode,
            OWLExactCardinalityNode exactCardinalityNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLObjectProperty) {
                OWLObjectProperty objectProperty = (OWLObjectProperty) property;
                int cardinality = exactCardinalityNode.getCardinality();
                OWLObjectExactCardinality restriction = objectFactory.createOWLObjectExactCardinality(cardinality,
                        objectProperty);
                return Optional.of(new OWLRestrictionRendering(restriction));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLDataExactCardinality(OWLPropertyNode propertyNode,
            OWLExactCardinalityNode exactCardinalityNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLDataProperty) {
                OWLDataProperty dataProperty = (OWLDataProperty) property;
                int cardinality = exactCardinalityNode.getCardinality();
                OWLDataExactCardinality restriction = objectFactory.createOWLDataExactCardinality(cardinality,
                        dataProperty);
                return Optional.of(new OWLRestrictionRendering(restriction));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLObjectMaxCardinality(OWLPropertyNode propertyNode,
            OWLMaxCardinalityNode maxCardinalityNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLObjectProperty) {
                OWLObjectProperty objectProperty = (OWLObjectProperty) property;
                int cardinality = maxCardinalityNode.getCardinality();
                OWLObjectMaxCardinality restriction = objectFactory.createOWLObjectMaxCardinality(cardinality,
                        objectProperty);
                return Optional.of(new OWLRestrictionRendering(restriction));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLDataMaxCardinality(OWLPropertyNode propertyNode,
            OWLMaxCardinalityNode maxCardinalityNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLDataProperty) {
                OWLDataProperty dataProperty = (OWLDataProperty) property;
                int cardinality = maxCardinalityNode.getCardinality();
                OWLDataMaxCardinality restriction = objectFactory.createOWLDataMaxCardinality(cardinality,
                        dataProperty);
                return Optional.of(new OWLRestrictionRendering(restriction));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLObjectMinCardinality(OWLPropertyNode propertyNode,
            OWLMinCardinalityNode minCardinalityNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLObjectProperty) {
                OWLObjectProperty objectProperty = (OWLObjectProperty) property;
                int cardinality = minCardinalityNode.getCardinality();
                OWLObjectMinCardinality restriction = objectFactory.createOWLObjectMinCardinality(cardinality,
                        objectProperty);
                return Optional.of(new OWLRestrictionRendering(restriction));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLDataMinCardinality(OWLPropertyNode propertyNode,
            OWLMinCardinalityNode minCardinalityNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLDataProperty) {
                OWLDataProperty dataProperty = (OWLDataProperty) property;
                int cardinality = minCardinalityNode.getCardinality();
                OWLDataMinCardinality restriction = objectFactory.createOWLDataMinCardinality(cardinality,
                        dataProperty);
                return Optional.of(new OWLRestrictionRendering(restriction));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLObjectHasValue(OWLPropertyNode propertyNode,
            OWLHasValueNode objectHasValueNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLObjectProperty) {
                OWLObjectProperty objectProperty = (OWLObjectProperty) property;
                if (objectHasValueNode.hasNameNone()) {
                    NameNode nameNode = objectHasValueNode.getNameNode();
                    String shortName = nameNode.getName();
                    OWLNamedIndividual individual = objectFactory.getAndCheckOWLNamedIndividual(shortName);
                    OWLObjectHasValue objectHasValueRestriction = objectFactory.createOWLObjectHasValue(objectProperty,
                            individual);
                    return Optional.of(new OWLRestrictionRendering(objectHasValueRestriction));
                } else if (objectHasValueNode.hasReferenceNode()) {
                    ReferenceNode referenceNode = objectHasValueNode.getReferenceNode();
                    Optional<OWLReferenceRendering> rendering = referenceRenderer.renderReference(referenceNode);
                    if (rendering.isPresent()) {
                        OWLReferenceRendering referenceRendering = rendering.get();
                        if (referenceRendering instanceof OWLEntityReferenceRendering) {
                            OWLEntityReferenceRendering entityRendering = (OWLEntityReferenceRendering) referenceRendering;
                            if (entityRendering.isOWLNamedIndividual()) {
                                OWLNamedIndividual individual = entityRendering.getOWLEntity().asOWLNamedIndividual();
                                OWLObjectHasValue objectHasValueRestriction = objectFactory
                                        .createOWLObjectHasValue(objectProperty, individual);
                                return Optional.of(new OWLRestrictionRendering(objectHasValueRestriction));
                            }
                            throw new RendererException(
                                    "Reference value '" + referenceNode + "' is not a named individual");
                        }
                    }
                    return Optional.empty();
                }
                throw new InternalRendererException("Unknown child node for node " + objectHasValueNode.getNodeName());
            }
            throw new RendererException("Property <" + property.getIRI() + "> is not an object property");
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLDataHasValue(OWLPropertyNode propertyNode,
            OWLHasValueNode hasValueNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLDataProperty) {
                OWLDataProperty dataProperty = (OWLDataProperty) property;
                if (hasValueNode.hasLiteralNode()) {
                    Optional<OWLLiteralRendering> literalRendering = literalRenderer
                            .renderOWLLiteral(hasValueNode.getOWLLiteralNode());
                    if (literalRendering.isPresent()) {
                        OWLLiteral literal = literalRendering.get().getOWLLiteral();
                        OWLDataHasValue dataHasValue = objectFactory.createOWLDataHasValue(dataProperty, literal);
                        return Optional.of(new OWLRestrictionRendering(dataHasValue));
                    }
                    return Optional.empty();
                } else if (hasValueNode.hasReferenceNode()) {
                    Optional<OWLReferenceRendering> rendering = referenceRenderer
                            .renderReference(hasValueNode.getReferenceNode());
                    if (rendering.isPresent()) {
                        OWLReferenceRendering referenceRendering = rendering.get();
                        if (referenceRendering instanceof OWLLiteralReferenceRendering) {
                            OWLLiteralReferenceRendering literalRendering = (OWLLiteralReferenceRendering) referenceRendering;
                            OWLLiteral literal = literalRendering.getOWLLiteral();
                            OWLDataHasValue dataHasValue = objectFactory.createOWLDataHasValue(dataProperty, literal);
                            return Optional.of(new OWLRestrictionRendering(dataHasValue));
                        }
                    }
                    return Optional.empty();
                }
                throw new RendererException("Expecting literal node or reference node: " + hasValueNode);
            }
            throw new RendererException("Property <" + property.getIRI() + "> is not a data property");
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLObjectSomeValuesFrom(OWLPropertyNode propertyNode,
            OWLObjectSomeValuesFromNode objectSomeValuesFromNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLObjectProperty) {
                OWLObjectProperty objectProperty = (OWLObjectProperty) property;
                if (objectSomeValuesFromNode.hasOWLClassExpressionNode()) {
                    Optional<OWLClassExpressionRendering> classExpressionRendering = renderOWLClassExpression(
                            objectSomeValuesFromNode.getOWLClassExpressionNode());
                    if (classExpressionRendering.isPresent()) {
                        OWLClassExpression classExpression = classExpressionRendering.get().getOWLClassExpression();
                        OWLObjectSomeValuesFrom objectSomeValuesFromRestriction = objectFactory
                                .createOWLObjectSomeValuesFrom(objectProperty, classExpression);
                        return Optional.of(new OWLRestrictionRendering(objectSomeValuesFromRestriction));
                    }
                    return Optional.empty();
                } else if (objectSomeValuesFromNode.hasOWLClassNode()) {
                    Optional<OWLClassRendering> classRendering = entityRenderer
                            .renderOWLClass(objectSomeValuesFromNode.getOWLClassNode(), false);
                    if (classRendering.isPresent()) {
                        OWLClassExpression cls = classRendering.get().getOWLClass();
                        OWLObjectSomeValuesFrom objectSomeValuesFromRestriction = objectFactory
                                .createOWLObjectSomeValuesFrom(objectProperty, cls);
                        return Optional.of(new OWLRestrictionRendering(objectSomeValuesFromRestriction));
                    }
                    return Optional.empty();
                }
                throw new InternalRendererException(
                        "Unknown child node for node " + objectSomeValuesFromNode.getNodeName());
            }
            throw new RendererException("Property <" + property.getIRI() + "> is not an object property");
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLDataSomeValuesFrom(OWLPropertyNode propertyNode,
            OWLDataSomeValuesFromNode dataSomeValuesFromNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLDataProperty) {
                OWLDataProperty dataProperty = (OWLDataProperty) property;
                String datatypeName = dataSomeValuesFromNode.getDatatypeName();
                OWLDatatype datatype = objectFactory.createOWLDatatype(datatypeName);
                OWLDataSomeValuesFrom dataSomeValuesFrom = objectFactory.createOWLDataSomeValuesFrom(dataProperty,
                        datatype);
                return Optional.of(new OWLRestrictionRendering(dataSomeValuesFrom));
            }
            throw new RendererException("Property <" + property.getIRI() + "> is not a data property");
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLObjectAllValuesFrom(OWLPropertyNode propertyNode,
            OWLObjectAllValuesFromNode onlyNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLObjectProperty) {
                OWLObjectProperty op = (OWLObjectProperty) property;
                if (onlyNode.hasOWLClassExpression()) {
                    Optional<OWLClassExpressionRendering> rendering = renderOWLClassExpression(
                            onlyNode.getOWLClassExpressionNode());
                    if (rendering.isPresent()) {
                        OWLClassExpression ce = rendering.get().getOWLClassExpression();
                        OWLObjectAllValuesFrom objectAllValuesFromRestriction = objectFactory
                                .createOWLObjectAllValuesFrom(op, ce);
                        return Optional.of(new OWLRestrictionRendering(objectAllValuesFromRestriction));
                    }
                } else if (onlyNode.hasOWLClass()) {
                    Optional<OWLClassRendering> rendering = entityRenderer.renderOWLClass(onlyNode.getOWLClassNode(),
                            false);
                    if (rendering.isPresent()) {
                        OWLClassExpression ce = rendering.get().getOWLClass();
                        OWLObjectAllValuesFrom objectAllValuesFromRestriction = objectFactory
                                .createOWLObjectAllValuesFrom(op, ce);
                        return Optional.of(new OWLRestrictionRendering(objectAllValuesFromRestriction));
                    }
                } else if (onlyNode.hasOWLObjectOneOfNode()) {
                    Optional<OWLClassExpressionRendering> rendering = renderOWLObjectOneOf(
                            onlyNode.getOWLObjectOneOfNode());
                    if (rendering.isPresent()) {
                        OWLClassExpression ce = rendering.get().getOWLClassExpression();
                        OWLObjectAllValuesFrom objectAllValuesFromRestriction = objectFactory
                                .createOWLObjectAllValuesFrom(op, ce);
                        return Optional.of(new OWLRestrictionRendering(objectAllValuesFromRestriction));
                    }
                }
            }
            throw new RendererException("Property <" + property.getIRI() + "> is not an object property");
        }
        return Optional.empty();
    }

    @Override
    public Optional<OWLRestrictionRendering> renderOWLDataAllValuesFrom(OWLPropertyNode propertyNode,
            OWLDataAllValuesFromNode dataAllValuesFromNode) throws RendererException {
        Optional<? extends OWLPropertyRendering> propertyRendering = entityRenderer.renderOWLProperty(propertyNode);
        if (propertyRendering.isPresent()) {
            OWLProperty property = propertyRendering.get().getOWLProperty();
            if (property instanceof OWLDataProperty) {
                OWLDataProperty dataProperty = (OWLDataProperty) property;
                OWLDatatype datatype = objectFactory.createOWLDatatype(dataAllValuesFromNode.getDatatypeName());
                OWLDataAllValuesFrom restriction = objectFactory.createOWLDataAllValuesFrom(dataProperty, datatype);
                return Optional.of(new OWLRestrictionRendering(restriction));
            }
            throw new RendererException("Property <" + property.getIRI() + "> is not a data property");
        }
        return Optional.empty();
    }
}
