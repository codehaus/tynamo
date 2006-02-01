package org.trails.demo;

import org.hibernate.lucene.Indexed;
import org.hibernate.lucene.Keyword;
import org.hibernate.lucene.Text;
import org.hibernate.lucene.Unstored;
import org.hibernate.validator.Length;
import org.trails.descriptor.annotation.PropertyDescriptor;

import javax.persistence.*;

@Entity
@Indexed(index = "indexedPojo")
public class IndexedPojo
{

    private Integer id;
    private String name;
    private String description;
    private Double value;
    private Double value2;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @PropertyDescriptor(index = 0)
    @Keyword(id = true, name = "Id")
    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    @Unstored
    @PropertyDescriptor(index = 1)
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Length(max = 250)
    @Text(name = "description")
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String toString()
    {
        return name;
    }

    public boolean equals(Object o)
    {

        if (this == o) return true;

        try
        {

            final IndexedPojo indexedPojo = (IndexedPojo) o;
            if (getId() != null ? !getId().equals(indexedPojo.getId()) : indexedPojo.getId() != null) return false;
            return true;

        } catch (Exception e)
        {
            return false;
        }
    }

    @PropertyDescriptor(format = "0.000")
    public Double getValue()
    {
        return value;
    }

    public void setValue(Double value)
    {
        this.value = value;
    }

    @PropertyDescriptor(format = "0.000")
    @Keyword
    public Double getValue2()
    {
        return value2;
    }

    public void setValue2(Double value2)
    {
        this.value2 = value2;
    }
}
