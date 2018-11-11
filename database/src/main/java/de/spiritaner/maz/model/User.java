package de.spiritaner.maz.model;

import de.spiritaner.maz.controller.user.UserEditorDialogController;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Entity
@Identifiable.Annotation(editorDialogClass = UserEditorDialogController.class, identifiableName = "Benutzer")
@NamedQueries({
	@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
	@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
})
public class User implements Identifiable {

	private static final Logger logger = Logger.getLogger(User.class);
	private static final Integer BCRYPT_ROUNDS = 12;

	private LongProperty id;
	private StringProperty username;
	private String password;
	private String passwordHash;
	private byte[] unencryptedDatabaseKey;
	private byte[] encryptedDatabaseKey;
	private String databaseKeySalt;
	private String obsoleteUsername;

	public User() {
		id = new SimpleLongProperty();
		username = new SimpleStringProperty();

		// It is important to save the users old editable when renaming the user in the H2 user table
		username.addListener((observable, oldValue, newValue) -> {
			obsoleteUsername = oldValue;
		});
	}

	public User(String username, String password) {
		this();
		setUsername(username);
		setPassword(password);
	}

	@PrePersist @PreUpdate
	public void hashPassword() throws Exception {
		System.out.println("CALLED hashPassword()");

		// If the unencrypted user password is set and it is not empty ...
		if(password != null && !password.trim().isEmpty()) {
			// ... hash it with BCrypt so it is correctly salted
			passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
			logger.info("We got '"+passwordHash+"' out of '"+password+"'");

			// Also if the unencrypted database editable is set (happens on database or user creation) ...
			if (unencryptedDatabaseKey.length > 0) {
				logger.info("Unencrypted database aes editable is '"+DatatypeConverter.printHexBinary(unencryptedDatabaseKey)+"'");

				try {
					// ... salt the user specific database editable every time
					String tmpSalt = BCrypt.gensalt(12);
					// ... generate an AES cipher instance with the user specific aes editable (128bit)
					Cipher cipher = Cipher.getInstance("AES");
					cipher.init(Cipher.ENCRYPT_MODE, generateUserSpecificAESKey(tmpSalt));

					encryptedDatabaseKey = cipher.doFinal(unencryptedDatabaseKey);
					logger.info("Encrypted database aes editable '"+DatatypeConverter.printHexBinary(encryptedDatabaseKey)+"'");
					unencryptedDatabaseKey = null;
					databaseKeySalt = tmpSalt;
				} catch(Exception e) {
					throw e;
				}
			}

			password = null;
		}
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id.get();
	}

	@Override
	public LongProperty idProperty() {
		return id;
	}

	public void setId(Long id) {
		this.id.set(id);
	}

	@Column(nullable = false, unique = true)
	public String getUsername() {
		return username.get();
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public StringProperty usernameProperty() {
		return username;
	}

	@Transient
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(nullable = false)
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	@Transient
	public byte[] getUnencryptedDatabaseKey() {
		if(password != null && !password.trim().isEmpty() && databaseKeySalt != null) {
			try {
				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.DECRYPT_MODE, generateUserSpecificAESKey(databaseKeySalt));

				unencryptedDatabaseKey = cipher.doFinal(encryptedDatabaseKey);
			} catch (Exception e) {
				// TODO visit correct
				logger.error(e);
			}
		}

		return unencryptedDatabaseKey;
	}

	public void setUnencryptedDatabaseKey(byte[] unencryptedDatabaseKey) {
		this.unencryptedDatabaseKey = unencryptedDatabaseKey;
	}

	@Column(nullable = false)
	public byte[] getEncryptedDatabaseKey() {
		return encryptedDatabaseKey;
	}

	public void setEncryptedDatabaseKey(byte[] encryptedDatabaseKey) {
		this.encryptedDatabaseKey = encryptedDatabaseKey;
	}

	@Column(nullable = false)
	public String getDatabaseKeySalt() {
		return databaseKeySalt;
	}

	public void setDatabaseKeySalt(String databaseKeySalt) {
		this.databaseKeySalt = databaseKeySalt;
	}

	private SecretKeySpec generateUserSpecificAESKey(String salt) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] key = (salt + username + password).getBytes("UTF-8");
		MessageDigest shaMD = MessageDigest.getInstance("SHA-1");
		key = shaMD.digest(key);
		key = Arrays.copyOf(key, 16);

		// TODO disable this here before release!
		logger.info("User specific aes editable is '"+DatatypeConverter.printHexBinary(key)+"'");

		return new SecretKeySpec(key, "AES");
	}

	@Transient
	public String getObsoleteUsername() {
		return obsoleteUsername;
	}
}
