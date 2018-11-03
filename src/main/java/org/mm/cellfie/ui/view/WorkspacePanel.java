package org.mm.cellfie.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.mm.app.MMApplication;
import org.mm.app.MMApplicationFactory;
import org.mm.app.MMApplicationModel;
// import org.mm.cellfie.action.OWLProtegeOntology;
import org.mm.core.OWLOntologySourceHook;
// import org.mm.core.TransformationRule;
// import org.mm.core.TransformationRuleSet;
import org.mm.core.settings.ReferenceSettings;
import org.mm.parser.ASTExpression;
import org.mm.parser.MappingMasterParser;
import org.mm.parser.ParseException;
import org.mm.parser.SimpleNode;
import org.mm.parser.node.ExpressionNode;
import org.mm.parser.node.MMExpressionNode;
import org.mm.renderer.Renderer;
import org.mm.rendering.Rendering;
import org.mm.ss.SpreadSheetDataSource;
import org.mm.ui.DialogManager;
import org.protege.editor.core.ui.split.ViewSplitPane;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;

public class WorkspacePanel extends JPanel
{
      private static final long serialVersionUID = 1L;

      private OWLOntology ontology;
      private OWLEditorKit editorKit;

      private DialogManager dialogHelper;
      private TransformationRuleBrowserView transformationRuleBrowserView;

      private MMApplication application;
      private MMApplicationFactory applicationFactory = new MMApplicationFactory();

      public WorkspacePanel(OWLOntology ontology, String PLCFilePath, OWLEditorKit editorKit,
                  DialogManager dialogHelper) {
            this.ontology = ontology;
            this.editorKit = editorKit;
            this.dialogHelper = dialogHelper;

            setLayout(new BorderLayout());

            JPanel pnlTargetOntology = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pnlTargetOntology.setBorder(new EmptyBorder(5, 5, 0, 5));
            add(pnlTargetOntology, BorderLayout.NORTH);

            JLabel lblTargetOntology = new JLabel("Target Ontology: ");
            lblTargetOntology.setForeground(Color.DARK_GRAY);
            pnlTargetOntology.add(lblTargetOntology);

            JLabel lblOntologyID = new JLabel(getTitle(ontology));
            lblOntologyID.setForeground(Color.DARK_GRAY);
            pnlTargetOntology.add(lblOntologyID);

            ViewSplitPane splitPane = new ViewSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setResizeWeight(0.4);
            add(splitPane, BorderLayout.CENTER);


            /*
             * Transformation rule browser, create, edit, remove panel
             */
            transformationRuleBrowserView = new TransformationRuleBrowserView(this);
            splitPane.setBottomComponent(transformationRuleBrowserView);

            validate();
      }

      private String getTitle(OWLOntology ontology) {
            if (ontology.getOntologyID().isAnonymous()) {
                  return ontology.getOntologyID().toString();
            }
            final com.google.common.base.Optional<IRI> iri = ontology.getOntologyID().getDefaultDocumentIRI();
            return getOntologyLabelText(iri);
      }

      private String getOntologyLabelText(com.google.common.base.Optional<IRI> iri) {
            StringBuilder sb = new StringBuilder();
            if (iri.isPresent()) {
                  String shortForm = new OntologyIRIShortFormProvider().getShortForm(iri.get());
                  sb.append(shortForm);
            } else {
                  sb.append("Anonymous ontology");
            }
            sb.append(" (");
            if (iri.isPresent()) {
                  sb.append(iri.get().toString());
            }
            sb.append(")");
            return sb.toString();
      }

      public static JDialog createDialog(OWLOntology ontology, String PLCFilePath, OWLEditorKit editorKit,
                  DialogManager dialogHelper) {
            final JDialog dialog = new JDialog(null, "OntoPLC", Dialog.ModalityType.MODELESS);

            final WorkspacePanel workspacePanel = new WorkspacePanel(ontology, PLCFilePath, editorKit, dialogHelper);
            workspacePanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CLOSE_DIALOG");
            workspacePanel.getActionMap().put("CLOSE_DIALOG", new AbstractAction() // Closing Cellfie using ESC key
            {
                  private static final long serialVersionUID = 1L;

                  @Override
                  public void actionPerformed(ActionEvent e) {
                        int answer = dialogHelper.showConfirmDialog(dialog, "Confirm Exit", "Exit Cellfie?");
                        switch (answer) {
                        case JOptionPane.YES_OPTION:
                              // if (workspacePanel.shouldClose()) {
                                    dialog.setVisible(false);
                              // }
                        }
                  }
            });
            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            dialog.addWindowListener(new WindowAdapter() // Closing Cellfie using close [x] button
            {
                  @Override
                  public void windowClosing(WindowEvent e) {
                        // if (workspacePanel.shouldClose()) {
                              dialog.setVisible(false);
                        // }
                  }
            });
            dialog.setContentPane(workspacePanel);
            dialog.setSize(1200, 900);
            dialog.setResizable(true);
            return dialog;
      }
}