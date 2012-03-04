package org.tynamo.services;

import org.apache.tapestry5.func.Predicate;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Match;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.PropertyAccess;
import org.apache.tapestry5.services.*;
import org.tynamo.VersionedModule;
import org.tynamo.bindings.ModelBindingFactory;
import org.tynamo.blob.BlobManager;
import org.tynamo.blob.DefaultBlobManager;
import org.tynamo.builder.BuilderDirector;
import org.tynamo.descriptor.TynamoClassDescriptor;
import org.tynamo.descriptor.TynamoPropertyDescriptor;
import org.tynamo.descriptor.annotation.handlers.*;
import org.tynamo.descriptor.decorators.DescriptorDecorator;
import org.tynamo.descriptor.decorators.TapestryDecorator;
import org.tynamo.descriptor.decorators.TynamoDecorator;
import org.tynamo.descriptor.factories.*;
import org.tynamo.internal.services.BeanModelSourceAdvice;
import org.tynamo.util.Pair;

public class TynamoCoreModule extends VersionedModule
{

	public final static String PROPERTY_DISPLAY_BLOCKS = "tynamo/PropertyDisplayBlocks";
	public final static String PROPERTY_EDIT_BLOCKS = "tynamo/PropertyEditBlocks";

	public static void bind(ServiceBinder binder)
	{
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.

		binder.bind(BuilderDirector.class, BuilderDirector.class);
		binder.bind(DescriptorFactory.class, ReflectionDescriptorFactory.class);
		binder.bind(PropertyDescriptorFactory.class, PropertyDescriptorFactoryImpl.class);
		binder.bind(MethodDescriptorFactory.class, MethodDescriptorFactoryImpl.class);
		binder.bind(EntityCoercerService.class, EntityCoercerServiceImpl.class);
		binder.bind(DescriptorService.class, DescriptorServiceImpl.class);
		binder.bind(TynamoDataTypeAnalyzer.class, TynamoDataTypeAnalyzer.class);

		binder.bind(BlobManager.class, DefaultBlobManager.class).withId("DefaultBlobManager");

		binder.bind(DescriptorAnnotationHandler.class, BeanModelAnnotationHandler.class).withId("BeanModelAnnotationHandler");
		binder.bind(DescriptorAnnotationHandler.class, BlobDescriptorAnnotationHandler.class).withId("BlobDescriptorAnnotationHandler");
		binder.bind(DescriptorAnnotationHandler.class, ClassDescriptorAnnotationHandler.class).withId("ClassDescriptorAnnotationHandler");
		binder.bind(DescriptorAnnotationHandler.class, CollectionDescriptorAnnotationHandler.class).withId("CollectionDescriptorAnnotationHandler");
		binder.bind(DescriptorAnnotationHandler.class, MethodDescriptorAnnotationHandler.class).withId("MethodDescriptorAnnotationHandler");
		binder.bind(DescriptorAnnotationHandler.class, PropertyDescriptorAnnotationHandler.class).withId("PropertyDescriptorAnnotationHandler");

	}

	@Match("BeanModelSource")
	public static void adviseBeanModelSource(MethodAdviceReceiver receiver,
	                                         @Autobuild BeanModelSourceAdvice advice)
	{
		receiver.adviseAllMethods(advice);
	}

	/**
	 * Add our components and pages to the "Tynamo" library.
	 */
	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration)
	{
		configuration.add(new LibraryMapping("tynamo", "org.tynamo"));
	}

	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration)
	{
		configuration.add("tynamo-" + version, "org/tynamo");
	}


	/**
	 * Contribution to the BeanBlockSource service to tell the BeanEditForm component about the editors.
	 */
	public static void contributeBeanBlockSource(Configuration<BeanBlockContribution> configuration)
	{
		configuration.add(new EditBlockContribution("nonVisual", PROPERTY_EDIT_BLOCKS, "nonVisual"));
		configuration.add(new EditBlockContribution("formatted-date", PROPERTY_EDIT_BLOCKS, "date"));
		configuration.add(new EditBlockContribution("fckEditor", PROPERTY_EDIT_BLOCKS, "fckEditor"));

		configuration.add(new EditBlockContribution("readOnly", PROPERTY_EDIT_BLOCKS, "readOnly"));
		configuration.add(new EditBlockContribution("single-valued-association", PROPERTY_EDIT_BLOCKS, "select"));
		configuration.add(new EditBlockContribution("identifierEditor", PROPERTY_EDIT_BLOCKS, "identifierEditor"));
		configuration.add(new EditBlockContribution("many-valued-association", PROPERTY_EDIT_BLOCKS, "palette"));
		configuration.add(new EditBlockContribution("composition", PROPERTY_EDIT_BLOCKS, "editComposition"));
		configuration.add(new EditBlockContribution("embedded", PROPERTY_EDIT_BLOCKS, "embedded"));
		configuration.add(new EditBlockContribution("blob", PROPERTY_EDIT_BLOCKS, "blob"));

		configuration.add(new DisplayBlockContribution("nonVisual", PROPERTY_DISPLAY_BLOCKS, "nonVisual"));
		configuration.add(new DisplayBlockContribution("formatted-date", PROPERTY_DISPLAY_BLOCKS, "date"));
		configuration.add(new DisplayBlockContribution("number", PROPERTY_DISPLAY_BLOCKS, "number"));

		configuration.add(new DisplayBlockContribution("composition", PROPERTY_DISPLAY_BLOCKS, "composition"));
		configuration.add(new DisplayBlockContribution("blob", PROPERTY_DISPLAY_BLOCKS, "download"));
	}

	public static void contributeBeanBlockOverrideSource(Configuration<BeanBlockContribution> configuration)
	{
		configuration.add(new EditBlockContribution("enum", PROPERTY_EDIT_BLOCKS, "select"));
	}

	public static void contributeDataTypeAnalyzer(OrderedConfiguration<DataTypeAnalyzer> configuration,
	                                              @InjectService("TynamoDataTypeAnalyzer")
	                                              DataTypeAnalyzer tynamoDataTypeAnalyzer)
	{
		configuration.add("Tynamo", tynamoDataTypeAnalyzer, "before:Default");
	}

	/**
	 * Contributes a set of standard type coercions to the {@link org.apache.tapestry5.ioc.services.TypeCoercer} service:
	 * <ul>
	 * <li>Class to String</li>
	 * <li>String to Double</li>
	 * </ul>
	 */
	@SuppressWarnings("unchecked")
	public static void contributeTypeCoercer(final Configuration<CoercionTuple> configuration,
	                                         @InjectService("EntityCoercerService")
	                                         EntityCoercerService entityCoercerService)
	{
		configuration.add(CoercionTuple.create(Class.class, String.class, new ClassToStringCoercion(entityCoercerService)));
		configuration.add(CoercionTuple.create(String.class, Class.class, new StringToClassCoercion(entityCoercerService)));
	}

	public static void contributeDescriptorFactory(OrderedConfiguration<DescriptorDecorator> configuration,
	                                               PropertyAccess propertyAccess, ObjectLocator locator)
	{
		configuration.add("TynamoDecorator", new TynamoDecorator(locator));
		configuration.add("TapestryDecorator", new TapestryDecorator(propertyAccess));
	}

	public static void contributeTynamoDataTypeAnalyzer(OrderedConfiguration<Pair> configuration)
	{
		configuration.add("nonVisual", newPair(TynamoDataTypeAnalyzerPredicates.nonVisual, "nonVisual"));
		configuration.add("readOnly", newPair(TynamoDataTypeAnalyzerPredicates.readOnly, "readOnly"));
		configuration.add("password", newPair(TynamoDataTypeAnalyzerPredicates.password, "password"));
		configuration.add("longtext", newPair(TynamoDataTypeAnalyzerPredicates.longtext, "longtext"));
		configuration.add("generatedId", newPair(TynamoDataTypeAnalyzerPredicates.generatedId, "readOnly"));
		configuration.add("assignedId", newPair(TynamoDataTypeAnalyzerPredicates.assignedId, "identifierEditor"));
//		configuration.add("", newPair("identifier && objectReference", "objectReferenceIdentifierEditor");
		configuration.add("enum", newPair(TynamoDataTypeAnalyzerPredicates.enumi, "enum")); // overrides Tapestry's enum
		configuration.add("blob", newPair(TynamoDataTypeAnalyzerPredicates.blob, "blob"));
		configuration.add("manyToOne", newPair(TynamoDataTypeAnalyzerPredicates.manyToOne, "single-valued-association"));
		configuration.add("manyToMany", newPair(TynamoDataTypeAnalyzerPredicates.manyToMany, "many-valued-association"));
		configuration.add("composition", newPair(TynamoDataTypeAnalyzerPredicates.composition, "composition"));
		configuration.add("embedded", newPair(TynamoDataTypeAnalyzerPredicates.embedded, "embedded"));
	}

	private static Pair<Predicate<TynamoPropertyDescriptor>, String> newPair(Predicate<TynamoPropertyDescriptor> predicate, String value)
	{
		return new Pair<Predicate<TynamoPropertyDescriptor>, String>(predicate, value);
	}

	public static void contributePropertyDescriptorFactory(Configuration<String> configuration)
	{
		configuration.add("exclude.*");
		configuration.add("class");
	}

	public static void contributeMethodDescriptorFactory(Configuration<String> configuration)
	{
		configuration.add("shouldExclude");
		configuration.add("set.*");
		configuration.add("get.*");
		configuration.add("is.*");
		configuration.add("equals");
		configuration.add("wait");
		configuration.add("toString");
		configuration.add("notify.*");
		configuration.add("hashCode");
	}

	@Contribute(EntityCoercerService.class)
	public static void contributeEntityCoercerService(MappedConfiguration<String, Class> configuration,
	                                                  DescriptorService descriptorService)
	{
		for (TynamoClassDescriptor classDescriptor : descriptorService.getAllDescriptors())
		{
			Class clazz = classDescriptor.getBeanType();
			String simpleName = clazz.getSimpleName().toLowerCase();
			configuration.add(simpleName, clazz);
		}
	}

	@Contribute(BindingSource.class)
	public void contributeBindingSource(MappedConfiguration<String, BindingFactory> configuration,
	                                    @Autobuild ModelBindingFactory modelBindingFactory)
	{
		configuration.add("mb", modelBindingFactory);
	}

}
