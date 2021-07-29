package museium;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import redis.clients.jedis.Jedis;


@Controller
public class AppController {
	private Jedis jedis = new Jedis("localhost");
	
	
	@RequestMapping("/")
    public String indexPage() {
        return "index";
    }
    @RequestMapping("/create")
    public String createPage() {
        return "createlisting";
    }
    
    @RequestMapping("/listings")
    public String listingsPage() {
        return "listings";
    }
    
    @RequestMapping("/listing")
    public String listingPage() {
        return "listing";
    }
    
    @GetMapping("/signup")
    public String signupPage(Model model) {
    	model.addAttribute("customer", new Customer());
    	return "signup";
    }
    
    @RequestMapping("/profile")
    public String profilePage() {
    	return "profile";
    }
    
    @RequestMapping("/seller")
    public String sellerPage() {
    	return "seller";
    }
    
    @RequestMapping("/cart")
    public String cartPage() {
    	return "cart";
    }
    
    @PostMapping("/register")
    public String processRegister(Customer user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        jedis.hset(user.getEmail(), "firstName", user.getFirstName());
        jedis.hset(user.getEmail(), "lastName", user.getLastName());
        jedis.hset(user.getEmail(), "password", user.getPassword());
        jedis.hset(user.getEmail(), "email", user.getEmail());
        jedis.hset(user.getEmail(), "address", user.getAddress());
        jedis.hset(user.getEmail(), "phone", user.getPhone());
        jedis.save();
        return "login";
    }
}