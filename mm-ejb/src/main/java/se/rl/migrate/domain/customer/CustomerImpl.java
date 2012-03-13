package se.rl.migrate.domain.customer;

import java.io.Serializable;

import rl.domain.Id;

public class CustomerImpl implements Serializable {
    
    private Id id;
    
    public CustomerImpl(final Id id) {
        this.id = id;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }
    
}
