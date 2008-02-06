package org.disco.bdd.money


scenario "two monies of the same currency are added", {
	
	given "one money object is added to another", {
		total = new Money(12, "CHF").add(new Money(14, "CHF"))
	}
	
	then "the total amount should be the sum of the two", {
		total.amount().shouldBe 26
	}
	
}

scenario "two monies of different currencies are added", {
	
}