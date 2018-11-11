package de.spiritaner.maz.util;

import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Relationship;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;

/**
 * This helper class compresses a list of persons to a list of families (or single persons)
 *
 * @author Florian Böhm
 * @version 2017.06.03
 */
public class FamilyHelper {

	public static void merge(final List<Person> personList) {
		List<Relationship> allRelationships = new ArrayList<>();
		List<FamilyMember> families = new ArrayList<>();

		personList.forEach(person -> {
			Hibernate.initialize(person.getRelationships());
			allRelationships.addAll(person.getRelationships());
		});

		for(Relationship relationship : allRelationships) {
			// Find family with from person
			FamilyMember family = null;

			Person toPerson = relationship.getToPerson();

			if(toPerson == null) {
				toPerson = new Person();
				toPerson.setFamilyName(relationship.getToPersonFamilyName());
				toPerson.setFirstName(relationship.getToPersonFirstName());
			}

			for(FamilyMember fm : families) {
				if(fm.isMember(relationship.getFromPerson())) {
					family = fm;
				}
			}

			if(family == null) {
				for(FamilyMember fm : families) {
					if(fm.isMember(toPerson)) {
						family = fm;
					}
				}
			}

			// No family exists for this person
			if(family == null) {
				family = new FamilyMember(null);
				family.nextMember = new FamilyMember(relationship.getFromPerson());
				families.add(family);
			}

			if(!family.isMember(toPerson)) {
				if (relationship.getRelationshipType().getId() <= 2 || relationship.getRelationshipType().getId() > 4) {
					family.insertAfter(relationship.getFromPerson(), toPerson);
				} else if (relationship.getRelationshipType().getId() > 2 && relationship.getRelationshipType().getId() <= 4) {
					family.insertBefore(relationship.getFromPerson(), toPerson);
				}
			}
		}

		for(FamilyMember fm : families) {
			System.out.println(fm.print());
		}
	}

	static class FamilyMember {
		FamilyMember nextMember;
		Person person;

		public FamilyMember(Person person) {
			this.person = person;
		}

		public void insertBefore(Person oldPerson, Person newPerson) {
			if (nextMember != null) {
				if (comp(nextMember.person,oldPerson)) {
					FamilyMember newMember = new FamilyMember(newPerson);
					newMember.nextMember = nextMember;
					nextMember = newMember;
				} else {
					nextMember.insertBefore(oldPerson, newPerson);
				}
			}
		}

		private boolean comp(Person a, Person b) {
			if(a.getId() < 0 && b.getId() < 0) {
				return a.getFirstName().equals(b.getFirstName()) &&
						  a.getFamilyName().equals(b.getFamilyName());
			} else {
				return a.equals(b);
			}
		}

		public void insertAfter(Person oldPerson, Person newPerson) {
			if (person == null && nextMember == null) {
				return;
			} else if (person == null) {
				nextMember.insertAfter(oldPerson, newPerson);
			} else if (comp(this.person, oldPerson)) {
				FamilyMember newMember = new FamilyMember(newPerson);
				newMember.nextMember = nextMember;
				nextMember = newMember;
			} else if(nextMember != null) {
				nextMember.insertAfter(oldPerson, newPerson);
			} else {
				return;
			}
		}

		public boolean isMember(Person person) {
			if (this.person == null && nextMember == null) {
				return false;
			} else if (this.person == null) {
				return nextMember.isMember(person);
			} else if (comp(this.person,person)) {
				return true;
			}else if (nextMember != null) {
				return nextMember.isMember(person);
			} else {
				return false;
			}
		}

		public String print() {
			if(this.nextMember == null) {
				return "["+person.getFullName()+"]";
			} else {
				return ((person != null) ? "["+person.getFullName()+"]->" : "") + nextMember.print();
			}
		}
	}
}
