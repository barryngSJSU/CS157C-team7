package museium;
import redis.clients.jedis.Jedis;

import java.security.Principal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class RestfulController {
	private Jedis jedis = new Jedis("localhost");
	
	@GetMapping("/testRedis")
	public String testMethod() {
		String s = "";
		Set<String> keys = jedis.keys("*");
		for (String key:keys) {
			s += key + "\n";
		}
		return s;
	}
	
	@PostMapping("/listing/create")
	public Map<String, String> createListing(@RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("price") String price, @RequestParam("image") String image, @RequestParam("email") String email, @RequestParam("tags") String tags) {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("Description", description);
		hash.put("Price", price);
		hash.put("Image", image);
		jedis.hset(title, "Description", description);
		jedis.hset(title, "Price", price);
		jedis.hset(title, "Image", image);
		jedis.hset(title, "Email", email);
		jedis.rpush("listings", title);
		jedis.rpush("listings:" + email, title);
		String rating = jedis.hget(email, "Rating");
		double score = 0;
		if (rating != null) {
			score = Double.parseDouble(rating);
		}
		String[] splitTags = tags.split(",");
		System.out.println(splitTags.length);
		for (int i = 0; i < splitTags.length; i++) {
			jedis.rpush("tags:"+ title, splitTags[i]);
			jedis.zadd("tag:" + splitTags[i], score, title);
		}
		
		jedis.save();
		return hash;
	}
	
	@GetMapping("/listing/list")
	public List<String> listListings() {
		List<String> listings = jedis.lrange("listings", 0, -1);
		return listings;
	}
	
	@GetMapping("/listing/{title}")
	public Map<String, String> getListing(@PathVariable String title) {
		Map<String, String> hash = jedis.hgetAll(title);
		return hash;
	}
	
	
	@RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Principal principal) {
		if (principal == null) {
			return "no user";
		}
		return principal.getName();
    }
	
	@GetMapping("/fullName{email}")
	public String listListings(@PathVariable String email) {
		String firstName = jedis.hget(email, "firstName");
		String lastName = jedis.hget(email, "lastName");
		return firstName + " " + lastName;
	}
	
	@PostMapping("/review/create")
	public Map<String, String> createReview(@RequestParam("title") String title, @RequestParam("rating") String rating, @RequestParam("reviewBody") String reviewBody, @RequestParam("email") String email, @RequestParam("reviewerEmail") String reviewerEmail ) {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("Rating", rating);
		hash.put("ReviewBody", reviewBody);
		hash.put("ReviewerEmail", reviewerEmail);
		jedis.hset(title, "Rating", rating);
		jedis.hset(title, "ReviewBody", reviewBody);
		jedis.hset(title, "ReviewerEmail", reviewerEmail);
		jedis.rpush("reviews:" + email, title);
		String totReview = jedis.hget(email, "Total Reviews");
		int totalReview = 0;
		if (totReview != null) {
			totalReview = Integer.parseInt(totReview);
		}
		String selRating = jedis.hget(email, "Rating");
		double sellerRating = 0;
		if (selRating != null) {
			sellerRating = Double.parseDouble(selRating);
		}
		double newRating = ((sellerRating*totalReview)+Double.parseDouble(rating))/(totalReview+1);
		jedis.hset(email, "Rating", Double.toString(newRating));
		jedis.hset(email, "Total Reviews", Integer.toString(totalReview + 1));
		jedis.zadd("sellerRatings", newRating, email);
		List<String> listings = jedis.lrange("listings:" + email, 0, -1);
		for (String s: listings) {
			List<String> tags = jedis.lrange("tags:" + s, 0, -1);
			for (String t: tags) {
				jedis.zadd("tag:" + t, newRating, s);
			}
		}
		
		jedis.save();
		return hash;
	}
	
	@GetMapping("/search{tag}")
	public List<String> search(@PathVariable String tag) {
		List<String> result = new ArrayList<String>(jedis.zrange("tag:"+ tag, 0, -1));
		Collections.reverse(result);
		return result;
	}
	
	@GetMapping("/tags{listing}")
	public String tags(@PathVariable String listing) {
		List<String> tags = jedis.lrange("tags:" + listing, 0, -1);
		String t = "";
		for (String tag: tags) {
			t += tag + ", ";
		}
		t = t.substring(0, t.length()- 2);
		return t;
	}
	
	@GetMapping("/seller{email}")
	public Map<String, String> seller(@PathVariable String email) {
		Map<String, String> seller = jedis.hgetAll(email);
		return seller;
	}
	
	@GetMapping("/reviews{email}")
	public List<String> reviews(@PathVariable String email) {
		List<String> reviews = jedis.lrange("reviews:" + email, 0, -1);
		return reviews;
	}
	
	@GetMapping("/review{title}")
	public Map<String, String> review(@PathVariable String title) {
		Map<String, String> review = jedis.hgetAll(title);
		return review;
	}
	
	@DeleteMapping("/listing/delete{title}")
    public void deleteListing(@PathVariable String title) {
		Map<String, String> listing = jedis.hgetAll(title);
		jedis.lrem("listings", 0, title);
		jedis.lrem("listings:" + listing.get("Email"), 0, title);
		List<String> tags = jedis.lrange("tags:" + title, 0, -1);
		jedis.del("tags:" + title);
		for (String t: tags) {
			jedis.zrem("tag:" + t, title);
		}
		jedis.del(title);
		jedis.save();
    }
	
	@PostMapping("/cart/create{title}")
	public void addToCart(@PathVariable String title, Principal principal){
		jedis.sadd("cart:" + principal.getName(), title);
	}
	
	@GetMapping("/cart{email}")
	public Set<String> getCart(@PathVariable String email){
		return jedis.smembers("cart:" + email);
	}
	
	@DeleteMapping("/cart/delete{title}")
	public void removeCart(@PathVariable String title, Principal principal) {
		jedis.srem("cart:" + principal.getName(), title);
	}

	@PutMapping("/checkout{user}")
	public void checkOut(Principal principal){
		jedis.smembers("cart:" + principal.getName());
		Set<String> cart = jedis.smembers("cart:" + principal.getName());
		for (String s:cart) {
			jedis.sadd("purchases:" + principal.getName(), s);
			jedis.lrem("listings", 0,s );
			jedis.lrem("listings:" + principal.getName(), 0, s);
			List<String> tags = jedis.lrange("tags:" + s, 0, -1);
			for (String t: tags) {
				jedis.zrem("tag:" + t, s);
			}
		}
		jedis.del("cart:" + principal.getName());
	}
	
	@GetMapping("/purchases")
	public Set<String> getCart(Principal principal){
		return jedis.smembers("purchases:" + principal.getName());
	}
}
