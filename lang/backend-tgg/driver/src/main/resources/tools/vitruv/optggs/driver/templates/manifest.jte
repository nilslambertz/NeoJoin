@import tools.vitruv.optggs.driver.Project
@param Project project
Manifest-Version: 1.0
Bundle-Name: ${project.name()}
Bundle-ManifestVersion: 2
Bundle-Version: 0.0.1
Bundle-SymbolicName: ${project.name()};singleton:=true
Bundle-ActivationPolicy: lazy
Bundle-RequiredExecutionEnvironment: JavaSE-1.8
Require-Bundle: org.emoflon.neo.neo4j.adapter,org.emoflon.neo.engine.modules,org.emoflon.neo.neocore,org.eclipse.xtext,org.apache.commons.logging,org.apache.log4j,org.apache.commons.lang
