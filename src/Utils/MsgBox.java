package Utils;

import java.awt.Component;
import javax.swing.JOptionPane;

public class MsgBox {
    
    public static void alert(Component p, String m){
        JOptionPane.showMessageDialog(p, m, "Hệ thống quản lý đào tạo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static boolean confirm(Component p, String m){
        int resul = JOptionPane.showConfirmDialog(p, m, "Hệ thống quản lý đào tạo", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return resul == JOptionPane.YES_OPTION;
    }
    
    public static String prompt(Component p, String m){
        return JOptionPane.showInputDialog(p, m, "Hệ thống quản lý dào tạo", JOptionPane.INFORMATION_MESSAGE);
    }
}
