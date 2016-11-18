package de.spiritaner.maz.model;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author Florian Schwab
 * @version 0.0.1
 */
@Test(sequential = true)
public class ResidenceTest {

	private static final String PERSISTENCE_UNIT_NAME = "testDataDb";
	private static EntityManagerFactory factory;
	private Long addressId;
	private Long personId;
	private Long residenceTypeId;

	@BeforeClass
	public void setup(ITestContext ctx) {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		Address address = new Address();
		address.setStreet("Any Street");
		address.setHouseNumber("1");
		address.setCity("Any City");
		address.setCountry("Any Country");
		address.setState("Any State");
		address.setAddition("Any Addition");
		address.setPostCode("123456");

		Gender gender = new Gender();
		gender.setDescription("Male");

		Person person = new Person();
		person.setFirstName("Prename");
		person.setFamilyName("Surname");
//		Date now = Calendar.getInstance(Locale.GERMANY).getTime();
		person.setBirthday(LocalDate.now());
		person.setBirthplace("Any City");
		person.setGender(gender);

		ResidenceType residenceType = new ResidenceType();
		residenceType.setDescription("Primary address");

		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(address);
		em.persist(person);
		em.persist(residenceType);
		em.getTransaction().commit();
		personId = person.getId();
		addressId = address.getId();
		residenceTypeId = residenceType.getId();
	}

	@AfterTest
	public void cleanup(ITestContext ctx) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM Residence r").executeUpdate();
		em.createQuery("DELETE FROM Person p").executeUpdate();
		em.createQuery("DELETE FROM Gender g").executeUpdate();
		em.createQuery("DELETE FROM Address a").executeUpdate();
		em.getTransaction().commit();
	}

	@Test void createInvalidResidence() {
		EntityManager em = factory.createEntityManager();
		Residence residence = new Residence();
		residence.setAddress(em.find(Address.class,addressId));
		residence.setPerson(em.find(Person.class,personId));

		Assert.assertNotNull(residence);
		Assert.assertEquals(residence.getPerson().getId(),personId);
		Assert.assertEquals(residence.getAddress().getId(),addressId);
	}

	@Test(enabled = false, dependsOnMethods = {"createInvalidAddress"})
	public void persistInvalidAddress() {
//		EntityManager em = factory.createEntityManager();
//		em.getTransaction().begin();
//
//		try {
//			em.persist(address);
//			em.getTransaction().commit();
//		} catch (PersistenceException e) {
//			em.getTransaction().rollback();
//		} finally {
//			Assert.assertEquals(em.contains(address),false);
//			em.close();
//		}
	}

	@Test(enabled = false, dependsOnMethods = {"persistInvalidAddress"})
	public void makeAddressValid() {
//		address.setPostCode("123456");
//
//		Assert.assertEquals(address.getPostCode(),"123456");
	}

	@Test(enabled = false, dependsOnMethods = {"makeAddressValid"})
	public void persistAddress() {
//		EntityManager em = factory.createEntityManager();
//		em.getTransaction().begin();
//		address = em.merge(address);
//		em.getTransaction().commit();
//		em.close();
//
//		Assert.assertNotNull(address.getId());
//		Assert.assertEquals(address.getId(),factory.getPersistenceUnitUtil().getIdentifier(address));
	}

	@Test(enabled = false, dependsOnMethods = {"persistAddress"})
	public void selectAddress() {
//		EntityManager em = factory.createEntityManager();
//		Query query = em.createQuery("SELECT a FROM Address a where a.street='"+ADDRESS_STREET_NAME+"'");
//		Collection<Address> results = query.getResultList();
//
//		Assert.assertEquals(results.size(),1);
//
//		Iterator<Address> iterator = results.iterator();
//		em.close();
//
//		while(iterator.hasNext()) {
//			Assert.assertEquals(iterator.next().getStreet(), "Any Street");
//		}
	}

	@Test(enabled = false, dependsOnMethods = {"selectAddress"})
	public void updateAddress() {
//		EntityManager em = factory.createEntityManager();
//
//		em.getTransaction().begin();
//		Address managedAddress = em.find(Address.class, address.getId());
//		managedAddress.setCountry("Other country");
//		em.getTransaction().commit();
//
//		Query query = em.createQuery("SELECT a FROM Address a where a.country='Other country'");
//		Collection<Address> results = query.getResultList();
//		em.close();
//
//		Assert.assertEquals(results.size(),1);
	}

	@Test(enabled = false, dependsOnMethods = {"updateAddress"}, expectedExceptions = {RollbackException.class})
	public void updateAddressToInvalid() {
//		EntityManager em = factory.createEntityManager();
//
//		em.getTransaction().begin();
//		Address managedAddress = em.find(Address.class, address.getId());
//		managedAddress.setStreet(null);
//		em.getTransaction().commit();
	}
}
