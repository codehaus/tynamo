package org.tynamo.seedentity.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "THING_ID", "order_no" }))
public class PrettyUniqueThing
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	private Thing thing;

	@Column(name="order_no")
	private int order;

	public PrettyUniqueThing() {

	}

	public PrettyUniqueThing(Thing thing, int order) {
		this.thing = thing;
		this.order = order;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Thing getThing() {
		return thing;
	}

	public void setThing(Thing thing) {
		this.thing = thing;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
