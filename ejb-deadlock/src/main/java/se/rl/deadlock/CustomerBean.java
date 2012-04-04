package se.rl.deadlock;

import java.util.Date;
import javax.ejb.Stateless;

@Stateless 
public class CustomerBean implements CustomerLocal, CustomerRemote {
    
    public CustomerImpl getCustomer(int size) {
        System.out.println("Getting customer...");
		final CustomerImpl customerImpl = new CustomerImpl();
        customerImpl.setDateArray(createLargeArray(size));
		return customerImpl;
    }
    
    private Date[] createLargeArray(final int size) {
        final Date[] dates = new Date[size];
        for (int i = 0 ; i < size; i++) {
            dates[i] = new Date();
        }
        return dates;
    }
    
}
