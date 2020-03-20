/**
 *  MIT License
 *  
 *  Copyright (c) 2018 Julb
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package io.julb.applications.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * The plugin to aggregate JavaDoc from all subprojects. <P>
 *
 * @author Julb.
 */
class JulbAggregateJavadocPlugin implements Plugin<Project> {
    
    /**
     * Task name to aggregate JavaDoc.
     */
    private static final String AGGREGATE_JAVADOC_TASK_NAME = 'aggregateJavadoc'

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(Project project) {
        project.logger.info('[Julb] Applying options for Aggregate Javadoc Plugin for project ' + project.name)
        
        def extension = project.extensions.create(AGGREGATE_JAVADOC_TASK_NAME, JulbAggregateJavadocPluginExtension)
        Project rootProject = project.rootProject
        rootProject.gradle.projectsEvaluated {
        	// Ensure project is correctly configured.
            validateExtension(extension)
            
            // Get subprojects to aggregate.
            Set<Project> javaSubprojects = getJavaSubprojects(rootProject, extension)
            
            // Proceed to the task.
            if (!javaSubprojects.isEmpty()) {
                project.task(AGGREGATE_JAVADOC_TASK_NAME, type: Javadoc) {
					title = extension.title
					
                    group = JavaBasePlugin.DOCUMENTATION_GROUP
                    description = 'Aggregates Javadoc API documentation of all subprojects.'
                    
                    destinationDir project.file(extension.outputDir)
	
                    dependsOn javaSubprojects.javadoc
                    source javaSubprojects.javadoc.source
                    classpath = rootProject.files(javaSubprojects.javadoc.classpath)
                }
            }
        }
    }

	/**
	 * Lists the subprojects for which to generate JavaDoc.
	 * @param rootProject the root project.
	 * @param extension the plugin configuration.
	 */
    private static Set<Project> getJavaSubprojects(Project rootProject, JulbAggregateJavadocPluginExtension extension) {
        if (extension.include != null) {
            // We have a set of projects the user wants to include. Make sure each has the Java plugin and proceed
            def includes = new LinkedHashSet(Arrays.asList(extension.include))
            def projects = rootProject.subprojects.findAll { subproject -> includes.remove(subproject.name) }
            projects.forEach { subproject ->
                if (!subproject.plugins.hasPlugin(JavaPlugin)) {
                    throw new GradleException("$AGGREGATE_JAVADOC_TASK_NAME-included project '$subproject.name' does not have the Java plugin, so Javadocs cannot be created" )
                }
            }

            // It is easy to rename a project and forget to add it to the includes. Accordingly, throw if not all includes
            // matched with an actual project
            if (!includes.empty) {
                throw new GradleException("No projects found matching $AGGREGATE_JAVADOC_TASK_NAME includes: " + includes)
            }

            return projects
        } else {

            // By default use all projects with the JavaPlugin, but if excludes are defined, apply those
            def projects = rootProject.subprojects.findAll { subproject -> subproject.plugins.hasPlugin(JavaPlugin) }
            if (extension.exclude != null) {
                def excludes = new LinkedHashSet(Arrays.asList(extension.exclude))
                projects.removeIf { subproject -> excludes.remove(subproject.name) }

                // It is easy to rename a project and forget to add it to the excludes. Accordingly, throw if not all excludes
                // matched with an actual project
                if (!excludes.empty) {
                    throw new GradleException("No projects found matching $AGGREGATE_JAVADOC_TASK_NAME excludes: " + excludes)
                }
            }

            return projects
        }
    }

	/**
	 * Checks the configuration of the plugin.
	 * @param extension the plugin configuration.
	 */
    private static void validateExtension(JulbAggregateJavadocPluginExtension extension) {
        if (extension.include != null && extension.exclude != null) {
            throw new GradleException("If defining includes, excludes cannot also be defined")
        }
    }
}
