package anime;

import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Formanime extends JFrame {
    private String[] judul = { "idanime", "judulanime", "tahun_rilis", "genreanime" };
    DefaultTableModel df;
    JTable tab = new JTable();
    JScrollPane scp = new JScrollPane();
    JPanel pnl = new JPanel();
    JLabel lblIdanime = new JLabel("idanime");
    JTextField txIdanime = new JTextField(10);
    JLabel lblJudulanime = new JLabel("judulanime");
    JTextField txJudulanime = new JTextField(20);
    JLabel lblTahunRilis = new JLabel("tahun_rilis");
    JTextField txTahunRilis = new JTextField(10);
    JLabel lblGenreanine = new JLabel("genreanime");
    JTextField txGenreanime = new JTextField(10);
    JButton btAdd = new JButton("Simpan");
    JButton btNew = new JButton("Baru");
    JButton btDel = new JButton("Hapus");
    JButton btEdit = new JButton("Ubah");
    Connection cn; // Added Connection object

    Formanime() {
        super("anime");
        setSize(460, 300);
        pnl.setLayout(null);

        pnl.add(lblIdanime);
        lblIdanime.setBounds(20, 10, 100, 20);
        pnl.add(txIdanime);
        txIdanime.setBounds(125, 10, 100, 20);

        pnl.add(lblJudulanime);
        lblJudulanime.setBounds(20, 33, 100, 20);
        pnl.add(txJudulanime);
        txJudulanime.setBounds(125, 33, 175, 20);

        pnl.add(lblTahunRilis);
        lblTahunRilis.setBounds(20, 56, 100, 20);
        pnl.add(txTahunRilis);
        txTahunRilis.setBounds(125, 56, 175, 20);

        pnl.add(lblGenreanine);
        lblGenreanine.setBounds(20, 79, 100, 20);
        pnl.add(txGenreanime);
        txGenreanime.setBounds(125, 79, 175, 20);

        pnl.add(btNew);
        btNew.setBounds(320, 10, 100, 20);
        btNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btNewAksi(e);
            }
        });

        pnl.add(btAdd);
        btAdd.setBounds(320, 33, 100, 20);
        btAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btAddAksi(e);
            }
        });

        pnl.add(btEdit);
        btEdit.setBounds(320, 56, 100, 20);
        btEdit.setEnabled(false);
        btEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btEditAksi(e);
            }
        });

        pnl.add(btDel);
        btDel.setBounds(320, 79, 100, 20);
        btDel.setEnabled(false);
        btDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btDelAksi(e);
            }
        });

        df = new DefaultTableModel(null, judul);
        tab.setModel(df);
        scp.getViewport().add(tab);
        tab.setEnabled(true);
        tab.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                tabMouseClicked(evt);
            }
        });

        scp.setBounds(20, 110, 405, 130);
        pnl.add(scp);
        getContentPane().add(pnl);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Connect to the database
        cn = new Connect_DB().getConnect();
        if (cn == null) {
            System.err.println("Failed to connect to the database");
            System.exit(1);
        }

        // Load data from the database
        loadData();
    }

    void loadData() {
        try (Statement st = cn.createStatement()) {
            String sql = "SELECT * FROM anime";
            try (ResultSet rs = st.executeQuery(sql)) {
                clearTable();
                while (rs.next()) {
                    String Idanime = rs.getString("Id_anime");
                    String Judulanime = rs.getString("Judul_anime");
                    String TahunRilis = rs.getString("Tahun_Rilis");
                    String Genreanime = rs.getString("Genre_anime");
                    String[] data = { Idanime, Judulanime, TahunRilis, Genreanime};
                    df.addRow(data);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void clearTable() {
        int numRow = df.getRowCount();
        for (int i = 0; i < numRow; i++) {
            df.removeRow(0);
        }
    }

    void clearTextField() {
        txIdanime.setText(null);
        txJudulanime.setText(null);
        txTahunRilis.setText(null);
        txGenreanime.setText(null);
    }

    void simpanData(Tabel_Anime B) {
        try {
            String sql = "INSERT INTO anime (Id_anime, Judul_anime, Tahun_Rilis, Genre_anime) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, B.getIdanime());
            ps.setString(2, B.getJudulanime());
            ps.setString(3, B.getTahunRilis());
            ps.setString(4, B.getGenreanine());
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan",
                    "Info Proses", JOptionPane.INFORMATION_MESSAGE);
            String[] data = { B.getIdanime(), B.getJudulanime(), B.getTahunRilis(), B.getGenreanine()};
            df.addRow(data);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void hapusData(String kode) {
        try {
            String sql = "DELETE FROM anime WHERE anime = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, kode);
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus", "Info Proses",
                    JOptionPane.INFORMATION_MESSAGE);
            df.removeRow(tab.getSelectedRow());
            clearTextField();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void ubahData(Tabel_Anime B, String kode) {
        try {
            String sql = "UPDATE anime SET Id_anime = ?, Judul_anime = ?, Tahun_Rilis = ?, Genre_anime = ?, Rating_anime = ?, WHERE anime = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, B.getIdanime());
            ps.setString(2, B.getJudulanime());
            ps.setString(3, B.getTahunRilis());
            ps.setString(4, B.getGenreanine());
            ps.setString(6, kode);
            int result = ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Diubah", "Info Proses",
                    JOptionPane.INFORMATION_MESSAGE);
            clearTable();
            loadData();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btNewAksi(ActionEvent evt) {
        clearTextField();
        btEdit.setEnabled(false);
        btDel.setEnabled(false);
        btAdd.setEnabled(true);
    }

    private void btAddAksi(ActionEvent evt) {
        Tabel_Anime B = new Tabel_Anime();
        B.setIdanime(txIdanime.getText());
        B.setJudulanime(txJudulanime.getText());
        B.setTahunRilis(txTahunRilis.getText());
        B.setGenreanime(txGenreanime.getText());
        simpanData(B);
    }

    private void btDelAksi(ActionEvent evt) {
        int status;
        status = JOptionPane.showConfirmDialog(null, "Yakin data akan dihapus?",
                "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
        if (status == 0) {
            hapusData(txIdanime.getText());
        }
    }

    private void btEditAksi(ActionEvent evt) {
        Tabel_Anime B = new Tabel_Anime();
        B.setIdanime(txIdanime.getText());
        B.setJudulanime(txJudulanime.getText());
        B.setTahunRilis(txTahunRilis.getText());
        B.setGenreanime(txGenreanime.getText());
        ubahData(B, txIdanime.getText());
    }
    

    private void tabMouseClicked(MouseEvent evt) {
        int row = tab.getSelectedRow();
        txIdanime.setText(tab.getValueAt(row, 0).toString());
        txJudulanime.setText(tab.getValueAt(row, 1).toString());
        txTahunRilis.setText(tab.getValueAt(row, 2).toString());
        txGenreanime.setText(tab.getValueAt(row, 3).toString());
        btEdit.setEnabled(true);
        btDel.setEnabled(true);
        btAdd.setEnabled(false);
    }

    public static void main(String[] args) {
        new Formanime();
    }
}