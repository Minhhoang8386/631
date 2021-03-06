/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package du.an.pkg1;


import dao.nhanVienDAO;
import helper.dialogHelper;
import helper.shareHelper;
import static java.awt.Color.pink;
import static java.awt.Color.white;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import Entity.nhanVien;
import static helper.shareHelper.USER;
import static java.awt.Color.pink;
import static java.awt.Color.white;
import javax.swing.JTextField;
import helper.utilityHelper;
/**
 *
 * @author Admin
 */
public class Quanlynhanvien extends javax.swing.JFrame {

    /**
     * Creates new form Quanlynhanvien
     */
    public Quanlynhanvien() {
        initComponents();
       init();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
     int index = 0;
    nhanVienDAO dao = new nhanVienDAO();
     void init() {
       
        tabs.setSelectedIndex(1);
    }
     void load() {

        DefaultTableModel model = (DefaultTableModel) tbnv.getModel();
        model.setRowCount(0);
        try {
            List<nhanVien> list = dao.select();
            for (nhanVien nv : list) {
                Object[] row = {
                    nv.getMaNV(),
                    shareHelper.USER.isVaiTro() ? nv.getMatKhau() : matKhauToSao(nv.getMatKhau()),
                    nv.getHoTen(),
                    nv.isVaiTro() ? "Trưởng phòng" : "Nhân viên"
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            dialogHelper.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    public String matKhauToSao(String pass) {
        String sao = "";
        for (int i = 0; i < pass.length(); i++) {
            sao += "*";
        }
        return sao;
    }

    void insert() {

        nhanVien model = getModel();
        String confirm = new String(txtxacnhanmatkhau.getPassword()); //cách chuyển char[] sang String

        txtxacnhanmatkhau.setBackground(white);
        if (confirm.equals(model.getMatKhau())) {
            try {
                dao.insert(model);
                this.load();
                this.clear();
                dialogHelper.alert(this, "Thêm mới thành công!");
            } catch (Exception e) {
                dialogHelper.alert(this, "Thêm mới thất bại!");
            }
        } else {
            txtxacnhanmatkhau.setBackground(pink);
            dialogHelper.alert(this, "Xác nhận mật khẩu không đúng!");
        }
    }

    void update() {
        nhanVien model = getModel();
        String confirm = new String(txtxacnhanmatkhau.getPassword());
        txtxacnhanmatkhau.setBackground(white);
        if (!confirm.equals(model.getMatKhau())) {
            txtxacnhanmatkhau.setBackground(pink);
            dialogHelper.alert(this, "Xác nhận mật khẩu không đúng!");
        } else {
            try {
                dao.update(model);     //cập nhật nhân viên theo maNV
                this.load();           //điền tt mới vào bảng
                dialogHelper.alert(this, "Cập nhật thành công!");
            } catch (Exception e) {
                dialogHelper.alert(this, "Cập nhật thất bại!");
            }
        }
    }

    void delete() {
        if (dialogHelper.confirm(this, "Bạn thực sự muốn xóa nhân viên này?")) {
            String manv = TXTMANV.getText();
            try {
                dao.delete(manv);
                this.load();
                this.clear();
                dialogHelper.alert(this, "Xóa thành công!");
            } catch (Exception e) {
                String sql = "select * from NhanVien where MaNV in (select NhanVien.MaNV from NhanVien join KhoaHoc on NhanVien.MaNV=KhoaHoc.MaNV)";
                String sql2 = "select * from NhanVien where MaNV in (select NhanVien.MaNV from NhanVien join NguoiHoc on NhanVien.MaNV=NguoiHoc.MaNV)";
                List<nhanVien> list2 = dao.select(sql2);
                for (nhanVien x : list2) {
                    if (x.getMaNV().equalsIgnoreCase(manv)) {
                        dialogHelper.alert(this, "Bạn phải xóa hết các người học do nhân viên này tạo\ntrước khi xóa nhân viên này");
                        return;
                    }
                }
                List<nhanVien> list = dao.select(sql);
                for (nhanVien x : list) {
                    if (x.getMaNV().equalsIgnoreCase(manv)) {
                        dialogHelper.alert(this, "Bạn phải xóa hết các khóa học do nhân viên này tạo\ntrước khi xóa nhân viên này");
                        return;
                    }
                }
                dialogHelper.alert(this, "Xóa thất bại!");
            }
        }
    }

    void edit() {
        setTrang();
        try {
            String manv = (String) tbnv.getValueAt(this.index, 0);  //lấy maNV từ bảng theo index
            nhanVien model = dao.findById(manv);
            if (model != null) {
                this.setModel(model);
                this.setStatus(false);
            }
        } catch (Exception e) {
            dialogHelper.alert(this, "Lỗi truy vấn dữ liệu!");
        }
    }

    public void setTrang() {
        TXTMANV.setBackground(white);
        txtpass.setBackground(white);
        txtxacnhanmatkhau.setBackground(white);
        TXTHOTEN.setBackground(white);
    }

    void clear() {
        setTrang();
        this.setModel(new nhanVien());
        this.setStatus(true);
    }

    void setModel(nhanVien model) {
        TXTMANV.setText(model.getMaNV());
        TXTHOTEN.setText(model.getHoTen());
        txtpass.setText(model.getMatKhau());
        txtxacnhanmatkhau.setText(model.getMatKhau());
        rdtruongphong.setSelected(model.isVaiTro());
        rdnhanvien.setSelected(!model.isVaiTro());
    }

    nhanVien getModel() {
        nhanVien model = new nhanVien();
        model.setMaNV(TXTMANV.getText());
        model.setHoTen(TXTHOTEN.getText());
        model.setMatKhau(new String(txtpass.getPassword()));
        model.setVaiTro(rdtruongphong.isSelected());
        return model;
    }

    void setStatus(boolean insertable) {
        TXTMANV.setEditable(insertable);
        btninsert.setEnabled(insertable);
        btnupdate.setEnabled(!insertable);
        btndelete.setEnabled(!insertable);
        boolean first = this.index > 0;
        boolean last = this.index < tbnv.getRowCount()-1;
        btn1.setEnabled(!insertable && first);
        btn2.setEnabled(!insertable && first);
        btn3.setEnabled(!insertable && last);
        btn4.setEnabled(!insertable && last);
    }
      public boolean checkTrungMa(JTextField txt) {
        txt.setBackground(white);
        if (dao.findById(txt.getText()) == null) {
            return true;
        } else {
            txt.setBackground(pink);
            dialogHelper.alert(this, txt.getName() + " đã bị tồn tại.");
            return false;
        }
    }
       public boolean checkChinhMinh(JTextField txt){
        nhanVien nv=dao.findById(txt.getText());
        if (nv.getMaNV().equals(USER.getMaNV())) {
            dialogHelper.alert(this, "bạn không được xóa chính mình.");
            return false;
        } else {
            return true;
        }
    }
   
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        jLabel1 = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbnv = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        TXTMANV = new javax.swing.JTextField();
        TXTHOTEN = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        rdtruongphong = new javax.swing.JRadioButton();
        rdnhanvien = new javax.swing.JRadioButton();
        btninsert = new javax.swing.JButton();
        btnupdate = new javax.swing.JButton();
        btndelete = new javax.swing.JButton();
        btnclear = new javax.swing.JButton();
        btn1 = new javax.swing.JButton();
        btn2 = new javax.swing.JButton();
        btn3 = new javax.swing.JButton();
        btn4 = new javax.swing.JButton();
        txtxacnhanmatkhau = new javax.swing.JPasswordField();
        txtpass = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jInternalFrame1.setClosable(true);
        jInternalFrame1.setIconifiable(true);
        jInternalFrame1.setMaximizable(true);
        jInternalFrame1.setVisible(true);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 204));
        jLabel1.setText("                                                   QUẢN LÝ NHÂN VIÊN");

        tbnv.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MÃ NV", "MẬT KHẨU", "HỌ VÀ TÊN", "VAI TRÒ"
            }
        ));
        jScrollPane1.setViewportView(tbnv);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabs.addTab("Danh sách", jPanel2);

        jLabel2.setText("Mã nhân viên");

        jLabel3.setText("Mật khẩu");

        jLabel4.setText("Xác nhận mật khẩu");

        jLabel5.setText("Họ và tên");

        jLabel6.setText("Vai trò");

        rdtruongphong.setText("Trưởng phòng");

        rdnhanvien.setSelected(true);
        rdnhanvien.setText("Nhân viên");

        btninsert.setText("Thêm");
        btninsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btninsertActionPerformed(evt);
            }
        });

        btnupdate.setText("Sửa");

        btndelete.setText("Xóa");

        btnclear.setText("Mới");
        btnclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnclearActionPerformed(evt);
            }
        });

        btn1.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\dau.png")); // NOI18N

        btn2.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\lui.png")); // NOI18N

        btn3.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\tien.png")); // NOI18N

        btn4.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\cuoi.png")); // NOI18N

        txtxacnhanmatkhau.setText("jPasswordField1");

        txtpass.setText("jPasswordField1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TXTMANV)
                    .addComponent(TXTHOTEN, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtxacnhanmatkhau)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(rdtruongphong, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rdnhanvien, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btninsert, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnupdate, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btndelete, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnclear, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47)
                                .addComponent(btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn4, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtpass))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(TXTMANV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(txtxacnhanmatkhau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TXTHOTEN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdtruongphong)
                    .addComponent(rdnhanvien))
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btninsert)
                    .addComponent(btnupdate)
                    .addComponent(btndelete)
                    .addComponent(btnclear)
                    .addComponent(btn1)
                    .addComponent(btn2)
                    .addComponent(btn3)
                    .addComponent(btn4))
                .addContainerGap(92, Short.MAX_VALUE))
        );

        tabs.addTab("Cập nhật", jPanel1);

        javax.swing.GroupLayout jInternalFrame1Layout = new javax.swing.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(tabs, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jInternalFrame1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(tabs)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jInternalFrame1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jInternalFrame1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    private void btninsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btninsertActionPerformed
        // TODO add your handling code here:
     
    }//GEN-LAST:event_btninsertActionPerformed

    private void btnclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclearActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_btnclearActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Quanlynhanvien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Quanlynhanvien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Quanlynhanvien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Quanlynhanvien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Quanlynhanvien().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TXTHOTEN;
    private javax.swing.JTextField TXTMANV;
    private javax.swing.JButton btn1;
    private javax.swing.JButton btn2;
    private javax.swing.JButton btn3;
    private javax.swing.JButton btn4;
    private javax.swing.JButton btnclear;
    private javax.swing.JButton btndelete;
    private javax.swing.JButton btninsert;
    private javax.swing.JButton btnupdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rdnhanvien;
    private javax.swing.JRadioButton rdtruongphong;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tbnv;
    private javax.swing.JPasswordField txtpass;
    private javax.swing.JPasswordField txtxacnhanmatkhau;
    // End of variables declaration//GEN-END:variables
}
