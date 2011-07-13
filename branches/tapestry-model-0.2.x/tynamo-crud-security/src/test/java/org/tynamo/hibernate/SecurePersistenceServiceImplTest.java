package org.tynamo.hibernate;

import java.util.List;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.TestingAuthenticationToken;
import org.tynamo.security.domain.Role;
import org.tynamo.security.domain.User;
import org.tynamo.seeddata.SeedDataInitializer;
import org.tynamo.test.MockableTransactionalTestCase;

public class SecurePersistenceServiceImplTest extends MockableTransactionalTestCase {
	private SeedDataInitializer seedDataInitializer;

	@Override
	public void onSetUpBeforeTransaction() throws Exception
	{
		super.onSetUpBeforeTransaction();
		seedDataInitializer = (SeedDataInitializer) applicationContext.getBean(SeedDataInitializer.class.getSimpleName());
		seedDataInitializer.init();
	}
	
	public void testSelfAssociationRestrictionOnView() {
  	SecurityContext securityContext = new SecurityContextImpl();
  	GrantedAuthority[] authorities = new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_USER")};
  	Authentication authentication = new TestingAuthenticationToken("username1", "password", authorities);
  	securityContext.setAuthentication(authentication);
  	SecurityContextHolder.setContext(securityContext);
  	
  	User user = new User();
  	user.setUsername("username1");
  	user.setFirstName("Test");
  	user.setLastName("User1");
  	user.setPassword("password");
  	user.setConfirmPassword("password");
  	persistenceService.save(user);
  	
    List<User> users = persistenceService.getAllInstances(User.class);
  	assertEquals(1,users.size());
    
  	authentication = new TestingAuthenticationToken("nonExistentUsername", "password", authorities);
  	securityContext.setAuthentication(authentication);
  	SecurityContextHolder.setContext(securityContext);
    users = persistenceService.getAllInstances(User.class);
  	assertEquals(0,users.size());
	}

	public void testNoSuitableRoleAndNoAssocationOnView() {
  	SecurityContext securityContext = new SecurityContextImpl();
  	GrantedAuthority[] authorities = new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_USER")};
  	Authentication authentication = new TestingAuthenticationToken("username1", "password", authorities);
  	securityContext.setAuthentication(authentication);
  	SecurityContextHolder.setContext(securityContext);
  	
  	List<Role> roles = persistenceService.getAllInstances(Role.class);
  	assertEquals(0,roles.size());
	}
	
	
	public void testRoleRestrictionOnView() {
		List<User> users = persistenceService.getAllInstances(User.class);
    int numberOfUsers = users.size();

		SecurityContext securityContext = new SecurityContextImpl();
  	GrantedAuthority[] authorities = new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_MANAGER")};
  	Authentication authentication = new TestingAuthenticationToken("username1", "password", authorities);
  	securityContext.setAuthentication(authentication);
  	SecurityContextHolder.setContext(securityContext);
    users = persistenceService.getAllInstances(User.class);
    // Requires at least one user to be seeded automatically
    assertTrue(users.size() > 0);
		assertEquals(numberOfUsers, users.size());
		
  	authorities = new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_ROOT")};
  	authentication = new TestingAuthenticationToken("username1", "password", authorities);
  	securityContext.setAuthentication(authentication);
  	SecurityContextHolder.setContext(securityContext);
    users = persistenceService.getAllInstances(User.class);
    assertTrue(users.size() > 0);
		assertEquals(numberOfUsers, users.size());
		
	}

	public void onTearDownAfterTransaction() {
		// Clear context so not to interfere with other tests
  	SecurityContextHolder.clearContext();
	}
}
