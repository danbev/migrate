package se.rl.deadlock;

import java.io.Serializable;
import java.util.Date;

public class CustomerImpl implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private Date[] dateArray;
    
    public CustomerImpl() {
    }
    
    public Date[] getDateArray() {
        return dateArray;
    }

    public void setDateArray(Date[] dateArray) {
        this.dateArray = dateArray;
    }
    
}
