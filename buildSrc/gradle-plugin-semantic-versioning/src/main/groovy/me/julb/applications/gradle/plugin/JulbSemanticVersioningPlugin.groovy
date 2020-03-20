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
package me.julb.applications.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.github.zafarkhaja.semver.Version
import org.ajoberstar.grgit.gradle.GrgitPlugin

/**
 * The plugin to handle semantic versioning for a project. <P>
 *
 * @author Julb.
 */
class JulbSemanticVersioningPlugin implements Plugin<Project> {

    /**
     * The snapshot suffix.
     */
    public static String SNAPSHOT_SUFFIX = 'SNAPSHOT'

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(Project project) {
        project.logger.info('[Julb] Applying options for Semantic Versioning Plugin for project ' + project.name)

        def rootProject = project.rootProject
		
		// Apply Grgit.        
        rootProject.plugins.apply(GrgitPlugin)
        
        def latestCommit = rootProject.grgit.head()
        def releaseVersion = rootProject.version.replaceAll('-' + SNAPSHOT_SUFFIX, '')
    	
    	rootProject.ext {
	    	// Add git properties.
    	    gitRevision = latestCommit.id
    	    gitShortRevision = latestCommit.abbreviatedId
    	    gitAuthorName = latestCommit.author.name
    	    gitAuthorEmail = latestCommit.author.email
    	    
	    	// Add build version.
    	    buildVersion = releaseVersion + '.' + gitShortRevision
    	}

        

        rootProject.gradle.projectsEvaluated {
        
			// Displays the current version.
        	rootProject.task('currentVersion', description: 'Display the project version') {
	            doLast {
	            	println "$project.version"
	            }
	        }
	        
	        // Displays the current build version.
        	rootProject.task('currentBuildVersion', description: 'Display the project build version') {
	            doLast {
	            	println "$project.buildVersion"
	            }
	        }
	        
        
	        // Change version.
	        def changeVersionModes = [ 'Major', 'Minor', 'Patch', 'Release', 'Custom' ]
	        changeVersionModes.each { changeVersionMode ->
				rootProject.task("changeVersion${changeVersionMode}", description: "Change the project version using ${changeVersionMode} method") {
					doLast {
						File gradlePropertiesFile = new File(rootProject.projectDir, 'gradle.properties')
	                    if(gradlePropertiesFile.exists()) {
	                    	// Compute new version.
		                    println "[Julb] Asked to update version with parameter <$changeVersionMode>"
		                    def newVersion = ''
		                    if(changeVersionMode.equals('Custom')) {
		                    	if(!rootProject.hasProperty("newVersion")) {
		                            throw new Exception('Missing newVersion property.')
		                        }
		                        newVersion = rootProject.newVersion
		                    } else if(changeVersionMode.equals('Release')) {
		                        newVersion = releaseVersion
		                    } else {
		                        def semverVersion = Version.valueOf(rootProject.version)
		                        if(changeVersionMode.equals('Major')) {
		                            semverVersion = semverVersion.incrementMajorVersion(SNAPSHOT_SUFFIX)
		                        } else if(changeVersionMode.equals('Minor')) {
		                            semverVersion = semverVersion.incrementMinorVersion(SNAPSHOT_SUFFIX)
		                        } else if(changeVersionMode.equals('Patch')) {
		                            semverVersion = semverVersion.incrementPatchVersion(SNAPSHOT_SUFFIX)
		                        }
		                        newVersion = semverVersion.toString()
		                    }
		
		                    // Change project version.
		                    rootProject.version = newVersion
		                    println "[Julb] New project version is $rootProject.version"
		
		                    // Update gradle.properties
		                    ant.propertyfile(file: 'gradle.properties') {
	                            entry(key: 'version', value: newVersion)
	                        }
	                    }
	                }
				}
	        }
	    }
    }
}