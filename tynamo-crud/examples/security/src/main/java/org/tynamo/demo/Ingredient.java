package org.tynamo.demo;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.tynamo.descriptor.annotation.ClassDescriptor;
import org.tynamo.descriptor.annotation.PropertyDescriptor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@ClassDescriptor(hidden = true)
public class Ingredient
{

    private Integer id;

    private String amount;

    private String name;

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    @PropertyDescriptor(index=0)
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public String toString()
    {
        return getAmount() + " " + getName();
    }
}
