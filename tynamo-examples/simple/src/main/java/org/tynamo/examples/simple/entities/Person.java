package org.tynamo.examples.simple.entities;

import org.tynamo.blob.TynamoBlobImpl;
import org.tynamo.descriptor.annotation.BlobDescriptor;
import org.tynamo.descriptor.annotation.PropertyDescriptor;
import org.tynamo.descriptor.extension.BlobDescriptorExtension;
import org.tynamo.descriptor.extension.TynamoBlob;

import javax.persistence.*;

@Entity
public class Person
{

	private Integer id;

	private String firstName;

	private String lastName;

	private TynamoBlob photo = new TynamoBlobImpl();

	private Car car;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	private Address address = new Address();

	@Embedded
	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	@OneToOne(mappedBy = "owner")
	public Car getCar()
	{
		return car;
	}

	public void setCar(Car car)
	{
		this.car = car;
	}


	/**
	 * Some database engines - for example, MySQL - have different BLOB types for different data sizes.
	 * (TINYBLOB, MEDIUMBLOB, LARGEBLOB)
	 * <p/>
	 * The actual BLOB type used by Hibernate is thus dependent upon the column length, but the default column length of
	 * 255 is often too small to acommodate typical BLOB data.
	 * <p/>
	 * Therefore you'll need to add a Column.length annotation to the property specifying the maximum possible size of the
	 * BLOB data.
	 *
	 * @return
	 */
	@BlobDescriptor(renderType = BlobDescriptorExtension.RenderType.LINK)
	@Lob
	@Column(length = 1048576)  // Use 1Mb maximum length. (MEDIUMBLOB in MySQL.)
	public TynamoBlob getPhoto()
	{
		return photo;
	}

	public void setPhoto(TynamoBlob photo)
	{
		this.photo = photo;
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Person person = (Person) o;

		return getId() != null ? getId().equals(person.getId()) : person.getId() == null;
	}

	public int hashCode()
	{
		return (getId() != null ? getId().hashCode() : 0);
	}
}
