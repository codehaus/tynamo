package org.tynamo.io;

import org.apache.tapestry.services.DataSqueezer;
import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import org.tynamo.descriptor.DescriptorService;
import org.tynamo.descriptor.TrailsClassDescriptor;
import org.tynamo.persistence.PersistenceService;
import org.tynamo.test.Foo;


public class EntitySquezerTest extends MockObjectTestCase
{

	EntitySqueezerStrategy entitySqueezeStrategy = new EntitySqueezerStrategy();
	TrailsClassDescriptor descriptor = new TrailsClassDescriptor(Foo.class);
	protected Mock descriptorServiceMock = new Mock(DescriptorService.class);
	protected DescriptorService descriptorService;
	protected Mock persistenceServiceMock = new Mock(PersistenceService.class);
	Mock nextDataSqueezer;
	Integer identifier = 32456;
	Foo foo = new Foo();

	protected void setUp() throws Exception
	{
		nextDataSqueezer = new Mock(DataSqueezer.class);
		entitySqueezeStrategy.setDescriptorService((DescriptorService) descriptorServiceMock.proxy());
		entitySqueezeStrategy.setPersistenceService((PersistenceService) persistenceServiceMock.proxy());
		entitySqueezeStrategy.setPrefix("HIBRN8:");
		entitySqueezeStrategy.setDelimiter(":");

		foo.setId(identifier);
		foo.setName("FooFoo");
	}

	public void testSqueeze()
	{
		descriptorServiceMock.expects(once()).method("getClassDescriptor").with(eq(Foo.class))
			.will(returnValue(descriptor));

		persistenceServiceMock.expects(once()).method("getIdentifier").with(eq(foo), eq(descriptor))
			.will(returnValue(identifier));

		nextDataSqueezer.expects(once()).method("squeeze").with(eq(identifier)).will(returnValue("32456"));

		assertEquals("HIBRN8:org.tynamo.test.Foo:32456",
			entitySqueezeStrategy.squeeze(foo, (DataSqueezer) nextDataSqueezer.proxy()));

	}

	public void testUnSqueeze()
	{
		persistenceServiceMock.expects(once()).method("loadInstance").with(eq(Foo.class), eq(identifier))
			.will(returnValue(foo));

		nextDataSqueezer.expects(once()).method("unsqueeze").with(eq("32456")).will(returnValue(identifier));

		assertSame(foo, entitySqueezeStrategy.unsqueeze("HIBRN8:org.tynamo.test.Foo:32456",
			(DataSqueezer) nextDataSqueezer.proxy()));
	}

}
