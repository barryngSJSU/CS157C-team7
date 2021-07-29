package museium;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import redis.clients.jedis.Jedis;
 
public class CustomerDetailService implements UserDetailsService {
    
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	Jedis jedis = new Jedis("localhost");
        Map<String, String> user = jedis.hgetAll(email);
        if (user.keySet().size() == 0) {
        	jedis.close();
            throw new UsernameNotFoundException("User not found");
        }
        Customer c = new Customer(user.get("fisrtName"), user.get("lastName"), user.get("email"), user.get("password"), user.get("phone"), user.get("address"));
        jedis.close();
        return new CustomerDetails(c);
    }
 
}
