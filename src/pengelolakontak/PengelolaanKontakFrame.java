package pengelolakontak;

import controller.KontakController;
import java.io.*;
import model.Kontak;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

public class PengelolaanKontakFrame extends javax.swing.JFrame {

    private DefaultTableModel model;
    private KontakController controller;

    /**
     * Creates new form PengelolaanKontakFrame
     */
    public PengelolaanKontakFrame() {
        initComponents();
        controller = new KontakController();
        model = new DefaultTableModel(new String[]{"No", "Nama", "Nomor Telepon", "Kategori"}, 0);
        tblKontak.setModel(model);
        loadContacts();
    }

    private void loadContacts() {
        try {
            model.setRowCount(0);
            List<Kontak> contacts = controller.getAllContacts();
            int rowNumber = 1;
            for (Kontak contact : contacts) {
                model.addRow(new Object[]{
                    rowNumber++,
                    contact.getNama(),
                    contact.getNomorTelepon(),
                    contact.getKategori()
                });
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void addContact() {
        String nama = txtNama.getText().trim();
        String nomorTelepon = txtNomorTelepon.getText().trim();
        String kategori = (String) cmbKategori.getSelectedItem();

        if (!validatePhoneNumber(nomorTelepon)) {
            return; // Validasi nomor telepon gagal
        }

        try {
            if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
                JOptionPane.showMessageDialog(this,
                        "Kontak nomor telepon ini sudah ada.",
                        "Kesalahan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.addContact(nama, nomorTelepon, kategori);
            loadContacts();
            JOptionPane.showMessageDialog(this,
                    "Kontak berhasil ditambahkan!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            clearInputFields();

        } catch (SQLException ex) {
            showError("Gagal menambahkan kontak: " + ex.getMessage());
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nomor telepon tidak boleh kosong.");
            return false;
        }
        if (!phoneNumber.matches("\\d+")) { // Hanya angka
            JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka.");
            return false;
        }
        if (phoneNumber.length() < 8 || phoneNumber.length() > 15) { // Panjang 8–15
            JOptionPane.showMessageDialog(this, "Nomor telepon harus memiliki panjang antara 8 hingga 15 karakter.");
            return false;
        }
        return true;
    }

    private void clearInputFields() {
        txtNama.setText("");
        txtNomorTelepon.setText("");
        cmbKategori.setSelectedIndex(0);
    }

    private void editContact() {
        int selectedRow = tblKontak.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih kontak yang ingin diperbarui.",
                    "Kesalahan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        String nama = txtNama.getText().trim();
        String nomorTelepon = txtNomorTelepon.getText().trim();
        String kategori = (String) cmbKategori.getSelectedItem();

        if (!validatePhoneNumber(nomorTelepon)) {
            return;
        }

        try {
            if (controller.isDuplicatePhoneNumber(nomorTelepon, id)) {
                JOptionPane.showMessageDialog(this,
                        "Kontak nomor telepon ini sudah ada.",
                        "Kesalahan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.updateContact(id, nama, nomorTelepon, kategori);
            loadContacts();
            JOptionPane.showMessageDialog(this,
                    "Kontak berhasil diperbarui!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            clearInputFields();

        } catch (SQLException ex) {
            showError("Gagal memperbarui kontak: " + ex.getMessage());
        }
    }

    private void populateInputFields(int selectedRow) {
        // Ambil data dari JTable
        String nama = model.getValueAt(selectedRow, 1).toString();
        String nomorTelepon = model.getValueAt(selectedRow, 2).toString();
        String kategori = model.getValueAt(selectedRow, 3).toString();

        // Set data ke komponen input
        txtNama.setText(nama);
        txtNomorTelepon.setText(nomorTelepon);
        cmbKategori.setSelectedItem(kategori);
    }

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {
        addContact();
    }

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {
        editContact();
    }
}
