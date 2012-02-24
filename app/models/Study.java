package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.GenericModel;
import ext.JavaExtensions;

@Entity
//@NamedNativeQuery(name = "nativeSQL", query = "select * from study", resultClass = Study.class)
public class Study extends GenericModel {

	@Id
	public long pk;
	public String study_id;
	public String study_desc;
	public Date study_datetime;
	public String study_custom1;
	@ManyToOne
	@JoinColumn(name="patient_fk")
	public Patient patient;
	@OneToMany(mappedBy = "study")
	public Set<Series> series;
	@OneToMany(mappedBy = "study")
	public Set<ProjectAssociation> projectAssociations;

	public ProjectAssociation getProjectAssociation(String username) {
		return ProjectAssociation.find("select projectAssociation from Project project, in(project.projectAssociations) projectAssociation where project.person.username = ? and projectAssociation.study = ?", username, this).first();
	}

	public String toDownloadString(String username) {
		String string = String.format("%s_%s", new SimpleDateFormat("yyyyMMddHHmm").format(study_datetime), pk);
		ProjectAssociation projectAssociation = getProjectAssociation(username);
		if (projectAssociation != null) {
			string = String.format("%s_%s", string, projectAssociation.project.name);
			if (projectAssociation.participationID != null && !projectAssociation.participationID.isEmpty()) {
				string = String.format("%s_%s", string, projectAssociation.participationID);
			}
		}
		return string;
	}

	public String toClipboardString() {
		return String.format("%s on %s", patient.pat_name == null ? "" : JavaExtensions.formatAsName(patient.pat_name), study_datetime == null ? "" : JavaExtensions.format(study_datetime));
	}
}
