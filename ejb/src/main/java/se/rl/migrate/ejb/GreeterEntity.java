package se.rl.migrate.ejb;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class GreeterEntity {
    
    @Id
    private String name;
    
    public GreeterEntity() {
    }
    
    public GreeterEntity(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GreeterEntity other = (GreeterEntity) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
}
