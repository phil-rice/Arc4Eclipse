package org.arc4eclipse.panelExerciser.fixtures;

import static org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants.*;

import org.arc4eclipse.arc4eclipseRepository.data.IOrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.IProjectData;
import org.arc4eclipse.arc4eclipseRepository.data.IReleaseData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.OrganisationData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.ProjectData;
import org.arc4eclipse.arc4eclipseRepository.data.impl.ReleaseData;
import org.arc4eclipse.binding.mocks.IBindingBuilder;
import org.arc4eclipse.panelExerciser.JarDataAndPath;
import org.arc4eclipse.utilities.maps.Maps;
import org.eclipse.jdt.core.dom.IBinding;

public class AntTestFixture {
	// Ant
	public static final String antContribJar = "../PanelExerciser/src/main/resources/ant-contrib-1.0b3.jar";
	private final static String orgUrlAntContrib = "http://ant-contrib.sourceforge.net/";
	private final static String projNameAntContrib = "Ant Contrib Tasks";
	public final static IOrganisationData orgAntContrib = new OrganisationData(Maps.<String, Object> makeMap(//
			organisationNameKey, "Ant Contrib Tasks",//
			organisationUrlKey, orgUrlAntContrib,//
			descriptionKey, ""));
	public final static IProjectData projAntContrib = new ProjectData(Maps.<String, Object> makeMap(//
			organisationUrlKey, orgUrlAntContrib,//
			projectNameKey, projNameAntContrib,//
			descriptionKey, "The Ant-Contrib project is a collection of tasks (and at one point maybe types and other tools) for Apache Ant."));
	public final static IReleaseData releaseAntContrib1_0_b3 = new ReleaseData(Maps.<String, Object> makeMap(//
			organisationUrlKey, orgUrlAntContrib,//
			projectNameKey, projNameAntContrib,//
			releaseIdentifierKey, "1.0b3",//
			descriptionKey, ""));

	public final static IBinding Utils$ColumnName = IBindingBuilder.Utils.//
			parent(antContribJar).withPackage("net.sf.antcontrib.logic").withClass("AntCallBack ").//
			child().withMethod("init");

	public final static JarDataAndPath jarAntContrib1_0b3 = new JarDataAndPath(releaseAntContrib1_0_b3, antContribJar);
}
