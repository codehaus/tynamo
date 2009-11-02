package org.tynamo.security;

import org.tynamo.descriptor.IClassDescriptor;
import org.tynamo.descriptor.TrailsClassDescriptor;
import org.tynamo.test.Foo;

public class ClassSecurityRestrictionTest extends SecurityRestrictionTest
{

	public ClassSecurityRestrictionTest()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public ClassSecurityRestrictionTest(String arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public void testRestrict() throws Exception
	{
		ClassSecurityRestriction classRestriction = new ClassSecurityRestriction();
		IClassDescriptor classDescriptor = new TrailsClassDescriptor(Foo.class);
		classRestriction.setRequiredRole(new String[]{"admin"} );
		classRestriction.setRestrictionType(RestrictionType.VIEW);
		classRestriction.restrict(authorities.adminAuthority, classDescriptor);
		assertFalse(classDescriptor.isHidden());
		classDescriptor = new TrailsClassDescriptor(Foo.class);
		classRestriction.restrict(authorities.noAdminAuthority, classDescriptor);
		assertTrue(classDescriptor.isHidden());

		classRestriction.setRestrictionType(RestrictionType.UPDATE);
		classDescriptor = new TrailsClassDescriptor(Foo.class);
		classRestriction.restrict(authorities.noAdminAuthority, classDescriptor);
		assertFalse(classDescriptor.isAllowSave());

		classRestriction.setRestrictionType(RestrictionType.REMOVE);
		classDescriptor = new TrailsClassDescriptor(Foo.class);
		classRestriction.restrict(authorities.noAdminAuthority, classDescriptor);
		assertFalse(classDescriptor.isAllowRemove());
	}
}
