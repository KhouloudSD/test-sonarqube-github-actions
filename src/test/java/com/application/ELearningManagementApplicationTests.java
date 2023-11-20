package com.application;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;


import java.security.Provider;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;


@SpringBootTest
class ELearningManagementApplicationTests {


  private JavaClasses importedClasses;

  @BeforeEach
  public void setup() {
    importedClasses = new ClassFileImporter()
      .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
      .importPackages("com.springboot.testing.archunit");
  }

  @Test
  void serviceClassesShouldOnlyBeAccessedByController() {
    classes()
      .that().resideInAPackage("..Service..")
      .should().onlyBeAccessed().byAnyPackage("..Service..", "..Controller..")
      .check(importedClasses);
  }


  @Test
  void fieldInjectionNotUseAutowiredAnnotation() {
    noFields()
      .should().beAnnotatedWith(Autowired.class)
      .check(importedClasses);
  }
  @Test
  void repositoryClassesShouldHaveSpringRepositoryAnnotation() {
    classes()
      .that().resideInAPackage("..repository..")
      .should().beAnnotatedWith(Repository.class)
      .check(importedClasses);
  }
  @Test
  void serviceClassesShouldHaveSpringServiceAnnotation() {
    classes()
      .that().resideInAPackage("..service..")
      .should().beAnnotatedWith(Service.class)
      .check(importedClasses);
  }

  @Test
  void layeredArchitectureShouldBeRespected() {
    layeredArchitecture()
      .layer("Controller").definedBy("..controller..")
      .layer("Service").definedBy("..service..")
      .layer("Repository").definedBy("..repository..")
      .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
      .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
      .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
      .check(importedClasses);

  }

  @Test
	void contextLoads() {
	}


}
