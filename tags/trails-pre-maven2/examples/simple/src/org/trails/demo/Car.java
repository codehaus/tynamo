package org.trails.demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

@Entity
public class Car
{

    public enum Origin
    {
        AFRICA,AMERICA,ASIA,EUROPE,OCEANIA
    }

    private Origin origin = Origin.ASIA;

    public Car()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    private Integer id;
    
    private String name;

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
    
    private Make make;
    
    private Model model;

    @ManyToOne
    public Make getMake()
    {
        return make;
    }

    public void setMake(Make make)
    {
        this.make = make;
    }

    @ManyToOne
    public Model getModel()
    {
        return model;
    }

    public void setModel(Model model)
    {
        this.model = model;
    }

    @Enumerated(value = EnumType.STRING)
    public Origin getOrigin()
    {
        return origin;
    }

    public void setOrigin(Origin origen)
    {
        this.origin = origen;
    }
    
    
}
