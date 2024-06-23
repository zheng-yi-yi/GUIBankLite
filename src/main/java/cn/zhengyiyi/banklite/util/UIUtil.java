package cn.zhengyiyi.banklite.util;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;

/**
 * UI工具类。
 * 提供屏幕尺寸计算和全局字体设置的功能。
 */
public class UIUtil {
    /**
     * 根据屏幕尺寸和给定的分割比例计算尺寸。
     *
     * @param widthDivision 宽度的分割比例
     * @param heightDivision 高度的分割比例
     * @return 计算后的尺寸
     */
    public static Dimension getDimensionFromScreenSize(int widthDivision, int heightDivision){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screenSize.getWidth() / widthDivision;
        int height = (int)screenSize.getHeight() / heightDivision;
        return new Dimension(width, height);
    }

    /**
     * 设置全局字体大小。
     * 包括标签、文本框、按钮、单选按钮、复选框、列表、菜单及菜单项、对话框消息及按钮文本。
     */
    public static void setFontSize(){
        // 设置标签、文本框、按钮、单选按钮、复选框的字体、列表
        FontUIResource font1 = new FontUIResource(new Font("黑体", Font.PLAIN, 28));
        UIManager.put("Label.font", font1);
        UIManager.put("TextField.font", font1);
        UIManager.put("Button.font", font1);
        UIManager.put("RadioButton.font", font1);
        UIManager.put("CheckBox.font", font1);
        UIManager.put("List.font", font1);
        // 设置菜单及菜单项字体大小
        FontUIResource font2 = new FontUIResource(new Font("黑体", Font.PLAIN, 26));
        UIManager.put("Menu.font", font2);
        UIManager.put("MenuItem.font", font2);
        // 设置Swing对话框消息及按钮文本的字体大小
        UIManager.put("OptionPane.messageFont", font2);
        UIManager.put("OptionPane.buttonFont", font2);
    }
}