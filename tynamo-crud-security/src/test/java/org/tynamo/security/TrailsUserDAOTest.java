package org.tynamo.security;

import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.tynamo.persistence.PersistenceService;
import org.tynamo.security.domain.Role;
import org.tynamo.security.domain.User;

public class TrailsUserDAOTest extends
	AbstractTransactionalSpringContextTests
{

	private PersistenceService persistenceService;

	private TrailsUserDAO trailsUserDAO;


	public TrailsUserDAOTest()
	{
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected void onSetUpInTransaction() throws Exception
	{
		persistenceService = (PersistenceService) applicationContext.getBean("persistenceService");
		//trailsUserDAO = (TrailsUserDAO)applicationContext.getBean("trailsUserDAO");
	}

	@Override
	protected String[] getConfigLocations()
	{
		return new String[]{"applicationContext-test.xml"};
	}

	public void testLoadUserByUsername()
	{
		User user = new User();
		Role role = new Role();
		role.setName("one");
		role.setDescription("one");
		Role role2 = new Role();
		role2.setName("two");
		role2.setDescription("two");
		role = persistenceService.save(role);
		role2 = persistenceService.save(role2);
		user.setUsername("username");
		user.setFirstName("what");
		user.setLastName("blah");
		user.setPassword("user");
		user.setConfirmPassword("user");
		user.getRoles().add(role);
		user.getRoles().add(role2);
		persistenceService.save(user);
		assertNotNull(trailsUserDAO.loadUserByUsername("username"));
	}

	public TrailsUserDAO getTrailsUserDAO()
	{
		return trailsUserDAO;
	}

	public void setTrailsUserDAO(TrailsUserDAO trailsUserDAO)
	{
		this.trailsUserDAO = trailsUserDAO;
	}

}
