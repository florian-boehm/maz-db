package de.spiritaner.maz.model;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Test(sequential = true)
public class GenderTest {

	private static final String PERSISTENCE_UNIT_NAME = "testDataDb";
	private static final String GENDER_DESCRIPTION = "Female";
	private static EntityManagerFactory factory;
	private Gender gender;

	@BeforeClass
	public void setup(ITestContext ctx) {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	}

	@AfterClass
	public void cleanup(ITestContext ctx) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM Gender g WHERE g.description='"+GENDER_DESCRIPTION+"'").executeUpdate();
		em.getTransaction().commit();
	}

	@Test
	void createInvalidGender() {
		gender = new Gender();

		Assert.assertNotNull(gender);
	}

	@Test(dependsOnMethods = {"createInvalidGender"})
	public void persistInvalidGender() {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		try {
			em.persist(gender);
			em.getTransaction().commit();
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
		} finally {
			Assert.assertEquals(em.contains(gender),false);
			em.close();
		}
	}

	@Test(dependsOnMethods = {"persistInvalidGender"})
	public void makeGenderValid() {
		gender.setDescription(GENDER_DESCRIPTION);

		Assert.assertEquals(gender.getDescription(),GENDER_DESCRIPTION);
	}

	@Test(dependsOnMethods = {"makeGenderValid"})
	public void persistGender() {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		gender = em.merge(gender);
		em.getTransaction().commit();
		em.close();

		Assert.assertNotNull(gender.getId());
		Assert.assertEquals(gender.getId(),factory.getPersistenceUnitUtil().getIdentifier(gender));
	}

	@Test(dependsOnMethods = {"persistGender"})
	public void selectGender() {
		EntityManager em = factory.createEntityManager();
		Query query = em.createQuery("SELECT g FROM Gender g where g.description='"+GENDER_DESCRIPTION+"'");
		Collection<Gender> results = query.getResultList();

		Assert.assertEquals(results.size(),1);

		Iterator<Gender> iterator = results.iterator();
		em.close();

		while(iterator.hasNext()) {
			Assert.assertEquals(iterator.next().getDescription(), GENDER_DESCRIPTION);
		}
	}

	@Test(dependsOnMethods = {"selectGender"})
	public void updateGender() {
		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();
		Gender managedAddress = em.find(Gender.class, gender.getId());
		managedAddress.setDescription("Male");
		em.getTransaction().commit();

		Query query = em.createQuery("SELECT g FROM Gender g where g.description='Male'");
		Collection<Address> results = query.getResultList();
		em.close();

		Assert.assertEquals(results.size(),1);
	}

	@Test(dependsOnMethods = {"updateGender"}, expectedExceptions = {RollbackException.class})
	public void updateGenderToInvalid() {
		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();
		Gender managedAddress = em.find(Gender.class, gender.getId());
		managedAddress.setDescription(null);
		em.getTransaction().commit();
	}
}
