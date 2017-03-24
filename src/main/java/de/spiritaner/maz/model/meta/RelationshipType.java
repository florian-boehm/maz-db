package de.spiritaner.maz.model.meta;

import de.spiritaner.maz.controller.meta.RelationshipTypeEditorDialogController;
import de.spiritaner.maz.model.Identifiable;
import org.hibernate.envers.Audited;

import javax.persistence.*;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Audited(targetAuditMode = NOT_AUDITED)
@Identifiable.Annotation(editorDialogClass = RelationshipTypeEditorDialogController.class, identifiableName = "Beziehungsart")
@NamedQueries({
		  @NamedQuery(name = "RelationshipType.findAll", query = "SELECT rt FROM RelationshipType rt"),
		  @NamedQuery(name = "RelationshipType.findAllWithoutInverse", query = "SELECT rt FROM RelationshipType rt WHERE rt.inverseRelationshipType IS NULL"),
})
public class RelationshipType extends MetaClass {

	private RelationshipType inverseRelationshipType;

	@OneToOne(fetch = FetchType.EAGER)
	public RelationshipType getInverseRelationshipType() {
		return inverseRelationshipType;
	}

	public void setInverseRelationshipType(RelationshipType inverseRelationshipType) {
		this.inverseRelationshipType = inverseRelationshipType;
	}

	public static RelationshipType createEmpty() {
		RelationshipType result = new RelationshipType();
		result.setDescription("-/-");
		result.setId(-1L);
		return result;
	}
}
