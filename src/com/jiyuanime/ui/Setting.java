package com.jiyuanime.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Comparing;
import com.intellij.ui.ColorPanel;
import com.jiyuanime.config.Config;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class Setting implements Configurable {

    private JTextField particleMaxCountTextField;
    private ColorPanel colorChooser;
    private JPanel rootPanel;
    private JCheckBox colorAutoCheckBox;
    private JTextField particleMaxSizeTextField;
    private JComboBox particleShapeComboBox;

    private Config.State state = Config.getInstance().state;

    @Nls
    @Override
    public String getDisplayName() {
        return "Activate Power Mode";
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        initListener();

        initSetting();

        return this.rootPanel;
    }

    @Override
    public boolean isModified() {
        try {
            return !Comparing.equal(state.PARTICLE_MAX_COUNT, Integer.parseInt(particleMaxCountTextField.getText())) ||
                    !Comparing.equal(state.PARTICLE_SHAPE,(String)particleShapeComboBox.getSelectedItem())||
                    !Comparing.equal(state.PARTICLE_MAX_SIZE, Integer.parseInt(particleMaxSizeTextField.getText())) ||
                    !Comparing.equal(state.PARTICLE_COLOR, colorAutoCheckBox.isSelected() ? null : colorChooser.getSelectedColor());
        } catch (NumberFormatException $ex) {
            return true;
        }

    }

    @Override
    public void apply() throws ConfigurationException {
        state.PARTICLE_SHAPE = (String) particleShapeComboBox.getSelectedItem();
        try {
            int particle_max_count = Integer.parseInt(particleMaxCountTextField.getText());
            if (particle_max_count < 0) {
                throw new ConfigurationException("The 'particle max count' field must be greater than 0.");
            }
            state.PARTICLE_MAX_COUNT = particle_max_count;
        } catch (NumberFormatException $ex) {
            throw new ConfigurationException("The 'particle max count' field format error.");
        }
        try {
            int particle_max_size = Integer.parseInt(particleMaxSizeTextField.getText());
            if (particle_max_size <= 0) {
                throw new ConfigurationException("The 'particle max size' field must be greater than 0.");
            }
            state.PARTICLE_MAX_SIZE = particle_max_size > 10 ? 10 : particle_max_size;
        } catch (NumberFormatException $ex) {
            throw new ConfigurationException("The 'particle max size' field format error.");
        }

        if (!colorAutoCheckBox.isSelected() && colorChooser.getSelectedColor() == null) {
            throw new ConfigurationException("'particle color' is not choose.'");
        }

        state.PARTICLE_COLOR = colorAutoCheckBox.isSelected() ? null : colorChooser.getSelectedColor();
    }

    private void initListener() {
        colorAutoCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox item = (JCheckBox) e.getItem();

                colorChooser.setSelectedColor(null);
                colorChooser.setEditable(!item.isSelected());
            }
        });
        particleShapeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String name=(String)e.getItem();
                particleShapeComboBox.setSelectedItem("name");
            }
        });
    }

    private void initSetting() {
        particleMaxCountTextField.setText(String.valueOf(state.PARTICLE_MAX_COUNT));
        particleMaxSizeTextField.setText(String.valueOf(state.PARTICLE_MAX_SIZE));
        if (state.PARTICLE_COLOR == null) {
            colorAutoCheckBox.setSelected(true);
        } else {
            colorChooser.setSelectedColor(state.PARTICLE_COLOR);
        }
        particleShapeComboBox.addItem("RoughRoundShape");
        particleShapeComboBox.addItem("RoundShape");
        particleShapeComboBox.addItem("StarShape");
        particleShapeComboBox.setSelectedItem(state.PARTICLE_SHAPE);
    }
}
