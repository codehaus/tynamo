package org.trails.io;

import org.apache.tapestry.services.DataSqueezer;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.trails.descriptor.DescriptorService;
import org.trails.descriptor.IdentifierDescriptor;
import org.trails.descriptor.TrailsClassDescriptor;
import org.trails.persistence.PersistenceService;
import org.trails.test.Foo;

public class SqueezableSqueezeAdaptorTest extends MockObjectTestCase
{
	Mock dataSqueezerMock = new Mock(DataSqueezer.class);
	Mock persistenceMock = new Mock(PersistenceService.class);
	Mock descriptorServiceMock = new Mock(DescriptorService.class);
	TrailsEntitySqueezerFilter adaptor = new TrailsEntitySqueezerFilter();
	Foo foo;
	
	public void setUp() throws Exception
	{
		foo = new Foo();
		foo.setId(new Integer(1));
		adaptor.setDescriptorService((DescriptorService)descriptorServiceMock.proxy());
		adaptor.setPersistenceService((PersistenceService)persistenceMock.proxy());
	}
	
	public void testSqueeze() throws Exception
	{
		TrailsClassDescriptor classDescriptor = new TrailsClassDescriptor(Foo.class);
		IdentifierDescriptor idDescriptor = new IdentifierDescriptor(Foo.class, "id", Integer.class);
		classDescriptor.getPropertyDescriptors().add(idDescriptor);
		descriptorServiceMock.expects(once()).method("getClassDescriptor")
			.with(eq(Foo.class)).will(returnValue(classDescriptor));
		
		
		
		
		// need to make a contraint so i can do tests on the arguments
		dataSqueezerMock.expects(once()).method("squeeze").with(new Constraint(){

			public StringBuffer describeTo(StringBuffer arg0)
			{
				arg0.append("whatever");
				return arg0;
			}

			public boolean eval(Object arg)
			{
				ObjectIdentity oid = (ObjectIdentity)arg;
				return (oid.getId().equals(new Integer(1)) && oid.getEntityName().equals(Foo.class.getName()));
			}
			
		}).will(returnValue("squeezed"));
		String squeezed = adaptor.squeeze(foo, (DataSqueezer)dataSqueezerMock.proxy());
		
	}
	
	public void testSqueezeNonEntity()
	{
		Double three = new Double(3);
		descriptorServiceMock.expects(once()).method("getClassDescriptor").with(eq(Double.class)).will(returnValue(null));
		dataSqueezerMock.expects(once()).method("squeeze").with(eq(three)).will(returnValue("3"));
		assertEquals("3", adaptor.squeeze(three, (DataSqueezer)dataSqueezerMock.proxy()));
	}
	
	public void testUnsqueeze() throws Exception
	{
		ObjectIdentity oid = new ObjectIdentity(Foo.class.getName(), new Integer(1));
		dataSqueezerMock.expects(once()).method("unsqueeze").with(eq("squeezedOid")).will(returnValue(oid));
		persistenceMock.expects(once()).method("getInstance").with(eq(Foo.class), eq(new Integer(1))).will(returnValue(foo));
		assertEquals(foo, 
				adaptor.unsqueeze(TrailsEntitySqueezerFilter.PREFIX +"squeezedOid",
					(DataSqueezer)dataSqueezerMock.proxy()));
	}
	
	public void testUnsqueezeNonEntity()
	{
		dataSqueezerMock.expects(once()).method("unsqueeze").with(eq("notanentity")).will(returnValue(new Integer(1)));
		assertEquals(new Integer(1), adaptor.unsqueeze("notanentity", (DataSqueezer)dataSqueezerMock.proxy()));
	}

}
