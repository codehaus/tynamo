package org.trails.demo;

import javax.persistence.*;

import org.trails.descriptor.annotation.ClassDescriptor;

@Entity
public class Person
{

    private Integer id;
    
    private String name;
    
    public Person()
    {
        super();
        // TODO Auto-generated constructor stub
    }
    
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
