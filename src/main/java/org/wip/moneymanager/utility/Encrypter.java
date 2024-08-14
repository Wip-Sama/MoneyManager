package org.wip.moneymanager.utility;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.io.File;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class Encrypter {
    private Cipher ecipher;
    private Cipher dcipher;
    private int bcrypt_salt = 12;

    public Encrypter(SecretKey key, int bccrypt_salt) throws Exception {
        this(key);
        this.bcrypt_salt = bccrypt_salt;
    }

    public Encrypter(String key, int bccrypt_salt) throws Exception {
        this(key);
        this.bcrypt_salt = bccrypt_salt;
    }

    public Encrypter(SecretKey key) throws Exception {
        ecipher = Cipher.getInstance("AES");
        dcipher = Cipher.getInstance("AES");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        dcipher.init(Cipher.DECRYPT_MODE, key);
    }

    public Encrypter(String key) throws Exception {
        SecretKey k = new SecretKeySpec(key.getBytes(), "AES");
        ecipher = Cipher.getInstance("AES");
        dcipher = Cipher.getInstance("AES");
        ecipher.init(Cipher.ENCRYPT_MODE, k);
        dcipher.init(Cipher.DECRYPT_MODE, k);
    }

    public String encrypt_string(String str) throws Exception {
        byte[] utf8 = str.getBytes("UTF8");
        byte[] enc = ecipher.doFinal(utf8);
        return Base64.getEncoder().encodeToString(enc);
    }

    public String decrypt_string(String str) throws Exception {
        byte[] dec = Base64.getDecoder().decode(str);
        byte[] utf8 = dcipher.doFinal(dec);
        return new String(utf8, "UTF8");
    }

    public void encrypt_file(String input_path, String output_path) throws Exception {
        encrypt_file(input_path, output_path, false);
    }
    public void encrypt_file(String input_path, String output_path, boolean delete_input_file) throws Exception {
        FileInputStream in = new FileInputStream(input_path);
        FileOutputStream out = new FileOutputStream(output_path);
        out.write(ecipher.doFinal(in.readAllBytes()));
        in.close();
        out.close();
        if (delete_input_file)
            if (!new File(input_path).delete())
                throw new IOException("Failed to delete input file");
    }

    public void decrypt_file(String input_path, String output_path) throws Exception {
        decrypt_file(input_path, output_path, false);
    }
    public void decrypt_file(String input_path, String output_path, boolean delete_input_file) throws Exception {
        FileInputStream in = new FileInputStream(input_path);
        FileOutputStream out = new FileOutputStream(output_path);
        out.write(dcipher.doFinal(in.readAllBytes()));
        in.close();
        out.close();
        if (delete_input_file)
            if (!new File(input_path).delete())
                throw new IOException("Failed to delete input file");
    }

    public String encrypt_string_bcrypt(String str) {
        return BCrypt.hashpw(str, BCrypt.gensalt(bcrypt_salt));
    }

    public boolean check_string_bcrypt(String str) {
        return BCrypt.checkpw(str, BCrypt.gensalt(bcrypt_salt));
    }
}