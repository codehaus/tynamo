package org.trails.io;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import org.apache.tapestry.util.io.DataSqueezerImpl;
import org.apache.tapestry.util.io.DataSqueezerUtil;


public class SqueezerDataEncoderTest extends TestCase
{

	SqueezerDataEncoder squeezerDataEncoder = new SqueezerDataEncoder();
	List lista = new ArrayList();

	protected void setUp() throws Exception
	{
		super.setUp();
		DataSqueezerImpl dataSqueezer = DataSqueezerUtil.createUnitTestSqueezer();
		dataSqueezer.register(new ClassAdaptor());
		squeezerDataEncoder.setDataSqueezer(dataSqueezer);


		lista.clear();
		lista.add(1);
		lista.add("alejandro");
		lista.add(SqueezerDataEncoder.class);
		lista.add(true);
	}


	public void testEncodePageChanges()
	{
		assertEquals("{1, Salejandro, Dorg.trails.io.SqueezerDataEncoder, T}",
			squeezerDataEncoder.encodePageChanges(lista));

	}

	public void testDos()
	{
//		lista.add(new Bar());
		System.out.println(squeezerDataEncoder.encodePageChanges(lista));
	}

	public void testTres()
	{
		List nuevos = squeezerDataEncoder.decodePageChanges("{1, Salejandro, Dorg.trails.io.SqueezerDataEncoder, T}");

		assertEquals(lista.size(), nuevos.size());

		for (int i = 0; i < nuevos.size(); i++)
		{
			assertEquals(lista.get(i), nuevos.get(i));
		}
	}
}
