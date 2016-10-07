package de.spiritaner.maz.model;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.type.descriptor.java.DateTypeDescriptor;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
public class PersonTest {

	private static final String PERSISTENCE_UNIT_NAME = "testDataDb";
	private static final String PERSON_PRENAME = "Max";
	private static EntityManagerFactory factory;
	private Person person;
	private Long genderId;

	@BeforeClass
	public void setup(ITestContext ctx) {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager em = factory.createEntityManager();
		Gender tmpGender = new Gender();
		tmpGender.setDescription("Male");
		em.getTransaction().begin();
		em.persist(tmpGender);
		em.getTransaction().commit();
		genderId = tmpGender.getId();
	}

	@AfterClass
	public void cleanup(ITestContext ctx) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM Person p WHERE p.prename='"+PERSON_PRENAME+"'").executeUpdate();
		em.createQuery("DELETE FROM Gender g WHERE g.description='Male'").executeUpdate();
		em.getTransaction().commit();
	}

	@Test
	void createInvalidPerson() {
		person = new Person();
		person.setPrename(PERSON_PRENAME);
		LocalDate now = LocalDate.now();
		person.setBirthday(now);
		person.setBirthplace("Any City");

		Assert.assertNotNull(person);
		Assert.assertEquals(person.getPrename(), PERSON_PRENAME);
//		Assert.assertEquals(person.getSurname(),"Any");
		Assert.assertEquals(person.getBirthday(),now);
		Assert.assertEquals(person.getBirthplace(),"Any City");
	}

	@Test(dependsOnMethods = {"createInvalidPerson"})
	public void persistInvalidPerson() {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		try {
			em.persist(person);
			em.getTransaction().commit();
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
		} finally {
			Assert.assertEquals(em.contains(person),false);
			em.close();
		}
	}

	@Test(dependsOnMethods = {"persistInvalidPerson"})
	public void makePersonValid() {
		EntityManager em = factory.createEntityManager();
		person.setSurname("Any");

		Assert.assertEquals(person.getSurname(),"Any");
	}

	@Test(dependsOnMethods = {"makePersonValid"})
	public void persistPerson() {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		person = em.merge(person);
		em.getTransaction().commit();
		em.close();

		Assert.assertNotNull(person.getId());
		Assert.assertEquals(person.getId(),factory.getPersistenceUnitUtil().getIdentifier(person));
	}

	@Test(enabled=false,dependsOnMethods = {"persistPerson"}, expectedExceptions = {RollbackException.class})
	public void removeGenderUsedByPerson() {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.remove(em.find(Gender.class, genderId));
		em.getTransaction().commit();
		em.close();
	}
}
