package DAO;

import Entity.NguoiHoc;
import Utils.JdbcHelper;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class NguoiHocDAO extends EduSysDAO<NguoiHoc, String> {

    String INSERT_SQL = "INSERT INTO NGUOIHOC(MaNH,HoTen,NgaySinh,GioiTinh,DienThoai,Email,GhiChu,MaNV) VALUES(?,?,?,?,?,?,?,?)";
    String UPDATE_SQL = "UPDATE NGUOIHOC SET HoTen = ?, NgaySinh = ?, GioiTinh = ?, DienThoai = ?, Email = ?, GhiChu = ? WHERE MaNH = ?";
    String DELETE_SQL = "DELETE FROM NGUOIHOC WHERE MaNH = ?";
    String SELECT_ALL_SQL = "SELECT * FROM NGUOIHOC";
    String SELECT_BY_ID_SQL = "SELECT * FROM NGUOIHOC WHERE MaNH = ?";

    @Override
    public void insert(NguoiHoc entity) {
        JdbcHelper.executeUpdate(INSERT_SQL, entity.getMaNH(), entity.getHoTen(), entity.getNgaySinh(), entity.isGioiTinh(),
                entity.getDienThoai(), entity.getEmail(), entity.getGhiChu(), entity.getMaNV());
    }

    @Override
    public void update(NguoiHoc entity) {
        JdbcHelper.executeUpdate(UPDATE_SQL, entity.getHoTen(), entity.getNgaySinh(), entity.isGioiTinh(),
                entity.getDienThoai(), entity.getEmail(), entity.getGhiChu(), entity.getMaNH());
    }

    @Override
    public void delete(String key) {
        JdbcHelper.executeUpdate(DELETE_SQL, key);
    }

    @Override
    public List<NguoiHoc> selectAll() {
        return this.selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public NguoiHoc selectById(String key) {
        List<NguoiHoc> list = this.selectBySql(SELECT_BY_ID_SQL, key);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<NguoiHoc> selectBySql(String sql, Object... args) {
        List<NguoiHoc> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.executeQuery(sql, args);
            while (rs.next()) {
                NguoiHoc entity = new NguoiHoc();
                entity.setMaNH(rs.getString("MaNH"));
                entity.setHoTen(rs.getString("HoTen"));
                entity.setNgaySinh(rs.getDate("NgaySinh"));
                entity.setGioiTinh(rs.getBoolean("GioiTinh"));
                entity.setDienThoai(rs.getString("DienThoai"));
                entity.setEmail(rs.getString("Email"));
                entity.setGhiChu(rs.getString("GhiChu"));
                entity.setMaNV(rs.getString("MaNV"));
                entity.setNgayDK(rs.getDate("NgayDK"));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<NguoiHoc> selectByKeyWord(String key) {
        String sql = "SELECT * FROM NGUOIHOC WHERE Hoten LIKE ?";
        return selectBySql(sql, "%" + key + "%");
    }

    public List<NguoiHoc> selectnotlnCourse(int makh, String keyword) {
        String sql = "SELECT * FROM NGUOIHOC"
                + " WHERE HoTen LIKE ? AND"
                + " MaNH NOT IN (SELECT MaNH FROM HOCVIEN WHERE MaKH = ?)";
        return selectBySql(sql, "%" + keyword + "%", makh);
    }
}
