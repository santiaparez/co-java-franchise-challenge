package org.sonarqube.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SimpleSonarPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        SonarExtension extension = project.getExtensions().create("sonar", SonarExtension.class);

        project.getTasks().register("sonar", task -> {
            task.setGroup("verification");
            task.setDescription("Placeholder SonarQube analysis task.");
            task.doLast(t -> project.getLogger().lifecycle(
                    "Sonar analysis properties: " + extension.getProperties()
            ));
        });
    }
}
