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
 * @author kien5
 */
public class QuanLyNhanVienJInternalFrame extends javax.swing.JInternalFrame {

    /** Creates new form QuanLyNhanVienJInternalFrame */
    public QuanLyNhanVienJInternalFrame() {
        initComponents();
        load();
    }
    int index = 0;
    nhanVienDAO dao = new nhanVienDAO();
     void init() {
        setFrameIcon(shareHelper.APP_ICON_1);
        tabs.setSelectedIndex(1);
    }
     void load() {

        DefaultTableModel model = (DefaultTableModel) tblDanhSach.getModel();
        model.setRowCount(0);
        try {
            List<nhanVien> list = dao.select();
            for (nhanVien nv : list) {
                Object[] row = {
                    nv.getMaNV(),
                    shareHelper.USER.isVaiTro() ? nv.getMatKhau() : matKhauToSao(nv.getMatKhau()),
                    nv.getHoTen(),
                    nv.isVaiTro() ? "0" : "1"
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
        String confirm = new String(txtXacnhanpass.getPassword()); //cách chuyển char[] sang String

        txtXacnhanpass.setBackground(white);
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
            txtXacnhanpass.setBackground(pink);
            dialogHelper.alert(this, "Xác nhận mật khẩu không đúng!");
        }
    }

    void update() {
        nhanVien model = getModel();
        String confirm = new String(txtXacnhanpass.getPassword());
        txtXacnhanpass.setBackground(white);
        if (!confirm.equals(model.getMatKhau())) {
            txtXacnhanpass.setBackground(pink);
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
            String manv = txtMaNV.getText();
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
            String manv = (String) tblDanhSach.getValueAt(this.index, 0);  //lấy maNV từ bảng theo index
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
        txtMaNV.setBackground(white);
        txtpass.setBackground(white);
        txtXacnhanpass.setBackground(white);
        txtHoVaTen.setBackground(white);
    }

    void clear() {
        setTrang();
        this.setModel(new nhanVien());
        this.setStatus(true);
    }

    void setModel(nhanVien model) {
        txtMaNV.setText(model.getMaNV());
        txtHoVaTen.setText(model.getHoTen());
        txtpass.setText(model.getMatKhau());
        txtXacnhanpass.setText(model.getMatKhau());
        rbTruongphong.setSelected(model.isVaiTro());
        rbNhanVien.setSelected(!model.isVaiTro());
    }

    nhanVien getModel() {
        nhanVien model = new nhanVien();
        model.setMaNV(txtMaNV.getText());
        model.setHoTen(txtHoVaTen.getText());
        model.setMatKhau(new String(txtpass.getPassword()));
        model.setVaiTro(rbTruongphong.isSelected());
        return model;
    }

    void setStatus(boolean insertable) {
        txtMaNV.setEditable(insertable);
        btnThem.setEnabled(insertable);
        btnSua.setEnabled(!insertable);
        btnXoa.setEnabled(!insertable);
        boolean first = this.index > 0;
        boolean last = this.index < tblDanhSach.getRowCount()-1;
        btnFirst.setEnabled(!insertable && first);
        btnPrev.setEnabled(!insertable && first);
        btnNext.setEnabled(!insertable && last);
        btnLast.setEnabled(!insertable && last);
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
       public boolean checkChinhMinh(JTextField txt) {
        nhanVien nv = dao.findById(txt.getText());
        if (nv.getMaNV().equals(USER.getMaNV())) {
            dialogHelper.alert(this, "bạn không được xóa chính mình.");
            return false;
        } else {
            return true;
        }
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDanhSach = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtMaNV = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtHoVaTen = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        rbTruongphong = new javax.swing.JRadioButton();
        rbNhanVien = new javax.swing.JRadioButton();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnMoi = new javax.swing.JButton();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        txtpass = new javax.swing.JPasswordField();
        txtXacnhanpass = new javax.swing.JPasswordField();

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 204));
        jLabel1.setText("QUẢN LÝ NHÂN VIÊN QUẢN TRỊ");

        tblDanhSach.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MÃ NV", "Mật Khẩu", "Họ Và Tên", "Vai Trò"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDanhSach.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDanhSachMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDanhSach);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 13, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tabs.addTab("DANH SÁCH", jPanel2);

        jLabel2.setText("Mã Nhân Viên");

        jLabel3.setText("Mật Khẩu");

        jLabel4.setText("Xác nhận mật khẩu");

        jLabel5.setText("Họ Và Tên");

        jLabel6.setText("Vai TRò");

        buttonGroup1.add(rbTruongphong);
        rbTruongphong.setText("Trưởng Phòng");

        buttonGroup1.add(rbNhanVien);
        rbNhanVien.setText("Nhân Viên");

        btnThem.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\Add to basket.png")); // NOI18N
        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnSua.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\Edit.png")); // NOI18N
        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnXoa.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\Delete.png")); // NOI18N
        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnMoi.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\Refresh.png")); // NOI18N
        btnMoi.setText("Mới");

        btnFirst.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\dau.png")); // NOI18N
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnPrev.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\lui.png")); // NOI18N
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });

        btnNext.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\tien.png")); // NOI18N
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnLast.setIcon(new javax.swing.ImageIcon("C:\\Users\\Admin\\Downloads\\duan_ngayxua-master\\duan_ngayxua-master\\Source code\\Du an mau\\src\\icon\\cuoi.png")); // NOI18N
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMaNV)
                    .addComponent(txtHoVaTen)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnThem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSua)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnXoa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMoi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFirst)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrev)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnNext)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLast))
                    .addComponent(txtpass)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(rbTruongphong)
                                .addGap(18, 18, 18)
                                .addComponent(rbNhanVien)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtXacnhanpass))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtXacnhanpass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHoVaTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbTruongphong)
                    .addComponent(rbNhanVien))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFirst, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnPrev)
                    .addComponent(btnNext, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnLast)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnThem)
                        .addComponent(btnSua)
                        .addComponent(btnXoa)
                        .addComponent(btnMoi)))
                .addContainerGap(82, Short.MAX_VALUE))
        );

        tabs.addTab("CẬP NHẬT", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(96, 96, 96))
            .addComponent(tabs, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(tabs))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        // TODO add your handling code here:
         if (utilityHelper.checkNullText(txtMaNV)
                && utilityHelper.checkNullPass(txtpass)
                && utilityHelper.checkNullPass(txtXacnhanpass)
                && utilityHelper.checkNullText(txtHoVaTen)) {
            if (utilityHelper.checkMaNV(txtMaNV)
                    && utilityHelper.checkPass(txtpass)
                    && utilityHelper.checkName(txtHoVaTen)) {
                if (checkTrungMa(txtMaNV)) {
                    insert();
                }
            }
        }
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        // TODO add your handling code here:
         if (utilityHelper.checkNullPass(txtpass)
                && utilityHelper.checkNullPass(txtXacnhanpass)
                && utilityHelper.checkNullText(txtHoVaTen)) {
            if (utilityHelper.checkPass(txtpass)
                    && utilityHelper.checkName(txtHoVaTen)) {
                update();
            }
        }
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
         if (shareHelper.USER.isVaiTro()) {
            if (checkChinhMinh(txtMaNV)) {
                delete();
            }
        } else {
            dialogHelper.alert(this, "Chỉ trưởng phòng mới được phép xóa");
        }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void tblDanhSachMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDanhSachMouseClicked
        // TODO add your handling code here:
         if (evt.getClickCount() == 2) {
            this.index = tblDanhSach.rowAtPoint(evt.getPoint());
            if (this.index >= 0) {
                this.edit();
                tabs.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_tblDanhSachMouseClicked

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        // TODO add your handling code here:
        this.index = 0;
        this.edit();
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        // TODO add your handling code here:
        this.index--;
        this.edit();
    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:
        this.index++;
        this.edit();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        this.index = tblDanhSach.getRowCount()-1;
        this.edit();
        
    }//GEN-LAST:event_btnLastActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnMoi;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbNhanVien;
    private javax.swing.JRadioButton rbTruongphong;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblDanhSach;
    private javax.swing.JTextField txtHoVaTen;
    private javax.swing.JTextField txtMaNV;
    private javax.swing.JPasswordField txtXacnhanpass;
    private javax.swing.JPasswordField txtpass;
    // End of variables declaration//GEN-END:variables

}
