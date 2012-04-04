package org.tynamo.security.jpa;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.services.AspectDecorator;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.ioc.services.TapestryIOCModule;
import org.apache.tapestry5.ioc.test.IOCTestCase;
import org.apache.tapestry5.jpa.JpaModule;
import org.apache.tapestry5.json.services.JSONModule;
import org.apache.tapestry5.services.TapestryModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tynamo.exceptionpage.services.ExceptionPageModule;
import org.tynamo.security.jpa.annotations.Operation;
import org.tynamo.security.jpa.annotations.RequiresAssociation;
import org.tynamo.security.jpa.annotations.RequiresRole;
import org.tynamo.security.jpa.internal.SecureEntityManager;
import org.tynamo.security.services.SecurityModule;
import org.tynamo.security.services.SecurityService;

public class JpaSecurityModuleUnitTest extends IOCTestCase {
	private Registry registry;
	private AspectDecorator aspectDecorator;
	private EntityManager delegate;
	private EntityManager interceptor;
	private SecurityService securityService;
	private HttpServletRequest request;
	private PropertyAccess propertyAccess;

	@BeforeClass
	public void setup() {
		EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testpersistence");
		delegate = emFactory.createEntityManager();
		securityService = mock(SecurityService.class);
		request = mock(HttpServletRequest.class);

		RegistryBuilder builder = new RegistryBuilder();
		builder.add(TapestryModule.class);
		// IOCUtilities.addDefaultModules(builder);
		builder.add(TapestryIOCModule.class);
		builder.add(JSONModule.class);
		builder.add(JpaModule.class);
		builder.add(SecurityModule.class);
		builder.add(ExceptionPageModule.class);
		// builder.add(JpaSecurityModule.class);
		registry = builder.build();
		// registry = IOCUtilities.buildDefaultRegistry();

		aspectDecorator = registry.getService(AspectDecorator.class);
		propertyAccess = registry.getService(PropertyAccess.class);
		// // final AspectInterceptorBuilder<EntityManager> aspectBuilder = aspectDecorator.createBuilder(EntityManager.class,
		// // delegate, "secureEntityManager");
		// // JpaSecurityModule.secureEntityOperations(aspectBuilder, securityService, request, propertyAccess);
		// interceptor = aspectBuilder.build();
		interceptor = new SecureEntityManager(securityService, propertyAccess, request, delegate);
	}

	@AfterMethod
	public void clearDb() {
		if (delegate.getTransaction().isActive()) delegate.getTransaction().rollback();
		delegate.getTransaction().begin();
		delegate.createQuery("DELETE FROM TestEntity m").executeUpdate();
		delegate.createQuery("DELETE FROM TestOwnerEntity t").executeUpdate();
		delegate.getTransaction().commit();
	}

	@AfterClass
	public void shutdown() {
		registry.shutdown();

		aspectDecorator = null;
		registry = null;
	}

	private void mockSubject(Long principalId) {
		Subject subject = mock(Subject.class);
		when(subject.isAuthenticated()).thenReturn(true);
		PrincipalCollection principalCollection = mock(PrincipalCollection.class);
		when(principalCollection.getPrimaryPrincipal()).thenReturn(principalId);
		when(subject.getPrincipals()).thenReturn(principalCollection);
		when(securityService.getSubject()).thenReturn(subject);
	}

	@Test
	public void secureFind() {
		delegate.getTransaction().begin();
		TestOwnerEntity owner = new TestOwnerEntity();
		owner.setId(1L);
		delegate.persist(owner);
		TestEntity entity = new TestEntity();
		entity.setOwner(owner);
		entity.setId(1L);
		delegate.persist(entity);
		delegate.getTransaction().commit();
		mockSubject(1L);

		entity = interceptor.find(TestEntity.class, 1L);
		assertNotNull(entity);
	}

	@Test
	public void findByAssociation() {
		delegate.getTransaction().begin();
		TestOwnerEntity owner = new TestOwnerEntity();
		owner.setId(1L);
		delegate.persist(owner);
		TestEntity entity = new TestEntity();
		entity.setOwner(owner);
		entity.setId(1L);
		delegate.persist(entity);
		delegate.getTransaction().commit();

		mockSubject(1L);
		entity = interceptor.find(TestEntity.class, null);
		assertNotNull(entity);
	}

	@Test
	public void findSelfByAssociation() {
		delegate.getTransaction().begin();
		TestOwnerEntity owner = new TestOwnerEntity();
		owner.setId(1L);
		delegate.persist(owner);
		delegate.getTransaction().commit();

		mockSubject(1L);
		owner = interceptor.find(TestOwnerEntity.class, null);
		assertNotNull(owner);

	}

	@Test(expectedExceptions = { NonUniqueResultException.class })
	public void findMultipleByAssociation() {
		delegate.getTransaction().begin();
		TestOwnerEntity owner = new TestOwnerEntity();
		owner.setId(1L);
		delegate.persist(owner);
		TestEntity entity = new TestEntity();
		entity.setOwner(owner);
		entity.setId(1L);
		delegate.persist(entity);
		entity = new TestEntity();
		entity.setOwner(owner);
		entity.setId(2L);
		delegate.persist(entity);
		delegate.getTransaction().commit();

		mockSubject(1L);
		entity = interceptor.find(TestEntity.class, null);
	}

	@Test(expectedExceptions = { EntitySecurityException.class })
	public void persistProtectedByRole() {
		interceptor.getTransaction().begin();
		RoleWriteProtectedEntity rolePersistProtectedEntity = new RoleWriteProtectedEntity();
		interceptor.persist(rolePersistProtectedEntity);
		interceptor.getTransaction().commit();
	}

	@Test(expectedExceptions = { EntitySecurityException.class })
	public void mergeProtectedByRole() {
		interceptor.getTransaction().begin();
		RoleWriteProtectedEntity rolePersistProtectedEntity = new RoleWriteProtectedEntity();
		interceptor.merge(rolePersistProtectedEntity);
		interceptor.getTransaction().commit();
	}

	@Test(expectedExceptions = { EntitySecurityException.class })
	public void removeProtectedByRole() {
		interceptor.getTransaction().begin();
		RoleWriteProtectedEntity rolePersistProtectedEntity = new RoleWriteProtectedEntity();
		interceptor.remove(rolePersistProtectedEntity);
		interceptor.getTransaction().commit();
	}

	@Entity(name = "RoleWriteProtectedEntity")
	@RequiresRole(value = "owner", operations = Operation.WRITE)
	public static class RoleWriteProtectedEntity {
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		private Long id;
	}

	@Entity(name = "TestEntity")
	@RequiresAssociation("owner")
	public static class TestEntity {

		@Id
		private Long id;

		@ManyToOne
		private TestOwnerEntity owner;

		public TestOwnerEntity getOwner() {
			return owner;
		}

		public void setOwner(TestOwnerEntity owner) {
			this.owner = owner;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

	}

	@Entity(name = "TestOwnerEntity")
	@RequiresAssociation
	public static class TestOwnerEntity {

		@Id
		private Long id;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

	}

}
