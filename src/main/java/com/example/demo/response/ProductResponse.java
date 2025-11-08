package com.example.demo.response;

import java.math.BigDecimal;

public class ProductResponse {
	
	 private final Data data;

	    public ProductResponse(Long id, String name, BigDecimal price, String description) {
	        this.data = new Data(id, name, price, description);
	    }

	    public Data getData() {
	        return data;
	    }

	    public static class Data {
	        private final String type = "product";
	        private final Attributes attributes;

	        public Data(Long id, String name, BigDecimal price, String description) {
	            this.attributes = new Attributes(id, name, price, description);
	        }

	        public String getType() {
	            return type;
	        }

	        public Attributes getAttributes() {
	            return attributes;
	        }
	    }

	    public static class Attributes {
	        private final Long id;
	        private final String name;
	        private final BigDecimal price;
	        private final String description;

	        public Attributes(Long id, String name, BigDecimal price, String description) {
	            this.id = id;
	            this.name = name;
	            this.price = price;
	            this.description = description;
	        }

	        public Long getId() { return id; }
	        public String getName() { return name; }
	        public BigDecimal getPrice() { return price; }
	        public String getDescription() { return description; }
	    }

}
