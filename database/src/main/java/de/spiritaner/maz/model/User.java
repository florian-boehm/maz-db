package de.spiritaner.maz.model;

import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Florian on 8/10/2016.
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
	@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
})
public class User {

	private static final Logger logger = Logger.getLogger(User.class);
	private static final Integer BCRYPT_ROUNDS = 12;

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Transient
	private String password;

	@Column(nullable = false)
	private String passwordHash;

	@Transient
	private byte[] unencryptedDatabaseKey;

	@Column(nullable = false)
	private byte[] encryptedDatabaseKey;

	@Column(nullable = false)
	private String databaseKeySalt;

	public User() {

	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@PrePersist @PreUpdate
	public void hashPassword() throws Exception {
		// If the unencrypted user password is set and it is not empty ...
		if(password != null && !password.trim().isEmpty()) {
			// ... hash it with BCrypt so it is correctly salted
			passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
			logger.info("We got '"+passwordHash+"' out of '"+password+"'");

			// Also if the unencrypted database key is set (happens on database or user creation) ...
			if (unencryptedDatabaseKey.length > 0) {
				logger.info("Unencrypted database aes key is '"+DatatypeConverter.printHexBinary(unencryptedDatabaseKey)+"'");

				try {
					// ... salt the user specific database key every time
					String tmpSalt = BCrypt.gensalt(12);
					// ... generate an AES cipher instance with the user specific aes key (128bit)
					Cipher cipher = Cipher.getInstance("AES");
					cipher.init(Cipher.ENCRYPT_MODE, generateUserSpecificAESKey(tmpSalt));

					encryptedDatabaseKey = cipher.doFinal(unencryptedDatabaseKey);
					logger.info("Encrypted database aes key '"+DatatypeConverter.printHexBinary(encryptedDatabaseKey)+"'");
					unencryptedDatabaseKey = null;
					databaseKeySalt = tmpSalt;
				} catch(Exception e) {
					throw e;
				}
			}

			password = null;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public byte[] getUnencryptedDatabaseKey() {
		if(password != null && !password.trim().isEmpty() && databaseKeySalt != null) {
			try {
				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, generateUserSpecificAESKey(databaseKeySalt));

				unencryptedDatabaseKey = cipher.doFinal(encryptedDatabaseKey);
			} catch (Exception e) {
				// TODO handle correct
				logger.error(e);
			}
		}

		return unencryptedDatabaseKey;
	}

	public void setUnencryptedDatabaseKey(byte[] unencryptedDatabaseKey) {
		this.unencryptedDatabaseKey = unencryptedDatabaseKey;
	}

	public byte[] getEncryptedDatabaseKey() {
		return encryptedDatabaseKey;
	}

	public void setEncryptedDatabaseKey(byte[] encryptedDatabaseKey) {
		this.encryptedDatabaseKey = encryptedDatabaseKey;
	}

	private SecretKeySpec generateUserSpecificAESKey(String salt) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] key = (salt + username + password).getBytes("UTF-8");
		MessageDigest shaMD = MessageDigest.getInstance("SHA-1");
		key = shaMD.digest(key);
		key = Arrays.copyOf(key, 16);

		// TODO disable this here before release!
		logger.info("User specific aes key is '"+DatatypeConverter.printHexBinary(key)+"'");

		return new SecretKeySpec(key, "AES");
	}
}
