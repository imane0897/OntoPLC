package org.mm.cellfie.ui.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.mm.core.TransformationRule;

public class TransformationRuleEditorPanel extends JPanel {
      private static final long serialVersionUID = 1L;

      private JTextField txtComment;
      private JTextArea txtRule;

      public TransformationRuleEditorPanel() {
            setLayout(new BorderLayout());

            JPanel pnlMain = new JPanel(new BorderLayout());
            pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            add(pnlMain, BorderLayout.CENTER);

            JLabel lblComment = new JLabel("Comment:");
            txtComment = new JTextField("");
            txtComment.addFocusListener(new FocusAdapter() {
                  @Override
                  public void focusGained(FocusEvent evt) {
                        SwingUtilities.invokeLater(() -> {
                              txtComment.requestFocus();
                              txtComment.selectAll();
                        });
                  }
            });

            JLabel lblRule = new JLabel("Rule:");

            JPanel pnlFields = new JPanel(new GridLayout(3, 2));
            pnlFields.add(lblComment);
            pnlFields.add(txtComment);
            pnlFields.add(lblRule);

            pnlMain.add(pnlFields, BorderLayout.NORTH);

            txtRule = new JTextArea("", 20, 48);
            pnlMain.add(txtRule, BorderLayout.CENTER);
      }

      public void fillFormFields(String rule, String comment) {
            txtComment.setText(comment);
            txtRule.setText(rule);
      }

      public TransformationRule getUserInput() {
            return new TransformationRule(txtComment.getText().trim(), txtRule.getText().trim());
      }
}
