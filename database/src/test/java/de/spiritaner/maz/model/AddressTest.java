package de.spiritaner.maz.model;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.persistence.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Florian Böhm
 * @version 0.0.1
 */
@Test(sequential = true)
public class AddressTest {

	private static final String PERSISTENCE_UNIT_NAME = "testDataDb";
	private static final String ADDRESS_STREET_NAME = "Any Street";
	private static EntityManagerFactory factory;
	private Address address;

	@BeforeClass
	public void setup(ITestContext ctx) {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	}

//	@AfterTest
//	public void cleanup(ITestContext ctx) {
//		EntityManager em = factory.createEntityManager();
//		em.getTransaction().begin();
//		em.createQuery("DELETE FROM Address a").executeUpdate();
//		em.getTransaction().commit();
//	}

	@Test void createInvalidAddress() {
		address = new Address();
		address.setStreet(ADDRESS_STREET_NAME);
		address.setHouseNumber("1");
		address.setCity("Any City");
		address.setCountry("Any Country");
		address.setState("Any State");
		address.setAddition("Any Addition");

		Assert.assertNotNull(address);
		Assert.assertEquals(address.getStreet(),ADDRESS_STREET_NAME);
		Assert.assertEquals(address.getHouseNumber(),"1");
		Assert.assertEquals(address.getCity(),"Any City");
		Assert.assertEquals(address.getCountry(),"Any Country");
		Assert.assertEquals(address.getState(),"Any State");
		Assert.assertEquals(address.getAddition(),"Any Addition");
	}

	@Test(dependsOnMethods = {"createInvalidAddress"})
	public void persistInvalidAddress() {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();

		try {
			em.persist(address);
			em.getTransaction().commit();
		} catch (PersistenceException e) {
			em.getTransaction().rollback();
		} finally {
			Assert.assertEquals(em.contains(address),false);
			em.close();
		}
	}

	@Test(dependsOnMethods = {"persistInvalidAddress"})
	public void makeAddressValid() {
		address.setPostCode("123456");

		Assert.assertEquals(address.getPostCode(),"123456");
	}

	@Test(dependsOnMethods = {"makeAddressValid"})
	public void persistAddress() {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		address = em.merge(address);
		em.getTransaction().commit();
		em.close();

		Assert.assertNotNull(address.getId());
		Assert.assertEquals(address.getId(),factory.getPersistenceUnitUtil().getIdentifier(address));
	}

	@Test(dependsOnMethods = {"persistAddress"})
	public void selectAddress() {
		EntityManager em = factory.createEntityManager();
		Query query = em.createQuery("SELECT a FROM Address a where a.street='"+ADDRESS_STREET_NAME+"'");
		Collection<Address> results = query.getResultList();

		Assert.assertEquals(results.size(),1);

		Iterator<Address> iterator = results.iterator();
		em.close();

		while(iterator.hasNext()) {
			Assert.assertEquals(iterator.next().getStreet(), "Any Street");
		}
	}

	@Test(dependsOnMethods = {"selectAddress"})
	public void updateAddress() {
		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();
		Address managedAddress = em.find(Address.class, address.getId());
		managedAddress.setCountry("Other country");
		em.getTransaction().commit();

		Query query = em.createQuery("SELECT a FROM Address a where a.country='Other country'");
		Collection<Address> results = query.getResultList();
		em.close();

		Assert.assertEquals(results.size(),1);
	}

	@Test(dependsOnMethods = {"updateAddress"}, expectedExceptions = {RollbackException.class})
	public void updateAddressToInvalid() {
		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();
		Address managedAddress = em.find(Address.class, address.getId());
		managedAddress.setStreet(null);
		em.getTransaction().commit();
	}
}
