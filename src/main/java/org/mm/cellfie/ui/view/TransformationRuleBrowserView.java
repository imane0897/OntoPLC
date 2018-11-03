package org.mm.cellfie.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

// import org.mm.cellfie.ui.exception.*;
import org.mm.ui.DialogManager;
import org.mm.ui.ModelView;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.JOptionPaneEx;

public class TransformationRuleBrowserView extends JPanel implements ModelView
{
      private static final long serialVersionUID = 1L;

      private WorkspacePanel container;

      private JPanel pnlContainer;

      public TransformationRuleBrowserView(WorkspacePanel container)
      {
            this.container = container;

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

            pnlContainer = new JPanel();
            pnlContainer.setLayout(new BorderLayout());
            add(pnlContainer, BorderLayout.CENTER);

            // tblTransformationRules = new JTable();
            // tblTransformationRules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            // tblTransformationRules.setGridColor(new Color(220, 220, 220));
            // tblTransformationRules.setDefaultRenderer(String.class, new MultiLineCellRenderer());
            // tblTransformationRules.addMouseListener(new MappingExpressionSelectionListener());
            // tblTransformationRules.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW)
            //             .put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "ADD_RULE");
            // tblTransformationRules.getActionMap().put("ADD_RULE", new AddRuleAction());
            // tblTransformationRules.getInputMap(JTable.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            //             "EDIT_RULE");
            // tblTransformationRules.getActionMap().put("EDIT_RULE", new EditRuleAction());
            // tblTransformationRules.getInputMap(JTable.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            //             "DELETE_RULE");
            // tblTransformationRules.getInputMap(JTable.WHEN_FOCUSED)
            //             .put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "DELETE_RULE");
            // tblTransformationRules.getActionMap().put("DELETE_RULE", new DeleteRuleAction());
            // tblTransformationRules.getInputMap(JTable.WHEN_FOCUSED)
            //             .put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "SELECT_ALL_RULES");
            // tblTransformationRules.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW)
            //             .put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "SELECT_ALL_RULES");
            // tblTransformationRules.getActionMap().put("SELECT_ALL_RULES", new SelectAllRulesAction());
            // tblTransformationRules.getInputMap(JTable.WHEN_IN_FOCUSED_WINDOW)
            //             .put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "DESELECT_ALL_RULES");
            // tblTransformationRules.getActionMap().put("DESELECT_ALL_RULES", new DeselectAllRulesAction());

            // tblTransformationRules.setColumnModel(new TransformationRulesColumnModel());
            // tblHeaderRenderer = new CheckBoxHeaderRenderer(tblTransformationRules.getTableHeader());

            // JScrollPane scrMappingExpression = new JScrollPane(tblTransformationRules);

            JPanel pnlTop = new JPanel(new BorderLayout());
            pnlTop.setBorder(new EmptyBorder(2, 5, 7, 5));
            pnlContainer.add(pnlTop, BorderLayout.NORTH);

            JPanel pnlCommandButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            pnlTop.add(pnlCommandButton, BorderLayout.WEST);

            // cmdAdd = new JButton("Add");
            // cmdAdd.setPreferredSize(new Dimension(72, 22));
            // cmdAdd.addActionListener(new AddButtonActionListener());
            // pnlCommandButton.add(cmdAdd);

            // cmdEdit = new JButton("Edit");
            // cmdEdit.setPreferredSize(new Dimension(72, 22));
            // cmdEdit.setEnabled(false);
            // cmdEdit.addActionListener(new EditButtonActionListener());
            // pnlCommandButton.add(cmdEdit);

            // cmdDelete = new JButton("Delete");
            // cmdDelete.setPreferredSize(new Dimension(72, 22));
            // cmdDelete.setEnabled(false);
            // cmdDelete.addActionListener(new DeleteButtonActionListener());
            // pnlCommandButton.add(cmdDelete);

            JPanel pnlMappingOpenSave = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            pnlTop.add(pnlMappingOpenSave, BorderLayout.EAST);

            // JButton cmdLoad = new JButton("Load Rules");
            // cmdLoad.setPreferredSize(new Dimension(152, 22));
            // cmdLoad.addActionListener(new OpenMappingAction());
            // pnlMappingOpenSave.add(cmdLoad);

            // cmdSave = new JButton("Save Rules");
            // cmdSave.setPreferredSize(new Dimension(152, 22));
            // cmdSave.addActionListener(new SaveMappingAction());
            // cmdSave.setEnabled(false);
            // pnlMappingOpenSave.add(cmdSave);

            // cmdSaveAs = new JButton("Save As...");
            // cmdSaveAs.setPreferredSize(new Dimension(152, 22));
            // cmdSaveAs.addActionListener(new SaveAsMappingAction());
            // cmdSaveAs.setEnabled(false);
            // pnlMappingOpenSave.add(cmdSaveAs);

            JPanel pnlCenter = new JPanel(new BorderLayout());
            pnlContainer.add(pnlCenter, BorderLayout.CENTER);

            // pnlCenter.add(scrMappingExpression, BorderLayout.CENTER);

            JPanel pnlGenerateAxioms = new JPanel();
            pnlContainer.add(pnlGenerateAxioms, BorderLayout.SOUTH);

            // cmdGenerateAxioms = new JButton("Generate Axioms");
            // cmdGenerateAxioms.setPreferredSize(new Dimension(152, 22));
            // cmdGenerateAxioms.addActionListener(new GenerateAxiomsAction(container));
            // cmdGenerateAxioms.setEnabled(false);
            // pnlGenerateAxioms.add(cmdGenerateAxioms);

            update();
            validate();
      }

      @Override
      public void update() {
            // TODO:
            // tableModel = new TransformationRulesTableModel(container.getActiveTransformationRules());
            // tblTransformationRules.setModel(tableModel);
            // tblTransformationRules.getColumnModel().getColumn(0).setHeaderRenderer(tblHeaderRenderer);
            // setTableHeaderAlignment(SwingConstants.CENTER);
            // setPreferredColumnWidth();
            // setPreferredColumnHeight();
            updateBorderUI();
      }

      // private void setTableHeaderAlignment(int alignment) {
      //       ((DefaultTableCellRenderer) tblTransformationRules.getTableHeader().getDefaultRenderer())
      //                   .setHorizontalAlignment(alignment);
      // }

      private void updateBorderUI() {
            pnlContainer.setBorder(ComponentFactory.createTitledBorder("Transformation Rules"));
      }
}