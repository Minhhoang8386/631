package DAO;

import Entity.HocVien;
import Utils.JdbcHelper;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;

public class HocVienDAO extends EduSysDAO<HocVien, Integer> {

    String INSERT_SQL = "INSERT INTO HOCVIEN (MaKH, MaNH, Diem) VALUES (?, ?, ?) ";
    String UPDATE_SQL = "UPDATE HOCVIEN SET MAKH = ?, MaNH = ?, Diem = ? WHERE MaHV = ?";
    String DELETE_SQL = "DELETE FROM HOCVIEN WHERE MaHV = ?";
    String SELECT_ALL_SQL = "SELECT * FROM HOCVIEN";
    String SELECT_BY_ID_SQL = "SELECT * FROM HOCVIEN WHERE MaHV = ?";

    @Override
    public void insert(HocVien entity) {
        JdbcHelper.executeUpdate(INSERT_SQL, entity.getMaKH(),entity.getMaNH(),entity.getDiem());
    }

    @Override
    public void update(HocVien entity) {
        JdbcHelper.executeUpdate(UPDATE_SQL, entity.getMaKH(),entity.getMaNH(),entity.getDiem(), entity.getMaHV());
    }

    @Override
    public void delete(Integer key) {
        JdbcHelper.executeUpdate(DELETE_SQL, key);
    }

    @Override
    public List<HocVien> selectAll() {
        return this.selectBySql(SELECT_ALL_SQL);
    }

    @Override
    public HocVien selectById(Integer key) {
        List<HocVien> list = this.selectBySql(SELECT_BY_ID_SQL, key);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<HocVien> selectBySql(String sql, Object... args) {
        List<HocVien> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelper.executeQuery(sql, args);
            while (rs.next()) {                
                HocVien entity = new HocVien();
                entity.setMaHV(rs.getInt("MaHV"));
                entity.setMaKH(rs.getInt("MaKH"));
                entity.setMaNH(rs.getString("MaNH"));
                entity.setDiem(rs.getDouble("Diem"));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<HocVien> selectByKhoaHoc(int maKH){
        String sql="SELECT * FROM HOCVIEN WHERE MaKH = ?";
        return selectBySql(sql, maKH);
    }
}
